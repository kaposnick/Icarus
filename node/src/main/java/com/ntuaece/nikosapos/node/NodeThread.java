package com.ntuaece.nikosapos.node;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import javax.swing.Timer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import services.IcasResponsible;
import services.IcasService;
import services.NeighborResponsible;
import services.NeighborService;
import services.TestIcasService;
import tasks.DarwinUpdateTask;
import tasks.DiscoveryTask;
import tasks.LinkCreateTask;
import tasks.RegistrationTask;
import tasks.UpdateBehaviorTask;

public class NodeThread extends Thread {
    private final Node node;

    private IcasResponsible icasService;
    private NeighborResponsible neighborService;
    private ScheduledExecutorService scheduledExecutorService;

    public NodeThread(Node node) {
        super("Node main " + String.valueOf(node.getId()));
        this.node = node;
    }

    @Override
    public void run() {
        prepareResources();
        executeAndSheduleDiscoveryTask();
        executeLinkTask();

        LongStream.rangeClosed(0, 3).filter(id -> id != node.getId() && !node.isNeighborWith(id)).forEach(id -> {
            Distant distant = new Distant();
            distant.setId(id);
            distant.setRelayId(node.getId());
            distant.setDistance(123456);
            distant.setTotalHops(Integer.MAX_VALUE);
            node.getDistantNodes().add(distant);
        });

        try {
            for (int i = 0; i < 4; i++) {
                neighborService.exchangeRoutingTables();
                Thread.sleep(1500);
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        node.getDistantNodes().stream().forEach(distant -> {
            System.out.println("Node " + node.getId() + " distant " + distant.getId() + " distance "
                    + distant.getDistance());
        });
       

         executeRegistrationTask();
//         scheduleDarwinTask();
         scheduleUpdateIcasForNeighborBehaviorTask();

        NodeRoutingThread routingThread = new NodeRoutingThread(node, neighborService, icasService);
        routingThread.start();
    }

    private void scheduleUpdateIcasForNeighborBehaviorTask() {
        scheduledExecutorService.scheduleAtFixedRate(
                                          new UpdateBehaviorTask(node, icasService), 
                                          NodeScheduledTask.DARWIN_UPDATE_PERIOD,
                                          NodeScheduledTask.DARWIN_UPDATE_PERIOD,
                                          TimeUnit.MILLISECONDS);
        
    }

    private void prepareResources() {
        OkHttpClient httpClient = new OkHttpClient.Builder().readTimeout(50, TimeUnit.SECONDS).build();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        icasService = new IcasService(node, httpClient, gson);
//        icasService = new TestIcasService();
        neighborService = new NeighborService(node, httpClient, gson);
        scheduledExecutorService = Executors.newScheduledThreadPool(2);
    }

    private void scheduleDarwinTask() {
        DarwinUpdateTask darwinUpdateTask = new DarwinUpdateTask(node, neighborService);
        scheduledExecutorService.scheduleAtFixedRate(darwinUpdateTask,
                                                     NodeScheduledTask.DARWIN_UPDATE_PERIOD * 10,
                                                     NodeScheduledTask.DARWIN_UPDATE_PERIOD,
                                                     TimeUnit.MILLISECONDS);
    }

    private void executeLinkTask() {
        LinkCreateTask linkTask = new LinkCreateTask(node);
        Future<?> linkFutureResult = scheduledExecutorService.submit(linkTask);
        while (!linkFutureResult.isDone());
    }

    private void executeRegistrationTask() {
        RegistrationTask registrationTask = new RegistrationTask(node, icasService);
        Future<?> registrationFutureResult = scheduledExecutorService.submit(registrationTask);
        while (!registrationFutureResult.isDone());
    }

    private void executeAndSheduleDiscoveryTask() {
        DiscoveryTask discoveryTask = new DiscoveryTask(node, neighborService);

        Future<?> discoveryFutureResult = scheduledExecutorService.submit(discoveryTask);
        // wait for the discovery task to get finished
        while (!discoveryFutureResult.isDone());

        // Schedule discover neighbor task
        scheduledExecutorService.scheduleAtFixedRate(discoveryTask,
                                                     NodeScheduledTask.DISCOVERY_PERIOD + node.getId() * 10,
                                                     NodeScheduledTask.DISCOVERY_PERIOD,
                                                     TimeUnit.MILLISECONDS);
    }

}
