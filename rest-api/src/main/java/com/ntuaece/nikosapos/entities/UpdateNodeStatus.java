package com.ntuaece.nikosapos.entities;
import java.util.TimerTask;

public class UpdateNodeStatus extends TimerTask {

    @Override
    public void run() {
//        System.out.println("Updating node status based on credits...");
        NodeEntity
            .NodeEntityList
            .stream()
            .forEach(node -> {
                node.updateNodeStatus();
            });
    }

}
