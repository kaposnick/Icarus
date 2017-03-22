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
                    + node.getId() + " and packet " + packet.getId()); }
            Optional<Neighbor> neighbor = node.findNeighborById(packet.getPathlist().get(neighborIndex));
            if (neighbor.isPresent()) {
                if (packet.isAck()) {
                    neighbor.get().incrementForwardedPacketCounter();
                } else {
                    neighbor.get().incrementSentPacketCounter();
                }
            }
        }
    }

}
