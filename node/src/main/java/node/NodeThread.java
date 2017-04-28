package node;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import services.IcasResponsible;
import services.IcasService;
import services.NeighborResponsible;
import services.NeighborService;
import services.MockIcasService;
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
    private ScheduledFuture<?> darwinComputation, updateRoutingTablesTask, updateIcasNeighborTask,
            darwinPacketDeliveryTask, statsTask;
    private NodeRoutingThread routingThread;

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
            for (int i = 0; i < 10; i++) {
                neighborService.exchangeRoutingTables();
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executeRegistrationTask();

        if (!node.isCheater() || true) {
            scheduleDarwinTask();
            scheduleExecuteDarwinAlgorithmTask();
        }

        scheduleUpdateIcasForNeighborBehaviorTask();
        // scheduleUpdateRoutingTablesTask();

        if (node.getId() == 1)
            statsTask = scheduledExecutorService.scheduleAtFixedRate(new StatsTask(),
                                                                     10000,
                                                                     10000,
                                                                     TimeUnit.MILLISECONDS);

        routingThread = new NodeRoutingThread(node, neighborService, icasService);
        node.setNodeRoutingThread(routingThread);
        routingThread.start();
    }

    public void kill() {
        if (darwinComputation != null && !darwinComputation.isCancelled()) darwinComputation.cancel(true);
        if (updateRoutingTablesTask != null && !updateRoutingTablesTask.isCancelled())
            updateRoutingTablesTask.cancel(true);
        if (updateIcasNeighborTask != null && !updateIcasNeighborTask.isCancelled())
            updateIcasNeighborTask.cancel(true);
        if (darwinPacketDeliveryTask != null && !darwinPacketDeliveryTask.isCancelled())
            darwinPacketDeliveryTask.cancel(true);
        if (statsTask != null && !statsTask.isCancelled()) statsTask.cancel(true);
        if (routingThread != null && routingThread.isAlive()) {
            routingThread.stopThread();
            routingThread = null;
        }
        neighborService.unregister();
        icasService.unregister();
        scheduledExecutorService.shutdownNow();
    }

    private void scheduleExecuteDarwinAlgorithmTask() {
        darwinComputation = scheduledExecutorService.scheduleAtFixedRate(() -> {
			if (node.getId() == 0) {
				System.out.println("Executing Darwin Algorithm");
			}
			node.executeDarwinAlgorithm();
		}, 10500, 10500, TimeUnit.MILLISECONDS);
    }

    private void scheduleUpdateRoutingTablesTask() {
        updateRoutingTablesTask = scheduledExecutorService.scheduleAtFixedRate(() -> neighborService.exchangeRoutingTables(), 60, 60, TimeUnit.SECONDS);
    }

    private void scheduleUpdateIcasForNeighborBehaviorTask() {
        updateIcasNeighborTask = scheduledExecutorService.scheduleAtFixedRate(new UpdateBehaviorTask(node, icasService),
                                                                              NodeScheduledTask.DARWIN_UPDATE_PERIOD,
                                                                              NodeScheduledTask.DARWIN_UPDATE_PERIOD,
                                                                              TimeUnit.MILLISECONDS);

    }

    private void prepareResources() {
        OkHttpClient httpClient = new OkHttpClient.Builder().readTimeout(50, TimeUnit.SECONDS)
                                                            .writeTimeout(30, TimeUnit.SECONDS)
                                                            .build();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        // icasService = new IcasService(node, httpClient, gson);
        icasService = new MockIcasService();
        neighborService = new NeighborService(node, httpClient, gson);
        scheduledExecutorService = Executors.newScheduledThreadPool(2);
    }

    private void scheduleDarwinTask() {
        DarwinUpdateTask darwinUpdateTask = new DarwinUpdateTask(node, neighborService);
        darwinPacketDeliveryTask = scheduledExecutorService.scheduleAtFixedRate(darwinUpdateTask,
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
        // scheduledExecutorService.scheduleAtFixedRate(discoveryTask,
        // NodeScheduledTask.DISCOVERY_PERIOD + node.getId() * 10,
        // NodeScheduledTask.DISCOVERY_PERIOD,
        // TimeUnit.MILLISECONDS);
    }

}
