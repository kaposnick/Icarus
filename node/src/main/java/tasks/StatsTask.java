package tasks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

import com.ntuaece.nikosapos.entities.Packet;
import com.ntuaece.nikosapos.node.Neighbor;
import com.ntuaece.nikosapos.node.NodeList;

public class StatsTask implements Runnable {
    File file;
    BufferedWriter bf;

    public StatsTask() {
        file = new File("/home/nickapostol/Desktop/stats.txt");
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        /*NodeList.GetInstance().stream().forEach(node -> {
            // System.out.println(node + " selfish Nodes: " +
            // node.getSelfishNodes());
            System.out.println(node);
            node.getNeighbors().forEach(neighbor -> {
                System.out.println("Neighbor [" + neighbor.getId() + "] Sent for Forwarded " + neighbor.getPacketsSent()
                        + " \t Forwarded " + neighbor.getPacketsForwarded() + "\t Ratio "
                        + neighbor.getConnectivityRatio());
            });
            System.out.println("------------");
        });*/

        try {
            FileWriter writer = new FileWriter(file, true);
            long delivered = Packet.getDeliveredPackets();
            long dropped = Packet.getDroppedPackets();
            long generated = Packet.packetCounter.get();
            writer.write("Generated: " + String.valueOf(generated) + " Delivered: " + String.valueOf(delivered)
                    + " Dropped: " + String.valueOf(dropped) + " Ratio: " + ((double) delivered / generated) * 100
                    + "\n");
            writer.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } finally {

        }
    }

}
