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
import tasks.StatsTask;
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

        try {
            for (int i = 0; i < 3; i++) {
                neighborService.exchangeRoutingTables();
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        if (node.getId() == 1) NodeList.GetInstance().stream().forEach(node -> {
//            node.getDistantNodes().forEach(distant -> System.out.println(node.getId() + " " + distant));
//        });

        // executeRegistrationTask();
         scheduleDarwinTask();
        // scheduleUpdateIcasForNeighborBehaviorTask();

         if (node.getId() == 1)
             scheduledExecutorService.scheduleAtFixedRate(new StatsTask(), 10000,
                                                          10000, TimeUnit.MILLISECONDS);

        NodeRoutingThread routingThread = new NodeRoutingThread(node, neighborService, icasService);
         routingThread.start();
    }

    private void scheduleUpdateIcasForNeighborBehaviorTask() {
        scheduledExecutorService.scheduleAtFixedRate(new UpdateBehaviorTask(node, icasService),
                                                     NodeScheduledTask.DARWIN_UPDATE_PERIOD,
                                                     NodeScheduledTask.DARWIN_UPDATE_PERIOD,
                                                     TimeUnit.MILLISECONDS);

    }

    private void prepareResources() {
        OkHttpClient httpClient = new OkHttpClient.Builder().readTimeout(50, TimeUnit.SECONDS).build();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        // icasService = new IcasService(node, httpClient, gson);
        icasService = new TestIcasService();
        neighborService = new NeighborService(node, httpClient, gson);
        scheduledExecutorService = Executors.newScheduledThreadPool(2);
    }

    private void scheduleDarwinTask() {
        DarwinUpdateTask darwinUpdateTask = new DarwinUpdateTask(node, neighborService);
        scheduledExecutorService.scheduleAtFixedRate(darwinUpdateTask,
                                                     NodeScheduledTask.DARWIN_UPDATE_PERIOD,
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
//        scheduledExecutorService.scheduleAtFixedRate(discoveryTask,
//                                                     NodeScheduledTask.DISCOVERY_PERIOD + node.getId() * 10,
//                                                     NodeScheduledTask.DISCOVERY_PERIOD,
//                                                     TimeUnit.MILLISECONDS);
    }

}
