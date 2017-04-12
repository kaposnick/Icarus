package tasks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

import com.ntuaece.nikosapos.entities.Packet;

import node.Neighbor;
import node.Node;
import node.NodeList;

public class StatsTask implements Runnable {
    File coopFile;
    File selfFile;
    BufferedWriter bf;

    public StatsTask() {
        coopFile = new File("/home/nickapostol/Desktop/darwin/coop.txt");
        selfFile = new File("/home/nickapostol/Desktop/darwin/self.txt");
        if (coopFile.exists()) {
            coopFile.delete();
        }
        if (selfFile.exists()) {
            selfFile.delete();
        }
        try {
            coopFile.createNewFile();
            selfFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        NodeList.GetInstance().stream().forEach(node -> {
             System.out.println(node + " selfish Nodes: " +
             node.getSelfishNodes());
//            System.out.println(node);
//            node.getNeighbors().forEach(neighbor -> {
//                System.out.println("Neighbor [" + neighbor.getId() + "] Sent for Forwarded " + neighbor.getPacketsSent()
//                        + " \t Forwarded " + neighbor.getPacketsForwarded() + "\t Ratio "
//                        + neighbor.getConnectivityRatio());
//            });
//            System.out.println("------------");
        });

        try {
            FileWriter coopwriter = new FileWriter(coopFile, true);
            FileWriter selfwriter = new FileWriter(selfFile, true);
//            long delivered = Packet.getDeliveredPackets();
//            long dropped = Packet.getDroppedPackets();
//            long generated = Packet.packetCounter.get();
//            writer.write("Generated: " + String.valueOf(generated) + " Delivered: " + String.valueOf(delivered)
//                    + " Dropped: " + String.valueOf(dropped) + " Ratio: " + ((double) delivered / generated) * 100
//                    + "\n");
            int cooperativeNodesForwarded = 0; 
            int selfishNodesForwarded = 0;
            int cooperativeNodesSent = 0;
            int selfishNodesSent = 0;
            for (Node node : NodeList.GetInstance()) {
                if (node.isCheater()) {
                    selfishNodesSent += node.getTotalPacketsSent();
                    selfishNodesForwarded += node.getTotalPacketsForwarded();
                } else {
                    cooperativeNodesSent += node.getTotalPacketsSent();
                    cooperativeNodesForwarded += node.getTotalPacketsForwarded();
                }
            }
//            coopwriter.write("Cooperative Nodes forwarding ratio: " + (float) cooperativeNodesForwarded / cooperativeNodesSent 
//                     + "\tSelfish Nodes forwarding ratio: " + (float) selfishNodesForwarded / selfishNodesSent + "\n");
            coopwriter.write((float) cooperativeNodesForwarded/ cooperativeNodesSent + "\n");
            selfwriter.write((float) selfishNodesForwarded / selfishNodesSent + "\n");
            coopwriter.close();
            selfwriter.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } finally {

        }
    }

}
