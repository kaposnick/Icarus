package com.ntuaece.nikosapos.entities;
import java.util.TimerTask;

public class UpdateNodeStatus extends TimerTask {

    @Override
    public void run() {
        NodeEntity
            .NodeEntityList
            .stream()
            .forEach(node -> {
                node.updateNodeStatus();
            });
    }

}
