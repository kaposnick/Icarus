package com.ntuaece.nikosapos.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ntuaece.nikosapos.behaviorpacket.BehaviorUpdateEntity;

import darwin.DarwinPacket;

public class Node {
    private long id;
    private int x;
    private int y;
    
    private List<Distant> distants = new ArrayList<Distant>();
    private List<Neighbor> neighbors = new ArrayList<Neighbor>();
    private List<String> selfishNodes = new ArrayList<String>();

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

    public void updateSelfishNodeList(List<String> updatedList) {
        selfishNodes.clear();
        selfishNodes.addAll(updatedList);
    }

    public void computeNeighborMeanConnectivityRatio() {
        neighbors.stream().forEach(neighbor -> {
            float meanConnectivityRatio = 0;
            float numerator = 0;
            float fractor = 0;

            float c_im = 0;
            float c_mj = 0;

            for (DarwinPacket packet : darwinPacketList) {
                if (packet.getId() == neighbor.getId()) {
                    continue;
                    // m != j
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

            if (fractor > 0) meanConnectivityRatio = numerator / fractor;
            else meanConnectivityRatio = 0;

            neighbor.setMeanConnectivityRatio(meanConnectivityRatio);
            neighbor.clearCounters();
        });
    }
    
    public List<String> getSelfishNodes(){
        return selfishNodes;
    }
    
    public List<Distant> getDistantNodes(){
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

    public static class Builder {
        private long id;
        private int x;
        private int y;

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

        public Node build() {
            Node node = new Node();
            node.id = id;
            node.x = x;
            node.y = y;
            return node;
        }
    }

    @Override
    public String toString() {
        return String.format("Node: ID= %d (%f,%f)", id, x, y);
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
