package com.ntuaece.nikosapos.tasks;

import com.google.gson.Gson;
import com.ntuaece.nikosapos.discover.DiscoveryService;
import com.ntuaece.nikosapos.discover.DiscoveryServiceImpl;
import com.ntuaece.nikosapos.node.Node;

import okhttp3.OkHttpClient;

public class DiscoveryTask extends NetworkTask {

    private final DiscoveryService discoveryServiceImpl;

    public DiscoveryTask(Node node) {
        super(node);
        this.discoveryServiceImpl = new DiscoveryServiceImpl(node);
    }

    public void setHttpClient(OkHttpClient client) {
        if (discoveryServiceImpl instanceof DiscoveryServiceImpl) {
            ((DiscoveryServiceImpl) discoveryServiceImpl).setHttpClient(client);
        }
    }

    public void setGson(Gson gson) {
        if (discoveryServiceImpl instanceof DiscoveryServiceImpl) {
            ((DiscoveryServiceImpl) discoveryServiceImpl).setGson(gson);
        }
    }

    @Override
    public void run() {
        System.out.println("Node: " + node.getId() + " discovering neighbor nodes...");
        discoveryServiceImpl.discoverForNeighbors();

    }
}
