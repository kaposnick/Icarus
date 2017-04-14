package darwin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ntuaece.nikosapos.SimulationParameters;
import com.ntuaece.nikosapos.behaviorpacket.BehaviorUpdateEntity;

import node.Neighbor;
import node.Node;

public class DarwinCalculator implements Darwin {
    private final static double gamma = 2;
    private final Node node;

    public DarwinCalculator(Node node) {
        this.node = node;
    }

    private void p_minusI(List<DarwinPacket> darwinPacketList) {
        int strategy = 0;

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

                            if (strategy == 0) {
                                c_im = node.findNeighborById(packet.getId()).get().getConnectivityRatio();
                            } else {
                                c_im = 1;
                            }
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
            neighbor.setPMinusI(p_minusI);
        }
    }

    private double p_I(List<DarwinPacket> darwinPacketList) {
        double numerator = 0;
        double factor = 0;
        for (DarwinPacket packet : darwinPacketList) {
            Neighbor neighbor = node.findNeighborById(packet.getId()).get();
            for (BehaviorUpdateEntity entity : packet.getNeighborRatioList()) {
                if (entity.getNeighId() == this.node.getId()) {
                    neighbor.setPI(entity.getΡForMe());
                    neighbor.setDarwinI(entity.getNeighborDarwinForMe());
                    numerator += entity.getΡForMe();
                    factor++;
                }
            }
        }
        return (factor > 0) ? (double) (numerator / factor) : 0;
    }

    private double p_DarwinI(Neighbor neighbor) {
        return neighbor.getDarwinMinusI();
    }

    private double p_DarwinMinusI() {
        int numerator = 0;
        int fractor = 0;
        for (Neighbor neighbor : node.getNeighbors()) {
            numerator += neighbor.getDarwinI();
            fractor++;
        }
        return (fractor > 0) ? numerator / fractor : 0;
        // return nodeNeighbor.getDarwinForMe();
    }

    @Override
    public void computeDarwin(List<DarwinPacket> darwinPacketList) {
        double p_I = p_I(darwinPacketList);
        double p_DarwinMinusI = p_DarwinMinusI();

        p_minusI(darwinPacketList);

        double p_DarwinI = 0;
        double p_minusI = 0;

        double q_I = 0;
        double q_minusI = 0;
        double p_DarwinNew = 0;

        for (Neighbor neighbor : node.getNeighbors()) {
            // p_DarwinMinusI = p_DarwinMinusI(neighbor);
            p_DarwinI = p_DarwinI(neighbor);
            p_minusI = neighbor.getPMinusI();  // p(-i)
            p_I = neighbor.getPI();

            q_minusI = DarwinUtils.normalizeValue(p_minusI - p_DarwinMinusI);
            q_I = DarwinUtils.normalizeValue(p_I - p_DarwinI);
            p_DarwinNew = DarwinUtils.normalizeValue(gamma * (q_minusI - q_I));
            neighbor.setDarwinMinusI(p_DarwinNew);

            if (neighbor.getDarwinMinusI() >= SimulationParameters.EDP) {
                node.addDarwinSelfishNode(neighbor.getId());
            } else {
                node.removeDarwinSelfishNode(neighbor.getId());
            }
        }
    }

    private double p_I_new_alt(List<DarwinPacket> darwinPacketList) {
        double numerator = 0;
        double fractor = 0;
        for (DarwinPacket packet : darwinPacketList) {
            Neighbor neighbor = node.findNeighborById(packet.getId()).get();
            for (BehaviorUpdateEntity entity : packet.getNeighborRatioList()) {
                if (entity.getNeighId() == node.getId()) {
                    // cii * cji
                    numerator += entity.getRatio() * neighbor.getConnectivityRatio();
                    fractor += neighbor.getConnectivityRatio();
                    neighbor.setDarwinI(entity.getNeighborDarwinForMe());
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
                    numerator += entity.getΡForMe();
                    fractor++;
                    neighbor.setDarwinI(entity.getNeighborDarwinForMe());
                }
            }
        }
        return (fractor > 0) ? (double) ((numerator / fractor)) : 0f;
    }
}
