package com.ntuaece.nikosapos.node;

import java.util.Optional;

import com.ntuaece.nikosapos.entities.Packet;

public class NeighborStatsRecorderImpl implements NeighborStatsRecorder {

    private final Node node;

    public NeighborStatsRecorderImpl(Node node) {
        this.node = node;
    }

    @Override
    public void recordPacket(Packet packet) {
        // if packet has reached destination no action should be done
        if (packet.getDestinationNodeID() != node.getId()) {

            int myNodeIndex = 0;
            for (int i = 0; i < packet.getPathlist().size(); i++) {
                if (packet.getPathlist().get(i) == node.getId()) {
                    myNodeIndex = i;
                    break;
                }
            }
            int neighborIndex = myNodeIndex + 1;
            if (neighborIndex >= packet.getPathlist().size()) { throw new RuntimeException("HERE FOR NODE "
                    + node.getId() + " and packet " + packet.getId() + " pathlisth: " + packet.getPathlist()); }
            Optional<Neighbor> neighbor = node.findNeighborById(packet.getPathlist().get(neighborIndex));
            if (neighbor.isPresent()) {
                if (packet.isAck()) {
                    // packet not sent for forwarding so no counter should be
                    // incremented
                    if (neighbor.get().getId() != packet.getDestinationNodeID()) {
                        neighbor.get().incrementForwardedPacketCounter();
                    }
                    if (packet.getSourceNodeID() == node.getId()) {
                        node.incrementForwardedPacketCounter();
                    }
                } else {

                    // packet sent to final destination --> not incrementing
                    // sent for forwarding counter
                    if (neighbor.get().getId() != packet.getDestinationNodeID()) {
                        neighbor.get().incrementSentPacketCounter();
                    }
                    
                    if (packet.getSourceNodeID() != node.getId()) {
                        node.incrementRelayedPacketCounter();
                    } else {
                        node.incrementSentPacketCounter();
                    }
                }
            }
        }
    }

}
