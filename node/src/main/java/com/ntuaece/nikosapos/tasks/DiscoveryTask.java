package com.ntuaece.nikosapos.tasks;

import java.util.TimerTask;

import com.ntuaece.nikosapos.discover.DiscoveryService;
import com.ntuaece.nikosapos.entities.Node;

public class DiscoveryTask extends TimerTask {
	
	private final Node node;
	private final DiscoveryService discoveryService;
	
	public DiscoveryTask(Node node){
		this.node = node;
		this.discoveryService = new DiscoveryService(node);
	}

	@Override
	public void run() {
		System.out.println("Node: " + node.getId() + " discovering neighbor nodes...");
		boolean result = discoveryService.discoverForNeighbors();
		if (result && false){
			System.out.println("Discovery successful");
		}
	}

}
