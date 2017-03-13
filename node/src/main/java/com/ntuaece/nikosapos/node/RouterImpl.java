package com.ntuaece.nikosapos.node;

import java.util.Optional;

import com.ntuaece.nikosapos.entities.Packet;

public class RouterImpl implements Router {

    private final Node node;

    public RouterImpl(Node node) {
        this.node = node;
    }

    @Override
    public Neighbor routePacket(Packet packet) {
        Neighbor nextNode = null;
        
        if (packet.getSourceNodeID() == node.getId()) {
            packet.addPathlist(node.getId());
        }
        
        
        if (!packet.isAck()) {
            nextNode = findNextNeighbor(packet);
            packet.addPathlist(nextNode.getId());
            packet.decrementTTL();
        } else {
            // ACK
            int nextNeighborIndex = -1;

            for (int i = 0; i < packet.getPathlist().size(); i++) {
                long pathNode = packet.getPathlist().get(i);
                if (pathNode == node.getId()) {
                    nextNeighborIndex = i - 1;
                    break;
                }
            }

            if (nextNeighborIndex > packet.getPathlist().size()) { throw new RuntimeException("Index too high!"); }

            // next neighbor for an ack can be retrieved from the path list
            long nextNeighborID = packet.getPathlist().get(nextNeighborIndex);
            Optional<Neighbor> neighbor = node.findNeighborById(nextNeighborID);

            if (neighbor.isPresent()) {
                nextNode = neighbor.get();
            } else throw new RuntimeException("Ack " + packet.getId() + " Node " + node.getId()
                    + " next neighbor cannot be null!");
        }

        return nextNode;
    }

    private Neighbor findNextNeighbor(Packet p) {
        // TODO: must implement the full algorithm here
        return node.getNeighbors().get(0);
    }

}
