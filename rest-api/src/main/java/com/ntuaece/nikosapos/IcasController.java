package com.ntuaece.nikosapos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ntuaece.nikosapos.entities.Packet;
import com.ntuaece.nikosapos.behaviorpacket.BehaviorUpdate;
import com.ntuaece.nikosapos.behaviorpacket.BehaviorUpdateEntity;
import com.ntuaece.nikosapos.entities.NodeEntity;

@RestController
public class IcasController {

	private List<NodeEntity> nodeList = new ArrayList<>();

	public IcasController() {
		NodeEntity node1 = new NodeEntity();
		node1.setId(1);
		node1.setX(3);
		node1.setY(5);
		node1.setTokens(SimulationParameters.CREDITS_INITIAL);
		node1.setNodeConnectivityRatio(5.64f);

		NodeEntity node2 = new NodeEntity();
		node2.setId(2);
		node2.setX(4);
		node2.setY(6);
		node2.setTokens(SimulationParameters.CREDITS_INITIAL);

		nodeList.add(node1);
		nodeList.add(node2);

		List<NodeEntity> node1NeighborList = new ArrayList<>();
		node1NeighborList.add(node2);

		List<NodeEntity> node2NeighborList = new ArrayList<>();
		node2NeighborList.add(node1);

		node1.setNeighborList(node1NeighborList);
		node2.setNeighborList(node2NeighborList);

		HashMap<Long, Float> node1ConnectivityRatio = new HashMap<>();
		node1ConnectivityRatio.put(node2.getId(), 1.4f);

		HashMap<Long, Float> node2ConnectivityRatio = new HashMap<>();
		node1ConnectivityRatio.put(node1.getId(), 1.4f);

		node1.setNeighborConnectivityRatio(node1ConnectivityRatio);
		node2.setNeighborConnectivityRatio(node2ConnectivityRatio);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/behavior", produces = "application/json")
	public ResponseEntity<String> updateBehavior(@RequestBody BehaviorUpdate packet) {
		/**
		 * do some stuff updating the behaviors
		 */
		Long senderNodeID = new Long(packet.getId());

		NodeEntity sourceNode = null;
		
		Optional<NodeEntity> kati = nodeList
			.stream()
			.filter( node -> node.getId() == senderNodeID)
			.findFirst();
		
		for (NodeEntity node : nodeList) {
			if (node.getId() == senderNodeID) {
				sourceNode = node;
				break;
			}
		}

		for (BehaviorUpdateEntity behavior : packet.getNeighborList()) {
			if (sourceNode != null) {
				sourceNode
					.getNeighborConnectivityRatio()
					.put(behavior.getNeighId(), behavior.getRatio());
			}
		}

		return new ResponseEntity<String>("Updating neighbor behavior: Successful", HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/permission")
	public ResponseEntity<Packet> askForSendPermission(@RequestBody Packet packet) {
		return new ResponseEntity<Packet>(packet, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/delivery")
	public String deliverySuccessful() {
		return null;
	}
}
