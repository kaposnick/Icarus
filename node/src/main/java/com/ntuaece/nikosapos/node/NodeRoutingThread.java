package com.ntuaece.nikosapos.node;

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
    private final NeighborResponsible neighborService;
    private final IcasResponsible icasService;
    private final Router router;
    private final NeighborStatsRecorder recorder;

    private Map<Long, Link> idToLink = new HashMap<>();
    private Queue<Long> incomingPacketQueue = new ConcurrentLinkedQueue<>();
    private int nextPacketDestination = 0;
    private boolean canSend = false;

    public NodeRoutingThread(Node node, NeighborResponsible nService, IcasResponsible iService) {
        super("Node routing " + node.getId());
        this.node = node;
        this.neighborService = nService;
        this.icasService = iService;
        this.router = new RouterImpl(node);
        this.recorder = new NeighborStatsRecorderImpl(node);
        node.getNeighbors().stream().forEach(neighbor -> {
            idToLink.put(neighbor.getLink().getId(), neighbor.getLink());
            neighbor.getLink().setPacketReceiver(node, this);
        });
//        if (node.getId() == 0) setTimer();
        setTimer();
    }

    private void checkLinks() {
        while (incomingPacketQueue.size() > 0) {
            long linkId = incomingPacketQueue.poll();
            Optional<Packet> maybePacket = idToLink.get(linkId).removePacketFromDownLink(node);
            if (maybePacket.isPresent()) {
                Packet packet = maybePacket.get();
                System.out.println("Node " + node.getId() + " received packet " + packet.getId());
                managePacket(packet);
            }
        }
    }

    private void sendNewPacket() {
        if (!icasPermits(nextPacketDestination)) return;
        Packet newPacket = new Packet.Builder().setSourceNodeId(node.getId())
                                               .setDestinationNodeId(nextPacketDestination)
                                               .setData((byte) 0x04)
                                               .build();
        System.out.println("Node " + node.getId() + " sending new packet " + newPacket.getId() + " to "
                + nextPacketDestination);
        managePacket(newPacket);
    }

    private void managePacket(Packet packet) {
        Neighbor nextNode = null;
        if (packet.isAck()) {
            // if it is the source of a packet sent
            if (packet.getSourceNodeID() == node.getId()) {
                System.out.println("Packet " + packet.getId() + " has reached source " + node.getId());
                recorder.recordPacket(packet);
                packet.drop();
                return;
            } else {
                nextNode = router.routePacket(packet);
                recorder.recordPacket(packet);
            }
        } else {
            if (packet.getDestinationNodeID() == node.getId()) {
                System.out.println("Packet " + packet.getId() + " has reached destination " + node.getId());
                packet.setAck(true);
                // inform icas
                //TODO: we have to inform icas here for the arrival of the packet
                nextNode = router.routePacket(packet);
                recorder.recordPacket(packet);
            } else {
                if (hasToDrop() || packet.getHopsRemaining() == 0) {
                    recorder.recordPacket(packet);
                    packet.drop();
                    return;
                } else {
                    // send to next neighbor
                    nextNode = router.routePacket(packet);
                    recorder.recordPacket(packet);
                }
            }
        }

        if (nextNode != null) {
            nextNode.getLink().addPacketToUpLink(node, packet);
        } else {
            System.out.println("Packet " + packet.getId() + " dropped by " + node.getId());
            packet.drop();
        }
    }

    private boolean hasToDrop() {
        // TODO:
        return false;
    }

    private boolean icasPermits(long id) {
//        return icasService.askForSendPermission(id);
        // TODO: 
        return true;
    }

    private void setTimer() {
        Timer timer = new Timer("Node " + node.getId() + " packet generator");
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                // choose a random node (maybe fixed)
                do {
                    nextPacketDestination = new Random().nextInt(NodePosition.x.length);
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
        while (true) {
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

}
