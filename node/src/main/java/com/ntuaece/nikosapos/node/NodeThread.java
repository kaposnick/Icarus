package com.ntuaece.nikosapos.node;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ntuaece.nikosapos.entities.Packet;
import com.ntuaece.nikosapos.spring.NodeApplication;
import com.ntuaece.nikosapos.tasks.DarwinUpdateTask;
import com.ntuaece.nikosapos.tasks.DiscoveryTask;
import com.ntuaece.nikosapos.tasks.LinkCreateTask;
import com.ntuaece.nikosapos.tasks.RegistrationTask;
import com.ntuaece.nikosapos.tasks.UpdateBehaviorTask;

import okhttp3.OkHttpClient;

public class NodeThread extends Thread {
	private final Node node;
	
	private OkHttpClient httpClient;
	private Gson gson;
	private ScheduledExecutorService scheduledExecutorService;

	public NodeThread(Node node) {
		super("Node main " + String.valueOf(node.getId()));
		this.node = node;
	}

	@Override
	public void run() {
		httpClient = new OkHttpClient.Builder()
		                    .readTimeout(50, TimeUnit.SECONDS)
		                    .build();
		gson = new GsonBuilder()
						.excludeFieldsWithoutExposeAnnotation()
						.create();
		scheduledExecutorService = Executors.newScheduledThreadPool(2);		
		
		executeAndSheduleDiscoveryTask();
		// executeRegistrationTask();
		executeLinkTask();
		// scheduleDarwinTask();
		
		NodeRoutingThread routingThread = new NodeRoutingThread(node);
		routingThread.start();	
	}

    private void scheduleDarwinTask() {
        DarwinUpdateTask darwinUpdateTask = new DarwinUpdateTask(node);
        scheduledExecutorService.scheduleAtFixedRate(
                         darwinUpdateTask,
                         NodeScheduledTask.DARWIN_UPDATE_PERIOD * 10, 
                         NodeScheduledTask.DARWIN_UPDATE_PERIOD, 
                         TimeUnit.MILLISECONDS);
    }

    private void executeLinkTask() {
        LinkCreateTask linkTask = new LinkCreateTask(node);
        Future<?> linkFutureResult = scheduledExecutorService.submit(linkTask);
        while (!linkFutureResult.isDone()) ;
    }

    private void executeRegistrationTask() {
        RegistrationTask registrationTask = new RegistrationTask(node);
        Future<?> registrationFutureResult  = scheduledExecutorService.submit(registrationTask);
        while (!registrationFutureResult.isDone()) ;
    }

    private void executeAndSheduleDiscoveryTask() {
        DiscoveryTask discoveryTask = new DiscoveryTask(node);
        discoveryTask.setHttpClient(httpClient);
        discoveryTask.setGson(gson);        
        
        Future<?> discoveryFutureResult = scheduledExecutorService.submit(discoveryTask);
        // wait for the discovery task to get finished
        while (!discoveryFutureResult.isDone()) ;
        
     // Schedule discover neighbor task
        scheduledExecutorService.scheduleAtFixedRate(
                                                  discoveryTask,
                                                  NodeScheduledTask.DISCOVERY_PERIOD + node.getId() * 10, 
                                                  NodeScheduledTask.DISCOVERY_PERIOD,
                                                  TimeUnit.MILLISECONDS);
    }

}
