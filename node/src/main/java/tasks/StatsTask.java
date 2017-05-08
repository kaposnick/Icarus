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
    int i = 0;

    File coopFile;
    File selfFile;
    File distantFile;

    public StatsTask() {
        coopFile = new File("/home/nickapostol/Desktop/darwin/coop.txt");
        selfFile = new File("/home/nickapostol/Desktop/darwin/self.txt");
        distantFile = new File("/home/nickapostol/Desktop/darwin/distant.txt");
        if (coopFile.exists()) {
            coopFile.delete();
        }
        if (selfFile.exists()) {
            selfFile.delete();
        }
        
        if (distantFile.exists()) {
            distantFile.delete();
        }
        try {
            coopFile.createNewFile();
            selfFile.createNewFile();
            distantFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if ((i++) % 6 == 0) {
            NodeList.GetInstance().stream().forEach(node -> {
                System.out.println(node + " selfish Nodes: " + node.getSelfishNodes());
            });
        }

        try {
            FileWriter coopwriter = new FileWriter(coopFile, true);
            FileWriter selfwriter = new FileWriter(selfFile, true);
            FileWriter distWriter = new FileWriter(distantFile,true);
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
            }
            coopwriter.write((float) cooperativeNodesForwarded / cooperativeNodesSent + "\n");
            coopwriter.close();
            selfwriter.write((float) selfishNodesForwarded / selfishNodesSent + "\n");
            selfwriter.close();
            distWriter.write((float) distantNodesForwarded / distantNodesSent + "\n");
            distWriter.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } finally {

        }
    }

}
