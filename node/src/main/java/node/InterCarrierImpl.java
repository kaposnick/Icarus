package node;

import java.util.Optional;

import com.ntuaece.nikosapos.entities.Packet;

public class InterCarrierImpl implements InterCarrier {

    @Override
    public void deliverToQueue(long id, Packet packet) {
        // find next node
        Optional<Node> nextNode = NodeList.GetNodeById(id);

        if (nextNode.isPresent()) {
            if (packet.isSemiAck()) {
                long neighborId = packet.getNeighborId();
                Optional<Neighbor> neighbor = nextNode.get().findNeighborById(neighborId);
                if (neighbor.isPresent()) {
                    neighbor.get().getLink().addPacketToDownLink(nextNode.get(), packet);
                } else {
                    throw new RuntimeException(nextNode.get() + " throwed exception because neighbor was not present");
                }
            } else if (!packet.isAck()) {
                // get the previous neighbor
                int sizeOfPathList = packet.getPathlist().size();
                Optional<Neighbor> neighbor = nextNode.get()
                                                      .findNeighborById(packet.getPathlist().get(sizeOfPathList - 2));

                if (neighbor.isPresent()) {
                    // previous neighbor add packet to downlink
                    neighbor.get().getLink().addPacketToDownLink(nextNode.get(), packet);
                } else {
                    throw new RuntimeException(nextNode.get() + " throwed exception because neighbor was not present");
                }
            } else {
                // packet is Ack
                int sizeOfPathList = packet.getPathlist().size();
                int thisNodeIndex = -1;
                int nextNeighborIndex = -1;
                for (int i = 0; i < sizeOfPathList; i++) {
                    if (packet.getPathlist().get(i) == id) {
                        thisNodeIndex = i;
                        break;
                    }
                }
                nextNeighborIndex = thisNodeIndex + 1;
                Optional<Neighbor> neighbor = nextNode.get()
                                                      .findNeighborById(packet.getPathlist().get(nextNeighborIndex));

                if (neighbor.isPresent()) {
                    // previous neighbor add packet to downlink
                    neighbor.get().getLink().addPacketToDownLink(nextNode.get(), packet);
                } else {
                    throw new RuntimeException(nextNode.get() + " throwed exception because neighbor was not present");
                }
            }
        } else {
            throw new RuntimeException("Node " + id + " not present");
        }
    }

}
