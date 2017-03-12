package com.ntuaece.nikosapos.node;

import com.ntuaece.nikosapos.entities.Packet;

public class NeighborStatsRecorderImpl implements NeighborStatsRecorder {

    private final Node node;
    
    public NeighborStatsRecorderImpl(Node node) {
        this.node = node;
    }
    
    @Override
    public void recordPacket(Packet packet, long linkId) {
        Neighbor neighbor =  node
                            .getNeighbors()
                            .stream()
                            .filter(n -> n.getLink().getId() == linkId)
                            .findFirst()
                            .get();
        //is destination
        if (packet.getDestinationNodeID() != node.getId()) {
            
            // we want to increment forwarded packet counter because an ack has arrived 
            if (packet.isAck()) neighbor.incrementForwardedPacketCounter();
            
            //we want to increment swnt packet counter because node is senting a new packet
            else neighbor.incrementSentPacketCounter();
        }
    }

}
