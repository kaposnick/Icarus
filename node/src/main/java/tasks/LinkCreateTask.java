package tasks;

import distance.DistanceCalculator;
import node.Link;
import node.Neighbor;
import node.Node;
import node.NodeList;

public class LinkCreateTask implements Runnable {
    
    private final Node node;

    public LinkCreateTask(Node node) {
        this.node = node;
    }

    @Override
    public void run() {
        for (int neighborIndex = 0; neighborIndex < node.getNeighbors().size(); neighborIndex++) {
            Neighbor neighbor = node.getNeighbors().get(neighborIndex);
            long neighborID = neighbor.getId();
            Node neighborNode = NodeList.GetInstance().stream().filter(n -> n.getId() == neighborID).findFirst().get();
            Link.createLinkIfNotExists(node, neighborNode, Math.round(DistanceCalculator.calculateDistance(node, neighborNode)));
        }
    }

}
