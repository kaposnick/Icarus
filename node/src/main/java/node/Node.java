package node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import darwin.Darwin;
import darwin.DarwinPacket;

public class Node {
    private long id;
    private int x;
    private int y;
    private List<Long> destinations = new ArrayList<Long>();

    private int totalPacketsSent;
    private int totalPacketsForwarded;
    private int totalRelayedPackets;
    private int totalDroppedPackets;
    private int totalReceivedPackets;

    private Darwin darwin;
    private double ownDarwin;
    private boolean isCheater;
    private boolean isDistant;
    private boolean isActive = false;

    private List<Distant> distants = new ArrayList<Distant>();
    private List<Neighbor> neighbors = new ArrayList<Neighbor>();

    private Map<Long, DarwinPacket> darwinPacketList = new ConcurrentHashMap<Long, DarwinPacket>();
    private Set<Long> darwinSelfishNodes = new HashSet<Long>();
    private Set<Long> icasSelfishNodes = new HashSet<Long>();
    private Set<Long> cpSelfishNodes = new HashSet<Long>();

    public boolean isNeighborWith(long maybeNeighborID) {
        synchronized (this) {
            return neighbors.stream().anyMatch(n -> n.getId() == maybeNeighborID);
        }
    }

    public Optional<Neighbor> findNeighborById(long id) {
        synchronized (this) {
            return neighbors.stream().filter(n -> n.getId() == id).findFirst();
        }
    }

    public Optional<Distant> findDistantById(long id) {
        synchronized (this) {
            Distant mDistant = null;;
            for (Distant distant : distants) {
                if (distant.getId() == id) {
                    mDistant = distant;
                    break;
                }
            }
            return Optional.ofNullable(mDistant);
        }
    }
    
    private NodeRoutingThread nodeRoutingThread;
    
    public void setNodeRoutingThread(NodeRoutingThread nodeRoutingThread) {
        this.nodeRoutingThread = nodeRoutingThread;
    }
    
    public NodeRoutingThread getNodeRoutingThread() {
        return nodeRoutingThread;
    }

    private NodeThread nodeThread;

    public void start() {
        if (!isActive) {
            if (nodeThread == null) nodeThread = new NodeThread(this);
            nodeThread.start();
            isActive = true;
        } else {
            System.out.println(this + " is already active");
        }
    }

    public void stop() {
        if (isActive) {
            if (nodeThread != null) nodeThread.kill();
            nodeThread = null;
            isActive = false;
            
            totalPacketsSent = 0;
            totalPacketsForwarded = 0;
            totalRelayedPackets = 0;
            totalDroppedPackets = 0;
            totalReceivedPackets = 0;
            distants.clear();
            neighbors.clear();
            darwinPacketList.clear();
            darwinSelfishNodes.clear();
            icasSelfishNodes.clear();
            cpSelfishNodes.clear();
        } else {
            System.out.println(this  + "is already inactive");
        }
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

    public boolean addCpSelfishNode(Long nodeId) {
        return cpSelfishNodes.add(nodeId);
    }

    public boolean removeCpSelfishNode(Long nodeId) {
        return cpSelfishNodes.removeIf(id -> id == nodeId);
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
        totalPacketsSent++;
    }

    public void incrementForwardedPacketCounter() {
        totalPacketsForwarded++;
    }

    public void incrementRelayedPacketCounter() {
        totalRelayedPackets++;
    }

    public void incrementReceivedPacketCounter() {
        totalReceivedPackets++;
    }

    public void incrementDroppedPacketCounter() {
        totalDroppedPackets++;
    }

    public int getTotalDroppedPackets() {
        return totalDroppedPackets;
    }

    public int getTotalReceivedPackets() {
        return totalReceivedPackets;
    }

    public int getRelayedPackets() {
        return totalRelayedPackets;
    }

    public int getTotalPacketsForwarded() {
        return totalPacketsForwarded;
    }

    public int getTotalPacketsSent() {
        return totalPacketsSent;
    }

    public void clearRelayedPacketsCounter() {
        totalRelayedPackets = 0;
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

    public boolean isCheater() {
        return isCheater;
    }
    
    public boolean isDistant() {
        return isDistant;
    }

    public List<Long> getDestinationList() {
        return destinations;
    }

    public boolean isActive() {
        return isActive;
    }

    public void updateDestinationList(Set<Long> destinations) {
        this.destinations.clear();
        this.destinations.addAll(destinations);
    }

    public static class Builder {
        private long id;
        private int x;
        private int y;
        private boolean selfish = false;
        private boolean distant = false;
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
        
        public Builder setDistant(boolean distant) {
            this.distant = distant;
            return this;
        }

        public Node build() {
            Node node = new Node();
            node.id = id;
            node.x = x;
            node.y = y;
            node.isCheater = selfish;
            node.isDistant = distant;
            node.totalRelayedPackets = 0;
            node.totalDroppedPackets = node.totalReceivedPackets = 0;
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

    public boolean existsInCpSelfishNodeList(long neighborId) {
        return cpSelfishNodes.contains(neighborId);
    }

    public void removeNeighbor(Neighbor unregisteringNeighbor) {
        neighbors.remove(unregisteringNeighbor);
        darwinPacketList.remove(unregisteringNeighbor.getId());
    }
}
