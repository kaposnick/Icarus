package darwin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ntuaece.nikosapos.SimulationParameters;
import com.ntuaece.nikosapos.behaviorpacket.BehaviorUpdateEntity;
import com.ntuaece.nikosapos.node.DarwinPacket;
import com.ntuaece.nikosapos.node.DarwinUtils;
import com.ntuaece.nikosapos.node.Neighbor;
import com.ntuaece.nikosapos.node.Node;

public class DarwinCalculator implements Darwin {
    private final static double gamma = 2;
    private final Node node;

    public DarwinCalculator(Node node) {
        this.node = node;
    }

    private Map<Long, Double> p_minusI(List<DarwinPacket> darwinPacketList) {
        Map<Long, Double> neighborProbMap = new HashMap<>(darwinPacketList.size());

        double c_minusI = 0;
        double p_minusI = 0;

        for (Neighbor neighbor : node.getNeighbors()) {
            double c_mj = 0;
            double c_im = 0;
            double fractor = 0;
            double numerator = 0;

            for (DarwinPacket packet : darwinPacketList) {
                if (packet.getId() != neighbor.getId()) {
                    for (BehaviorUpdateEntity entity : packet.getNeighborRatioList()) {
                        if (entity.getNeighId() == neighbor.getId()) {
                            c_mj = entity.getRatio();
                            c_im = node.findNeighborById(packet.getId()).get().getConnectivityRatio();
                            numerator += c_im * c_mj;
                            fractor += c_im;
                        }
                    }
                }
            }

            c_im = 1;
            c_mj = neighbor.getConnectivityRatio();
            numerator += c_im * c_mj;
            fractor += c_im;

            c_minusI = numerator / fractor;
            neighbor.setMeanConnectivityRatio(c_minusI);

            p_minusI = 1 - c_minusI;
            neighbor.setP(p_minusI);
            neighborProbMap.put(neighbor.getId(), p_minusI);
        }
        return neighborProbMap;
    }

    private double p_I(List<DarwinPacket> darwinPacketList) {
        double numerator = 0;
        double fractor = 0;
        for (DarwinPacket packet : darwinPacketList) {
            Neighbor neighbor = node.findNeighborById(packet.getId()).get();
            for (BehaviorUpdateEntity entity : packet.getNeighborRatioList()) {
                if (entity.getNeighId() == node.getId()) {
                    // cii * cji
                    numerator += entity.getRatio() * neighbor.getConnectivityRatio();
                    fractor += neighbor.getConnectivityRatio();
                    neighbor.setNeighborDarwinForMe(entity.getNeighborDarwinForMe());
                }
            }
        }
        double c_I = 1;
        if (fractor > 0) c_I = numerator / fractor;
        return 1 - c_I;
    }

    private double p_I_alt(List<DarwinPacket> darwiPacketList) {
        double numerator = 0;
        double fractor = 0;
        for (DarwinPacket packet : darwiPacketList) {
            Neighbor neighbor = node.findNeighborById(packet.getId()).get();
            for (BehaviorUpdateEntity entity : packet.getNeighborRatioList()) {
                if (entity.getNeighId() == this.node.getId()) {
                    neighbor.setNeighborDarwinForMe(entity.getNeighborDarwinForMe());
                    numerator += entity.getP();
                    fractor++;
                }
            }
        }
        return (fractor > 0) ? (double)(1 - (numerator / fractor)) : 1f;
    }

    private double p_DarwinI(Neighbor neighbor) {
        return neighbor.getNeighborDarwin();
    }

    private double p_DarwinMinusI(Neighbor neighbor) {
        return neighbor.getNeighborDarwinForMe();
    }

    @Override
    public void computeDarwin(List<DarwinPacket> darwinPacketList) {
        double p_I = p_I(darwinPacketList);
        Map<Long, Double> neighProbMap = p_minusI(darwinPacketList);

        double p_DarwinI = 0;
        double p_DarwinMinusI = 0;
        double p_minusI = 0;
        double q_I = 0;
        double q_minusI = 0;
        double p_DarwinNew = 0;

        for (Neighbor neighbor : node.getNeighbors()) {
            p_DarwinI = p_DarwinI(neighbor);
            p_DarwinMinusI = p_DarwinMinusI(neighbor);
            p_minusI = neighProbMap.get(neighbor.getId());

            q_I = DarwinUtils.normalizeValue(p_I - p_DarwinI);
            q_minusI = DarwinUtils.normalizeValue(p_minusI - p_DarwinMinusI);
            p_DarwinNew = DarwinUtils.normalizeValue(gamma * (q_minusI - q_I));
            neighbor.setNeighborDarwin(p_DarwinNew);

            if (neighbor.getP() >= SimulationParameters.EDP) {
                node.addDarwinSelfishNode(neighbor.getId());
            } else {
                node.removeDarwinSelfishNode(neighbor.getId());
            }
//            neighbor.clearCounters();
        }
        neighProbMap.clear();
    }

    private static synchronized void printResults(Node node) {
        System.out.println(node);
        node.getNeighbors().forEach(neighbor -> {
            System.out.println("Neighbor [" + neighbor.getId() + "]\t P(-1): " + neighbor.getP() + "\tDarwin(-1): "
                    + neighbor.getNeighborDarwin());
        });
    }
}
