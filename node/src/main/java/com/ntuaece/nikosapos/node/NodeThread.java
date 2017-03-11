package com.ntuaece.nikosapos.node;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ntuaece.nikosapos.entities.Packet;
import com.ntuaece.nikosapos.spring.NodeApplication;
import com.ntuaece.nikosapos.tasks.DiscoveryTask;
import com.ntuaece.nikosapos.tasks.LinkCreateTask;
import com.ntuaece.nikosapos.tasks.RegistrationTask;
import com.ntuaece.nikosapos.tasks.UpdateBehaviorTask;

import okhttp3.OkHttpClient;

public class NodeThread extends Thread {
	private final Node node;

	public NodeThread(Node node) {
		super(String.valueOf(node.getId()));
		this.node = node;
	}

	@Override
	public void run() {
		OkHttpClient httpClient = new OkHttpClient();
		Gson gson = new GsonBuilder()
						.excludeFieldsWithoutExposeAnnotation()
						.create();
		
		DiscoveryTask discoveryTask = new DiscoveryTask(node);
		discoveryTask.setHttpClient(httpClient);
		discoveryTask.setGson(gson);		
		
		RegistrationTask registrationTask = new RegistrationTask(node);
		LinkCreateTask linkTask = new LinkCreateTask(node);
		UpdateBehaviorTask updateTask = new UpdateBehaviorTask();

		ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);

		Future<?> discoveryFutureResult = scheduledExecutorService.submit(discoveryTask);
		// wait for the discovery task to get finished
		while (!discoveryFutureResult.isDone())
			;

		Future<?> linkFutureResult = scheduledExecutorService.submit(linkTask);
		// wait fot the link creation task to get finished
		while (!linkFutureResult.isDone())
			;

		
		NodeRoutingThread routingThread = new NodeRoutingThread(node);
		node.getNeighbors().stream().forEach(neigh -> neigh.getLink().setPacketReceiver(node, routingThread));
		routingThread.start();

		if (node.getId() >= 1 ) {
			for (int i = 0; i< 1; i++){
				node.getNeighbors()
				.get(0)
				.getLink()
				.addPacketToUpLink(node, new Packet.Builder()
						.setSourceNodeId(node.getId())
						.setDestinationNodeId(15)
						.setData((byte)4)
						.build());
				try {
					sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
				
//		Future<?> registrationFutureResult = scheduledExecutorService.submit(registrationTask);
//		// wait for the registration task to get finished
//		while (!registrationFutureResult.isDone())
//			;
		

		// Schedule discover neighbor task
		scheduledExecutorService.scheduleAtFixedRate(discoveryTask,
				NodeScheduledTask.DISCOVERY_PERIOD + node.getId() * 10, NodeScheduledTask.DISCOVERY_PERIOD,
				TimeUnit.MILLISECONDS);

		// schedule update neighbor behavior task
		scheduledExecutorService.scheduleAtFixedRate(updateTask,
				NodeScheduledTask.UPDATE_BEHAVIOR_INITIAL_DELAY + node.getId() * 10,
				NodeScheduledTask.UPDATE_BEHAVIOR_PERIOD, TimeUnit.MILLISECONDS);
		}

}
