package com.ntuaece.nikosapos.spring;

import java.util.OptionalLong;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ntuaece.nikosapos.discover.DiscoverPacket;
import com.ntuaece.nikosapos.discover.DiscoverResponse;
import com.ntuaece.nikosapos.distance.NeighborValidator;
import com.ntuaece.nikosapos.distance.NeighborValidatorImpl;
import com.ntuaece.nikosapos.entities.Node;
import com.ntuaece.nikosapos.node.NodeList;

@RestController
public class NodeController {

	private final NeighborValidator neighborValidator;

	public NodeController() {
		neighborValidator = new NeighborValidatorImpl();
	}

	@RequestMapping(method = RequestMethod.POST, value = "/discovery/{id}")
	public ResponseEntity onDiscovery(@PathVariable("id") String nodeID,
			@RequestBody DiscoverPacket incomingDiscoveryMessage) {

		/* verify incoming ID is a valid node ID */
		OptionalLong maxID = NodeList.GetInstance().stream().mapToLong(n -> n.getId()).max();

		if (!maxID.isPresent() || Long.parseLong(nodeID) > maxID.getAsLong()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		/* sending node */
		Node sourceNode = new Node.Builder().setId(incomingDiscoveryMessage.getSourceID())
				.setX(incomingDiscoveryMessage.getSourceX()).setY(incomingDiscoveryMessage.getSourceY()).build();

		Node targetNode = NodeList.GetInstance().stream().filter(n -> n.getId() == Long.parseLong(nodeID)).findFirst()
				.get();

		if (neighborValidator.areNeighbors(sourceNode, targetNode)) {
			if (!targetNode.isNeighborWith(sourceNode.getId())) {
				targetNode.addNeighbor(sourceNode);
			}
			DiscoverResponse response = new DiscoverResponse();
			response.setSourceID(targetNode.getId());
			response.setSourceX(targetNode.getX());
			response.setSourceY(targetNode.getY());
			response.setAreNeighbors(true);
			return new ResponseEntity<DiscoverResponse>(response, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
