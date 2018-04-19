package tasks;

import node.Node;
import services.NeighborResponsible;

public class DarwinUpdateTask implements Runnable {

    private final Node node;
    private final NeighborResponsible service;

    public DarwinUpdateTask(Node node, NeighborResponsible service) {
        this.node = node;
        this.service = service;
    }

    @Override
    public void run() {
        service.exchangeDarwinInfo();
    }
}
