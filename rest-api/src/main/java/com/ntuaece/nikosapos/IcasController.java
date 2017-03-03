package com.ntuaece.nikosapos;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/*
 * Functionalities:
 * 1) Ask for delivery permission
 * 2) Send Neighbor Behavior
 * 3) Share rewards upon a delivery
 */

@RestController
public class IcasController {
	
	@RequestMapping(method = RequestMethod.POST, value = "/register")
	public String registerNode(@RequestBody Node node){
		return "OK!";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/behavior")
	public String updateBehavior() {
		return null;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/permission")
	public ResponseEntity<Packet> askForSendPermission(@RequestBody Packet packet) {
		return new ResponseEntity<Packet>(packet, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/deliverysuccessful")
	public String deliverySuccessful() {
		return null;
	}
}
