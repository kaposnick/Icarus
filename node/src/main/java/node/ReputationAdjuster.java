package node;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import darwin.DarwinUtils;

public class ReputationAdjuster implements Runnable {
    private final Function<Double, Double> ReputationAlleviationFunction = x -> x / x + 1.1;
    private final Node node;

    public ReputationAdjuster(Node node) {
        this.node = node;
    }

    private void adjustReputation() {
        Set<Long> selfishNodes = new HashSet<>(node.getSelfishNodes());
        for (Long selfishId : selfishNodes) {
            Neighbor neighbor = node.findNeighborById(selfishId).get();
            neighbor.setDarwinI(ReputationAlleviationFunction.apply(neighbor.getDarwinI()));
            DarwinUtils.DecideDarwinSelfishNode(node, neighbor);
        }
    }

    @Override
    public void run() {
        adjustReputation();
    }
}
