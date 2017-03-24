package com.ntuaece.nikosapos.tasks;


import com.ntuaece.nikosapos.node.Node;

import services.NeighborResponsible;

public class DiscoveryTask implements Runnable {
    private final NeighborResponsible responsible;
    private final Node node;
    
    public DiscoveryTask(Node node, NeighborResponsible service) {
        this.node = node;
        this.responsible = service;
    }


    @Override
    public void run() {
        System.out.println("Node: " + node.getId() + " discovering neighbor nodes...");
        responsible.discoverNeigbors();
    }
}
