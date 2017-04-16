package darwin;

import java.util.Map;

import com.ntuaece.nikosapos.SimulationParameters;
import com.ntuaece.nikosapos.behaviorpacket.BehaviorUpdateEntity;

import node.Neighbor;
import node.Node;

public class DarwinAlternativeCalculator implements Darwin {
    private final static int gamma = 2;
    private final Node node;

    public DarwinAlternativeCalculator(Node node) {
        this.node = node;
    }

    @Override
    public void computeDarwin(Map<Long,DarwinPacket> darwinPacketList) {
        double p_I, p_minusI, p_DarwinI, p_DarwinMinusI;
        double q_I, q_minusI;
        double p_NewDarwinI, p_NewDarwinMinusI;

        calculatePMinusIForAllNeighbors(darwinPacketList);
        p_I = calculatePI(darwinPacketList);

        for (Neighbor neighbor : this.node.getNeighbors()) {
            neighbor.setPI(p_I);

            p_minusI = neighbor.getPMinusI();
            p_DarwinI = neighbor.getDarwinI();
            p_DarwinMinusI = neighbor.getDarwinMinusI();

            q_I = DarwinUtils.normalizeValue(p_I - p_DarwinI);
            q_minusI = DarwinUtils.normalizeValue(p_minusI - p_DarwinMinusI);

            p_NewDarwinI = DarwinUtils.normalizeValue(gamma * (q_minusI - q_I));
            neighbor.setDarwinI(p_NewDarwinI);

//            p_NewDarwinMinusI = DarwinUtils.normalizeValue(gamma * (q_I - q_minusI));
//            neighbor.setDarwinMinusI(p_NewDarwinMinusI);

            if (neighbor.getDarwinI() >= SimulationParameters.EDP) {
                node.addDarwinSelfishNode(neighbor.getId());
            } else {
                node.removeDarwinSelfishNode(neighbor.getId());
            }
        }

    }

    private double calculatePI(Map<Long,DarwinPacket> darwinPacketList) {
        double numerator = 0;
        double denominator = 0;
        for (DarwinPacket packet : darwinPacketList.values()) {
            Neighbor neighbor = node.findNeighborById(packet.getId()).get();
            for (BehaviorUpdateEntity entity : packet.getNeighborRatioList()) {
                if (entity.getNeighId() == node.getId()) {
                    // cii * cji
                    // numerator += entity.getRatio() *
                    // neighbor.getConnectivityRatio();
                    // denominator += neighbor.getConnectivityRatio();
                    // neighbor.setPForMe(1 - entity.getRatio());
                    numerator += entity.getΡForMe();
                    denominator++;
                    neighbor.setDarwinMinusI(entity.getNeighborDarwinForMe());
                }
            }
        }
        return (denominator > 0) ? numerator / denominator : 0;
//        return (denominator > 0) ? (double) (1 - numerator / denominator) : 0;
    }

    private void calculatePMinusIForAllNeighbors(Map<Long,DarwinPacket> darwinPacketList) {
        double c_minusI = 0;
        double p_minusI = 0;

        for (Neighbor neighbor : node.getNeighbors()) {
            double c_mj = 0;
            double c_im = 0;
            double denominator = 0;
            double numerator = 0;

            for (DarwinPacket packet : darwinPacketList.values()) {
                if (packet.getId() != neighbor.getId()) {
                    for (BehaviorUpdateEntity entity : packet.getNeighborRatioList()) {
                        if (entity.getNeighId() == neighbor.getId()) {
                            c_mj = entity.getRatio();
                            c_im = node.findNeighborById(packet.getId()).get().getConnectivityRatio();

                            numerator += c_im * c_mj;
                            denominator += c_im;
                        }
                    }
                }
            }

            c_im = 1;
            c_mj = neighbor.getConnectivityRatio();
            numerator += c_im * c_mj;
            denominator += c_im;

            c_minusI = numerator / denominator;
            neighbor.setMeanConnectivityRatio(c_minusI);

            p_minusI = 1 - c_minusI;
            neighbor.setPMinusI(p_minusI);
        }
    }

}
