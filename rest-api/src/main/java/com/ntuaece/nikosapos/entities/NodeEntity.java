package com.ntuaece.nikosapos.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import com.ntuaece.nikosapos.SimulationParameters;

public class NodeEntity {
    public static final List<NodeEntity> NodeEntityList = new ArrayList<>();

    public synchronized static Optional<NodeEntity> GetNodeEntityById(long id) {
        return NodeEntityList.stream().filter(entity -> entity.getId() == id).findFirst();
    }

    public synchronized static boolean NodeExists(long id) {
        return NodeEntityList.stream().anyMatch(entity -> entity.getId() == id);
    }

    private long id;
    private int x, y;
    private int tokens;

    // connectivity ratio from other neighbors
    private HashMap<Long, Double> neighborConnectivityRatio;
    private double nodeConnectivityRatio;

    private NodeStatus nodeStatus;
    private boolean isDistant;
    private boolean isSelfish;
    private boolean isAllowedToSendPacketsForFree;
    private int relayedPackets;
    private int totalNeighbors;

    public synchronized static void AddNode(NodeEntity entity) {
        NodeEntityList.add(entity);
    }

    public static class Builder {
        private long id;
        private int x, y;
        private int totalNeighbors;

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

        public Builder setTotalNeighbors(int totalNeighbors) {
            this.totalNeighbors = totalNeighbors;
            return this;
        }

        public NodeEntity build() {
            NodeEntity entity = new NodeEntity();
            entity.id = id;
            entity.x = x;
            entity.y = y;
            entity.tokens = SimulationParameters.CREDITS_INITIAL;
            entity.neighborConnectivityRatio = new HashMap<>();
            entity.nodeConnectivityRatio = 1.0f;
            entity.nodeStatus = NodeStatus.ANY_SEND;
            entity.isDistant = totalNeighbors <= 1;
            entity.isSelfish = false;
            entity.isAllowedToSendPacketsForFree = false;
            entity.relayedPackets = 0;
            entity.totalNeighbors = totalNeighbors;
            return entity;
        }

    }

    public void updateConnectivityRatioIfNecessary() {
        if (neighborConnectivityRatio.size() == totalNeighbors) {
            double sum = 0.0f;
            int totalNonSelfishNeighbors = 0;
            for (Entry<Long, Double> neighborEntry : neighborConnectivityRatio.entrySet()) {
                long neighborId = neighborEntry.getKey();

                // take into account only the non-selfish nodes
                if (!NodeEntity.GetNodeEntityById(neighborId).get().isSelfish()) {
                    totalNonSelfishNeighbors++;
                    sum += neighborEntry.getValue();
                }
            }

            if (totalNonSelfishNeighbors > 0) {
                nodeConnectivityRatio = sum / totalNonSelfishNeighbors;
            } else {
                nodeConnectivityRatio = 1;
            }
            setSelfish();
            neighborConnectivityRatio.clear();
        }
    }

    public void setTotalNeighbors(int neighbors) {
        this.totalNeighbors = neighbors;
        this.isDistant = totalNeighbors <= 1;
    }

    public void setTokens(int tokens) {
        this.tokens = tokens;
    }

    public void setAllowedToSendPacketsForFree(boolean isAllowedToSentPacketsForFree) {
        this.isAllowedToSendPacketsForFree = isAllowedToSentPacketsForFree;
    }

    public void setRelayedPackets(int relayedPackets) {
        this.relayedPackets = relayedPackets;
    }

    public long getId() {
        return id;
    }

    public int getTokens() {
        return tokens;
    }

    public NodeStatus getStatus() {
        return nodeStatus;
    }

    public int getRelayedPackets() {
        return relayedPackets;
    }

    public HashMap<Long, Double> getNeighborConnectivityRatio() {
        return neighborConnectivityRatio;
    }

    public double getNodeConnectivityRatio() {
        return nodeConnectivityRatio;
    }

    public boolean isSelfish() {
        return isSelfish;
    }

    public boolean isAllowedToSendPacketsForFree() {
        return isAllowedToSendPacketsForFree;
    }

    public boolean isDistant() {
        return isDistant;
    }

    public void updateNodeStatus() {
        if (tokens >= SimulationParameters.CREDIT_STATUS_THRESHOLD) {
            nodeStatus = NodeStatus.ANY_SEND;
        } else if (tokens < SimulationParameters.CREDIT_STATUS_THRESHOLD && tokens >= 0) {
            nodeStatus = NodeStatus.NEIGHBOR_SEND;
        } else nodeStatus = NodeStatus.NO_SEND;
    }

    private void setSelfish() {
        isSelfish = nodeConnectivityRatio < SimulationParameters.CONNECTIVITY_RATIO_ICAS_THRESHOLD;
        // System.out.println("Updating selfish for node " + id + " : " +
        // isSelfish);
    }

}
