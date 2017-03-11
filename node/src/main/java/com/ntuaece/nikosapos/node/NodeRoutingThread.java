package com.ntuaece.nikosapos.node;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ntuaece.nikosapos.entities.Packet;

public class NodeRoutingThread extends Thread implements PacketReceiver {

	private final Node node;
	private Map<Long, Link> idToLink = new HashMap<>();
	private Queue<Long> incomingPacketQueue = new ConcurrentLinkedQueue<>();

	public NodeRoutingThread(Node node) {
		this.node = node;
		node.getNeighbors().stream().forEach(neighbor -> idToLink.put(neighbor.getLink().getId(), neighbor.getLink()));
	}

	@Override
	public void run() {
		while (true) {
			try {
				//System.out.println("Node " + node.getId() + " sleeping");
				sleep(Long.MAX_VALUE);
			} catch (InterruptedException e) {
				// e.printStackTrace();
				checkLinks();
			}
		}
	}

	private void checkLinks() {
		// TODO Auto-generated method stub
		while (incomingPacketQueue.size() > 0) {
			Optional<Packet> maybePacket = idToLink.get(incomingPacketQueue.poll()).removePacketFromDownLink(node);
			if (maybePacket.isPresent()) {
				managePacket(maybePacket.get());
				
				for (int i = 0; i<= 1000000; i++);
				
				Packet packet = maybePacket.get();
				long lastID = packet.getPathlist().get(packet.getPathlist().size()-1);
				packet.getPathlist().add(node.getId());
				node.getNeighbors()
					.stream()
					.filter(neighbor -> neighbor.getId() == lastID)
					.forEach(n -> n.getLink().addPacketToUpLink(node, packet));
			}
		}

	}

	private void managePacket(Packet packet) {
		System.out.println("Node " + node.getId() + " received packet " + packet.getId());
		
		if (packet.isAck()) {
			// informNeighborTables(packet);
			if (packet.getSourceNodeID() == node.getId()) packet.drop();
		} else {
			if (packet.getDestinationNodeID() == node.getId()) {
				// create an ack
				// inform icas
				// send to next neighbor
			} else {
				if ( hasToDrop() ) {
					// do nothing
				} else {
					// update packet
					// send to next neighbor
				}
			}
		}
	}
	
	private boolean hasToDrop(){return false;}

	@Override
	public void onPacketReceived(Long id) {
		incomingPacketQueue.offer(id);
		interrupt();
	}

}
