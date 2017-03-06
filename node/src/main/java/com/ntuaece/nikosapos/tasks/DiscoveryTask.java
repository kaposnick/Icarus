package com.ntuaece.nikosapos.tasks;

import java.util.TimerTask;

import com.ntuaece.nikosapos.discover.DiscoveryService;
import com.ntuaece.nikosapos.discover.DiscoveryServiceImpl;
import com.ntuaece.nikosapos.entities.Node;

public class DiscoveryTask extends TimerTask {
	
	private final Node node;
	private final DiscoveryService discoveryServiceImpl;
	private boolean verbose;
	
	public DiscoveryTask(Node node){
		this.node = node;
		this.discoveryServiceImpl = new DiscoveryServiceImpl(node);
		verbose = false;
	}
	
	public void setVerbose(boolean verbose){
		this.verbose = verbose;
	}

	@Override
	public void run() {
		System.out.println("Node: " + node.getId() + " discovering neighbor nodes...");
		boolean result = discoveryServiceImpl.discoverForNeighbors();
		if (result && verbose){
			System.out.println("Discovery successful");
		}
	}

}
