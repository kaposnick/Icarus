package node;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ntuaece.nikosapos.entities.Packet;
import com.ntuaece.nikosapos.node.Link;
import com.ntuaece.nikosapos.node.Neighbor;
import com.ntuaece.nikosapos.node.Node;

public class LinkTest {

	@Test
	public void test() {
		Node node1 = new Node.Builder()
					.setId(2)
					.setX(3)
					.setY(4)
					.build();
		
		Node node2 = new Node.Builder()
				.setId(3)
				.setX(3)
				.setY(4)
				.build();
		
		node1.addNeighbor(Neighbor.FromNode(node2));
		node2.addNeighbor(Neighbor.FromNode(node1));

		Link.createLinkIfNotExists(node1, node2, 1);
		
		for(int i =0; i<10; i++){
			node1.getNeighbors()
				.get(0)
				.getLink()
				.addPacketToUpLink(node1, new Packet.Builder()
											.setSourceNodeId(node1.getId())
											.setDestinationNodeId(node2.getId())
											.setData((byte)4)
											.build());
		}
		
		while(true);
	}

}
