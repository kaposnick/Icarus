package com.ntuaece.nikosapos;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ntuaece.nikosapos.entities.Packet;
import com.ntuaece.nikosapos.behaviorpacket.BehaviorUpdate;
import com.ntuaece.nikosapos.entities.Node;

@RestController
public class IcasController {

	private List<Node> nodeList = new ArrayList<Node>();

	@RequestMapping(method = RequestMethod.POST, value = "/register")
	public Node registerNode(@RequestBody Node node) {
		nodeList.add(node);
		node.setId(nodeList.size());
		return node;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/behavior")
	public ResponseEntity<String> updateBehavior(@RequestBody BehaviorUpdate packet) {
		/**
		 * do some stuff updating the behaviors
		 */
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
