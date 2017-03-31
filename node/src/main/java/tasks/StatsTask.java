package tasks;

import com.ntuaece.nikosapos.entities.Packet;
import com.ntuaece.nikosapos.node.Neighbor;
import com.ntuaece.nikosapos.node.NodeList;

public class StatsTask implements Runnable {

    @Override
    public void run() {
        NodeList.GetInstance().stream().forEach(node -> {
            System.out.println("Node " + node.getId() + " selfish Nodes: " + node.getSelfishNodes());
        });
//        NodeList.GetInstance().stream().forEach(node -> {
//            for (Neighbor neighbor : node.getNeighbors()) {
//              /*  System.out.println("Neighbor " + node.getId() + "-" + neighbor.getId() + " sent: "
//                        + neighbor.getPacketsSent() + ", forwarded: " + neighbor.getPacketsForwarded());*/
//            }
//        });
//        System.out.println("Dropped: " + Packet.droppedPacketCounter.get() +" Created: " + Packet.packetCounter.get());
    }

}
