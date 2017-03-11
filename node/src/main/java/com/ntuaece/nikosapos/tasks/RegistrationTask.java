package com.ntuaece.nikosapos.tasks;

import com.ntuaece.nikosapos.node.Node;
import com.ntuaece.nikosapos.register.RegistrationService;
import com.ntuaece.nikosapos.register.RegistrationServiceImpl;

public class RegistrationTask extends NodeTask implements Runnable {

	private final RegistrationService registrationService;

	public RegistrationTask(Node node) {
		super(node);
		this.registrationService = new RegistrationServiceImpl(node);
	}

	@Override
	public void run() {
		System.out.println("Node: " + node.getId() + " registrating to ICAS...");
		if (registrationService.registerToICAS()) {
			System.out.println("Node: " + node.getId() + " successfully registered to ICAS");
		} else {
			System.out.println("Node: " + node.getId() + " encountered a problem registering to ICAS");
		}
	}

}
