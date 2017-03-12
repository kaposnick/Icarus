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
        if (!packet.isAck()) {
            nextNode = findNextNeighbor(packet);
            packet.addPathlist(node.getId());
            packet.decrementTTL();
        } else {
            // ACK 
            int nextNeighborIndex;
            for(nextNeighborIndex = 0; nextNeighborIndex < packet.getPathlist().size(); nextNeighborIndex++){
                long pathNode = packet.getPathlist().get(nextNeighborIndex);
                if (pathNode == node.getId()) break;
            }
            
            if (nextNeighborIndex > packet.getPathlist().size()) {
                throw new  RuntimeException("Index too high!");
            }

            // next neighbor for an ack can be retrieved from the path list
            long nextNeighborID = packet.getPathlist().get(nextNeighborIndex - 1);
            Optional<Neighbor> neighbor = node.findNeighborById(nextNeighborID);

            if (neighbor.isPresent()) {
                nextNode = neighbor.get();
            } else throw new RuntimeException("Ack " + packet.getId() + " Node " + node.getId()
                    + " next neighbor cannot be null!");
        }

        return nextNode;
    }

    private Neighbor findNextNeighbor(Packet p) {
        //TODO: must implement the full algorithm here
        return node.getNeighbors().get(0);
    }

}
