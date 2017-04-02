package com.ntuaece.nikosapos.node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.ntuaece.nikosapos.SimulationParameters;
import com.ntuaece.nikosapos.behaviorpacket.BehaviorUpdateEntity;

public class Node {
    private long id;
    private int x;
    private int y;

    private int totalPacketsSent;
    private int totalPacketsForwarded;
    private int sentPackets;
    private int forwardedPackets;
    private int relayedPackets;

    private static final int gamma = 2;

    private boolean isCheater;

    private List<Long> destinations = new ArrayList<Long>();
    private List<Distant> distants = new ArrayList<Distant>();
    private List<Neighbor> neighbors = new ArrayList<Neighbor>();
    private Set<Long> selfishNodes = new HashSet<Long>();
    private List<DarwinPacket> darwinPacketList = new ArrayList<DarwinPacket>();

    public boolean isNeighborWith(long maybeNeighborID) {
        return neighbors.stream().anyMatch(n -> n.getId() == maybeNeighborID);
    }

    public Optional<Neighbor> findNeighborById(long id) {
        return neighbors.stream().filter(n -> n.getId() == id).findFirst();
    }

    public Optional<Distant> findDistantById(long id) {
        return distants.stream().filter(dst -> dst.getId() == id).findFirst();
    }

    public void updateSelfishNodeList(Set<Long> updatedList) {
        selfishNodes.clear();
        selfishNodes.addAll(updatedList);
    }

    public boolean rejectPacket() {
        boolean SBI = sentPackets - forwardedPackets <= SimulationParameters.IFN;
        boolean condition = isCheater && SBI;
        if (sentPackets > SimulationParameters.IFN + 1) {
            clearSentPacketCounter();
            clearForwardedPacketCounter();
        }
        return condition;
    }

    public void computeNeighborMeanConnectivityRatioForNeighbors() {
        final double p_i = 1 - computeOwnC();
        neighbors.stream().forEach(neighbor -> {
            double c_minusI = 0;
            double numerator = 0;
            double fractor = 0;

            double c_im = 0;
            double c_mj = 0;

            for (DarwinPacket packet : darwinPacketList) {
                if (packet.getId() == neighbor.getId()) {
                    for (BehaviorUpdateEntity entity : packet.getNeighborRatioList()) {
                        if (entity.getNeighId() == this.id) {
                            neighbor.setNeighborDarwinForMe(entity.getNeighborDarwinForMe());
                            break;
                        }
                    }
                    // m != j
                    continue;
                }

                for (BehaviorUpdateEntity entity : packet.getNeighborRatioList()) {
                    if (entity.getNeighId() == neighbor.getId()) {
                        c_mj = entity.getRatio();
                        c_im = findNeighborById(packet.getId()).get().getConnectivityRatio();
                        numerator += (c_im * c_mj);
                        fractor += c_im;
                        break;
                    }
                }

            }
            
            c_im = 1;
            c_mj = neighbor.getConnectivityRatio();
            numerator += (c_im * c_mj);
            fractor += c_im;

            if (fractor > 0) c_minusI = numerator / fractor;
            else c_minusI = 1;
            neighbor.setMeanConnectivityRatio(c_minusI);

            double p_minusI = 1 - c_minusI;
            
            double q_minusI = DarwinUtils.normalizeValue(p_minusI - neighbor.getNeighborDarwin());
            double q_i = DarwinUtils.normalizeValue(p_i - neighbor.getNeighborDarwinForMe());
            double newDarwin = DarwinUtils.normalizeValue(gamma * (q_minusI - q_i));
            
            neighbor.setNeighborDarwin(newDarwin);
            neighbor.setEdp(p_minusI);
            if (neighbor.getEdp() > SimulationParameters.EDP) {
                // System.out.println("Node " + id + " recognized " +
                // neighbor.getId() + " as selfish.");
                selfishNodes.add(neighbor.getId());
            } else {
                // System.out.println("Node " + id + " removed " +
                // neighbor.getId() + " from selfish.");
                selfishNodes.removeIf(id -> neighbor.getId() == id);
            }
        });
    }

    private double computeOwnC() {
        double fractor = 0;
        double numerator = 0;
        for (Neighbor neighbor : neighbors) {
            for (DarwinPacket packet : darwinPacketList) {
                for (BehaviorUpdateEntity entity : packet.getNeighborRatioList()) {
                    if (entity.getNeighId() == this.id) {
                        numerator += (entity.getRatio() * neighbor.getConnectivityRatio());
                        fractor += neighbor.getConnectivityRatio();
                        break;
                    }
                }
            }
        }
        return (numerator / fractor);
    }

    public void incrementSentPacketCounter() {
        sentPackets++;
        totalPacketsSent++;
    }

    public void incrementForwardedPacketCounter() {
        forwardedPackets++;
        totalPacketsForwarded++;
    }

    public void clearSentPacketCounter() {
        sentPackets = 0;
    }

    public void clearForwardedPacketCounter() {
        forwardedPackets = 0;
    }

    public void incrementRelayedPacketCounter() {
        relayedPackets++;
    }

    public int getRelayedPackets() {
        return relayedPackets;
    }

    public void clearRelayedPacketsCounter() {
        relayedPackets = 0;
    }

    public Set<Long> getSelfishNodes() {
        return selfishNodes;
    }

    public List<Distant> getDistantNodes() {
        return distants;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public void addDarwinPacket(DarwinPacket packet) {
        darwinPacketList.add(packet);
    }

    public List<DarwinPacket> getDarwinPacketList() {
        return darwinPacketList;
    }

    public void clearDarwinPacketList() {
        darwinPacketList.clear();
    }

    public boolean allDarwinPacketsArrived() {
        return darwinPacketList.size() == neighbors.size();
    }

    public void addNeighbor(Neighbor node) {
        neighbors.add(node);
    }

    public List<Neighbor> getNeighbors() {
        return neighbors;
    }

    public void setCheater(boolean isCheater) {
        this.isCheater = isCheater;
    }

    public int getTotalPacketsSent() {
        return totalPacketsSent;
    }

    public int getTotalPacketsForwarded() {
        return totalPacketsForwarded;
    }

    public boolean isCheater() {
        return isCheater;
    }

    public List<Long> getDestinationList() {
        return destinations;
    }

    public static class Builder {
        private long id;
        private int x;
        private int y;
        private boolean selfish = false;
        private List<Long> destinationIds = new ArrayList<>();

        public Builder setId(long id) {
            this.id = id;
            return this;
        }

        public Builder setX(int x) {
            this.x = x;
            return this;
        }

        public Builder setY(int y) {
            this.y = y;
            return this;
        }

        public Builder setSelfish(boolean selfish) {
            this.selfish = selfish;
            return this;
        }

        public Builder setDestinationIds(long[] ids) {
            for (int index = 0; index < ids.length; index++) {
                destinationIds.add(ids[index]);
            }
            return this;
        }

        public Node build() {
            Node node = new Node();
            node.id = id;
            node.x = x;
            node.y = y;
            node.isCheater = selfish;
            node.sentPackets = 0;
            node.forwardedPackets = 0;
            node.relayedPackets = 0;
            node.totalPacketsSent = node.totalPacketsForwarded = 0;
            node.destinations.addAll(destinationIds);
            destinationIds.clear();
            destinationIds = null;
            return node;
        }
    }

    @Override
    public String toString() {
        return "Node [" + id + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        if (node.getId() == this.id) return true;
        return false;
    }

}
