package com.ntuaece.nikosapos.carrier;

import java.util.Optional;

import com.ntuaece.nikosapos.entities.Packet;
import com.ntuaece.nikosapos.node.Neighbor;
import com.ntuaece.nikosapos.node.Node;
import com.ntuaece.nikosapos.node.NodeList;

public class InterCarrierImpl implements InterCarrier {

	@Override
	public void deliverToQueue(long id, Packet packet) {
		// find next node
		Optional<Node> nextNode = NodeList.GetNodeById(id);

		if (nextNode.isPresent()) {

			// get the previous neighbor
			int sizeOfPathList = packet.getPathlist().size();
			Optional<Neighbor> neighbor = nextNode.get().findNeighborById(packet.getPathlist().get(sizeOfPathList-1));

			if (neighbor.isPresent()) {
				// previous neighbor add packet to downlink
				neighbor.get().getLink().addPacketToDownLink(nextNode.get(), packet);
			}
		}
	}

}
