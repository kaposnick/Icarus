package node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import darwin.Darwin;
import darwin.DarwinPacket;

public class Node {
    private long id;
    private int x;
    private int y;

    private int totalPacketsSent;
    private int totalPacketsForwarded;
    private int sentPackets;
    private int forwardedPackets;
    private int relayedPackets;

    private Darwin darwin;
    private double ownDarwin;
    private boolean isCheater;

    private List<Long> destinations = new ArrayList<Long>();
    private List<Distant> distants = new ArrayList<Distant>();
    private List<Neighbor> neighbors = new ArrayList<Neighbor>();

    private Map<Long,DarwinPacket> darwinPacketList = new ConcurrentHashMap<Long,DarwinPacket>();
    private Set<Long> darwinSelfishNodes = new HashSet<Long>();
    private Set<Long> icasSelfishNodes = new HashSet<Long>();

    public boolean isNeighborWith(long maybeNeighborID) {
        synchronized (this) {
            return neighbors.stream().anyMatch(n -> n.getId() == maybeNeighborID);
        }
    }

    public Optional<Neighbor> findNeighborById(long id) {
        return neighbors.stream().filter(n -> n.getId() == id).findFirst();
    }

    public Optional<Distant> findDistantById(long id) {
        Distant mDistant = null;;
        for (Distant distant : distants) {
            if (distant.getId() == id) {
                mDistant = distant;
                break;
            }
        }
        return Optional.ofNullable(mDistant);
    }

    public void updateSelfishNodeList(Set<Long> updatedList) {
        icasSelfishNodes.clear();
        icasSelfishNodes.addAll(updatedList);
    }

    public void setDarwinImpl(Darwin darwin) {
        this.darwin = darwin;
    }

    public void executeDarwinAlgorithm() {
        if (darwin != null) {
            darwin.computeDarwin(darwinPacketList);
        } else {
            throw new NullPointerException("Darwin calculator should not be null");
        }
    }

    public void addDarwinPacket(DarwinPacket packet) {
        darwinPacketList.put(packet.getId(), packet);
    }

    public boolean addDarwinSelfishNode(Long nodeId) {
        return darwinSelfishNodes.add(nodeId);
    }

    public boolean removeDarwinSelfishNode(Long nodeId) {
        return darwinSelfishNodes.removeIf(id -> id == nodeId);
    }

    public boolean existsInSelfishNodeList(Long id) {
        return darwinSelfishNodes.contains(id) || icasSelfishNodes.contains(id);
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
        return darwinSelfishNodes;
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

    public Map<Long, DarwinPacket> getDarwinPacketList() {
        return darwinPacketList;
    }

    private void clearDarwinPacketList() {
        darwinPacketList.clear();
    }

    public boolean allDarwinPacketsArrived() {
        return darwinPacketList.size() == neighbors.size();
    }

    public void addNeighbor(Neighbor node) {
        neighbors.add(node);
    }

    public void setOwnDarwin(double ownDarwin) {
        this.ownDarwin = ownDarwin;
    }

    public double getOwnDarwin() {
        return ownDarwin;
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

    public void addDistant(Distant distant) {
        synchronized (this) {
            distants.add(distant);
        }
    }

}
