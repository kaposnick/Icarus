package com.ntuaece.nikosapos.node;

public class Neighbor {
    private final static float CONNECTIVITY_RATIO_DEFAULT = 1.0f;

    private long id;
    private int x;
    private int y;
    private float connectivityRatio;
    private float meanConnectivityRatio;
    private int packetsSent;
    private int packetsForwarded;
    private int distance;

    private int totalPacketsSent;
    private int totalPacketsForwarded;
    private Link link;

    private double neighborDarwin, neighborP, neighborQ;
    private double edp;

    public static Neighbor FromNode(Node node) {
        Neighbor neighbor = new Neighbor();
        neighbor.id = node.getId();
        neighbor.x = node.getX();
        neighbor.y = node.getY();
        neighbor.neighborDarwin = 1f;
        neighbor.packetsSent = neighbor.packetsForwarded = 0;
        neighbor.totalPacketsForwarded = neighbor.totalPacketsSent = 0;
        neighbor.connectivityRatio = CONNECTIVITY_RATIO_DEFAULT;
        return neighbor;
    }

    public void incrementSentPacketCounter() {
        packetsSent++;
        totalPacketsSent++;
        updateConnectivityRatio();
    }

    public void incrementForwardedPacketCounter() {
        packetsForwarded++;
        totalPacketsForwarded++;
        updateConnectivityRatio();
    }

    private void updateConnectivityRatio() {
        if (packetsSent == 0) connectivityRatio = 1;
        else connectivityRatio = packetsForwarded / (packetsSent * 1.0f);
    }

    public void clearCounters() {
        packetsSent = 0;
        packetsForwarded = 0;
        updateConnectivityRatio();
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public long getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void bindLink(Link link) {
        this.link = link;
    }

    public Link getLink() {
        return link;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public float getConnectivityRatio() {
        return connectivityRatio;
    }

    public void setMeanConnectivityRatio(float meanConnectivityRatio) {
        this.meanConnectivityRatio = meanConnectivityRatio;
    }

    public float getMeanConnectivityRatio() {
        return meanConnectivityRatio;
    }

    public void setNeighborDarwin(double neighborDarwin) {
        this.neighborDarwin = neighborDarwin;
    }

    public double getNeighborDarwin() {
        return neighborDarwin;
    }

    public void setNeighborP(double neighborP) {
        this.neighborP = neighborP;
    }

    public double getNeighborP() {
        return neighborP;
    }

    public void setNeighborQ(double neighborQ) {
        this.neighborQ = neighborQ;
    }

    public double getNeighborQ() {
        return neighborQ;
    }
    
    public double getEdp() {
        return edp;
    }
    
    public int getPacketsSent() {
        return packetsSent;
    }
    
    public int getPacketsForwarded() {
        return packetsForwarded;
    }
}
