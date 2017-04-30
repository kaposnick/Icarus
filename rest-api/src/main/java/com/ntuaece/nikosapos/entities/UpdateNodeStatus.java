package com.ntuaece.nikosapos.entities;

import java.util.TimerTask;

public class UpdateNodeStatus extends TimerTask {

    @Override
    public void run() {
        synchronized (NodeEntity.NodeEntityList) {
            NodeEntity.NodeEntityList.stream().forEach(node -> {
                node.updateNodeStatus();
            });
        }
    }

}
