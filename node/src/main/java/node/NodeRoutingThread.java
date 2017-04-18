package node;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ntuaece.nikosapos.entities.Packet;

import services.IcasResponsible;
import services.NeighborResponsible;

public class NodeRoutingThread extends Thread implements PacketReceiver {

    private final Node node;
    private final IcasResponsible icasService;
    private final Router router;
    private final NeighborStatsRecorder recorder;

    private Map<Long, Link> idToLink = new HashMap<>();
    private Queue<Long> incomingPacketQueue = new ConcurrentLinkedQueue<>();
    private long nextPacketDestination = 0;
    private boolean canSend = false;
    private Timer timer;

    private Random randomGenerator;

    public NodeRoutingThread(Node node, NeighborResponsible nService, IcasResponsible iService) {
        super("Node routing " + node.getId());
        this.node = node;
        this.icasService = iService;
        this.timer = new Timer(node + " packet generator");
        this.router = new RouterImpl(node, nService);
        this.recorder = new NeighborStatsRecorderImpl(node);
        node.getNeighbors().stream().forEach(neighbor -> {
            idToLink.put(neighbor.getLink().getId(), neighbor.getLink());
            neighbor.getLink().setPacketReceiver(node, this);
        });
        randomGenerator = new Random();
        thisThread = this;
        if (node.getId() <= 50) setTimer();
    }

    private void checkLinks() {
        while (incomingPacketQueue.size() > 0) {
            long linkId = incomingPacketQueue.poll();
            Optional<Packet> maybePacket = idToLink.get(linkId).removePacketFromDownLink(node);
            if (maybePacket.isPresent()) {
                Packet packet = maybePacket.get();
                managePacket(packet);
            } else {
                throw new RuntimeException(node + " packet from " + linkId + " should not be null");
            }
        }
    }

    private void sendNewPacket() {
        if (!icasPermits(nextPacketDestination)) return;
        Packet newPacket = new Packet.Builder().setSourceNodeId(node.getId())
                                               .setDestinationNodeId(nextPacketDestination)
                                               .setData((byte) 0x04)
                                               .build();
        managePacket(newPacket);
    }

    private void managePacket(Packet packet) {
        Neighbor nextNode = null;
        if (packet.isAck()) {
            // if it is the source of a packet sent
            if (packet.getSourceNodeID() == this.node.getId()) {
                Packet.incrementDeliveredPackets();
                recorder.recordPacket(packet);
                packet.drop();
                return;
            } else {
                nextNode = router.routePacket(packet);
                recorder.recordPacket(packet);
            }
        } else {
            if (packet.getDestinationNodeID() == node.getId()) {
                packet.setAck(true);
                icasService.confirmSuccessfulDelivery(packet);
                nextNode = router.routePacket(packet);
                recorder.recordPacket(packet);
            } else {
                if (dropBecauseAmCheater(packet)) {
                    System.out.println(packet + " dropped by cheater " + node + " " + packet.getPathlist());
                    dropPacket(packet);
                    return;
                } else if (packet.getHopsRemaining() == 0) {
                    System.out.println(packet + " dropped for hops by " + node + " " + packet.getPathlist());
                    dropPacket(packet);
                    return;
                } else {
                    // send to next neighbor
                    nextNode = router.routePacket(packet);
                    if (nextNode != null) {
                        recorder.recordPacket(packet);
                    }
                }
            }
        }

        if (nextNode != null) {
            nextNode.getLink().addPacketToUpLink(node, packet);
        } else {
            System.out.println(packet + " dropped by " + node + " " + packet.getPathlist());
            packet.drop();
            // Packet.incrementDroppedPackets();
        }
    }

    private void dropPacket(Packet packet) {
        // packet.addPathlist(node.getId());
        // recorder.recordPacket(packet);
        packet.drop();
        Packet.incrementDroppedPackets();
    }

    private boolean dropBecauseAmCheater(Packet p) {
        return node.isCheater() && p.getSourceNodeID() != node.getId();
    }

    private boolean icasPermits(long id) {
        try {
            return icasService.askForSendPermission(id);
        } catch (Exception e) {
            return true;
        }
    }

    private void setTimer() {
        timer.scheduleAtFixedRate(new TimerTask() {

            final int size = node.getDestinationList().size();
            int i = 0;

            @Override
            public void run() {
                do {
                    i++;
                    i %= size;
                    nextPacketDestination = node.getDestinationList().get(i);
                } while (nextPacketDestination == node.getId());
                canSend = true;
                if (!NodeRoutingThread.this.isInterrupted()) {
                    NodeRoutingThread.this.interrupt();
                }
            }
        }, NodeScheduledTask.PACKET_SENT_INITIAL_DELAY, NodeScheduledTask.PACKET_SENT_PERIOD);
    }

    private volatile Thread thisThread;

    @Override
    public void run() {
        Thread blinker = Thread.currentThread();

        while (blinker == thisThread) {
            try {
                sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                checkLinks();
                if (canSend) {
                    canSend = false;
                    sendNewPacket();
                }
            }
        }
    }

    @Override
    public void onPacketReceived(Long id) {
        incomingPacketQueue.offer(id);
        if (!isInterrupted()) interrupt();
    }

    public void stopThread() {
        thisThread = null;
        timer.cancel();
    }
}
