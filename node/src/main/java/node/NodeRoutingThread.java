package node;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ntuaece.nikosapos.SimulationParameters;
import com.ntuaece.nikosapos.entities.Packet;

import services.IcasResponsible;
import services.NeighborResponsible;

public class NodeRoutingThread extends Thread implements PacketReceiver {

    private volatile Thread thisThread;
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

    private int droppedCounter = 0;

    public NodeRoutingThread(Node node, NeighborResponsible nService, IcasResponsible iService) {
        super("Node routing " + node.getId());
        this.node = node;
        this.icasService = iService;
        this.timer = new Timer(node + " packet generator");
        this.router = new RouterImpl(node, nService);
        this.recorder = new NeighborStatsRecorderImpl(node);
        this.randomGenerator = new Random();
        this.thisThread = this;
        node.getNeighbors().stream().forEach(neighbor -> {
            idToLink.put(neighbor.getLink().getId(), neighbor.getLink());
            neighbor.getLink().setPacketReceiver(node, this);
        });
        if (node.getId() <= 50) setTimer();
    }

    public void addNeighbor(Neighbor neighbor) {
        synchronized (idToLink) {
            idToLink.put(neighbor.getLink().getId(), neighbor.getLink());
            neighbor.getLink().setPacketReceiver(node, this);
        }
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
        if (packet.isSemiAck()) {
            recorder.recordPacket(packet);
            return;
        }
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
                if (nextNode != null) {
                    recorder.recordPacket(packet);
                }
            }
        } else {
            if (packet.getDestinationNodeID() == node.getId()) {
                packet.setAck(true);
                node.incrementReceivedPacketCounter();
                icasService.confirmSuccessfulDelivery(packet);
                nextNode = router.routePacket(packet);
                if (nextNode != null) {
                    recorder.recordPacket(packet);
                }
            } else {
                if (dropBecauseAmCheater(packet)) {
                    System.out.println(packet + " dropped by cheater " + node + " " + packet.getPathlist());
                    dropPacket(packet);
                    return;
                } else if (dropBecauseComingFromSelfishNode(packet)) {
                    System.out.println(packet + " dropped by " + node + " " + packet.getPathlist());
                    dropPacket(packet);
                    return;
                } else if (packet.getHopsRemaining() == Integer.MIN_VALUE) {
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

        if (nextNode != null && nextNode.getLink() != null) {
            nextNode.getLink().addPacketToUpLink(node, packet);
            if (!packet.isAck()) {
                sendSemiAck(packet);
            }
        } else packet.drop();
    }

    private boolean dropBecauseComingFromSelfishNode(Packet packet) {
        if (packet.getSourceNodeID() != node.getId()) {
            int previousNodeIndex = packet.getPathlist().size() - 2;
            long previousNode = packet.getPathlist().get(previousNodeIndex);
            if (node.existsInSelfishNodeList(previousNode)) {
                if (node.findNeighborById(previousNode)
                        .get()
                        .getDarwinI() >= randomGenerator.nextDouble()) { return true; }
            }
        }
        return false;
    }

    private void sendSemiAck(Packet packet) {
        if (packet.getSourceNodeID() == this.node.getId()) return;

        int previousNodeIndex = packet.getPathlist().size() - 3;
        long previousNodeId = packet.getPathlist().get(previousNodeIndex);
        Optional<Neighbor> previousNode = node.findNeighborById(previousNodeId);
        if (previousNode.isPresent()) {
            Packet semiAck = new Packet();
            semiAck.setSemiAck(true);
            semiAck.setMe(this.node.getId());
            previousNode.get().getLink().addPacketToUpLink(node, semiAck);
        }

    }

    private void dropPacket(Packet packet) {
        packet.drop();
        node.incrementDroppedPacketCounter();
    }

    private boolean dropBecauseAmCheater(Packet p) {
        if (!node.isCheater() || p.getSourceNodeID() == node.getId()) return false;
        return (++droppedCounter) % 10 != 0
                && randomGenerator.nextDouble() <= SimulationParameters.CHEATING_PROBABILITY;
    }

    private boolean icasPermits(long id) {
        return icasService.askForSendPermission(id);
    }

    private void setTimer() {
        timer.scheduleAtFixedRate(new TimerTask() {

            int i = 0;

            @Override
            public void run() {
                int size = node.getDestinationList().size();
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
