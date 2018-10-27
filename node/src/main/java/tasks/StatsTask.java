package tasks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

import com.ntuaece.nikosapos.entities.Packet;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import node.Neighbor;
import node.Node;
import node.NodeList;

public class StatsTask implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatsTask.class);

    int i = 0;
        
    @Override
    public void run() {
        if ((i++) % 6 == 0) {
            NodeList.GetInstance().stream().forEach(node -> {
                System.out.println(node + " selfish Nodes: " + node.getSelfishNodes());
            });
        }
            int cooperativeNodesForwarded = 0;
            int selfishNodesForwarded = 0;
            int cooperativeNodesSent = 0;
            int selfishNodesSent = 0;
            int distantNodesSent = 0;
            int distantNodesForwarded = 0;
            for (Node node : NodeList.GetInstance()) {
                if (node.isCheater()) {
                    selfishNodesSent += node.getTotalPacketsSent();
                    selfishNodesForwarded += node.getTotalPacketsForwarded();
                } else {
                    cooperativeNodesSent += node.getTotalPacketsSent();
                    cooperativeNodesForwarded += node.getTotalPacketsForwarded();
                }

                if (node.isDistant()) {
                    distantNodesSent += node.getTotalPacketsSent();
                    distantNodesForwarded += node.getTotalPacketsForwarded();
                }

                if (node.getId() == 6) {
                }
            }
            LOGGER.info(String.valueOf((float) cooperativeNodesForwarded / cooperativeNodesSent));
            LOGGER.info(String.valueOf((float) selfishNodesForwarded / selfishNodesSent));
        } 
    }