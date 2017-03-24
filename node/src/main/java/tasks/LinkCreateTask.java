package tasks;

import com.ntuaece.nikosapos.node.Link;
import com.ntuaece.nikosapos.node.Neighbor;
import com.ntuaece.nikosapos.node.Node;
import com.ntuaece.nikosapos.node.NodeList;

import distance.DistanceCalculator;

public class LinkCreateTask extends NodeTask {

    public LinkCreateTask(Node node) {
        super(node);
    }

    @Override
    public void run() {
        for (int neighborIndex = 0; neighborIndex < node.getNeighbors().size(); neighborIndex++) {
            Neighbor neighbor = node.getNeighbors().get(neighborIndex);
            long neighborID = neighbor.getId();
            Node neighborNode = NodeList.GetInstance().stream().filter(n -> n.getId() == neighborID).findFirst().get();
            Link.createLinkIfNotExists(node, neighborNode, DistanceCalculator.calculateDistance(node, neighborNode));
        }
    }

}
