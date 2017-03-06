package com.ntuaece.nikosapos.node;

import java.util.Timer;

import com.ntuaece.nikosapos.entities.Node;
import com.ntuaece.nikosapos.tasks.DiscoveryTask;
import com.ntuaece.nikosapos.tasks.UpdateBehaviorTask;

public class NodeThread extends Thread {
	private final Node node;
	
	public NodeThread(Node node){
		super(String.valueOf(node.getId()));
		this.node = node;
	}
	
	@Override
	public void run() {
		/* Initializing the timer */ 
		Timer mTimer = new Timer(true);
		
		DiscoveryTask discoveryTask = new DiscoveryTask(node);
		UpdateBehaviorTask updateTask = new UpdateBehaviorTask();
		
		mTimer.scheduleAtFixedRate(discoveryTask, 
								NodeScheduledTask.DISCOVERY_INITIAL_DELAY + node.getId()*10,
								NodeScheduledTask.DISCOVERY_PERIOD);
	}

}
