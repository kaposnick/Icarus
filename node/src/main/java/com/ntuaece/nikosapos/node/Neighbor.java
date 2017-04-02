package com.ntuaece.nikosapos.node;

public class Neighbor {
    private final static float CONNECTIVITY_RATIO_DEFAULT = 1.0f;

    private long id;
    private int x;
    private int y;
    private double connectivityRatio = CONNECTIVITY_RATIO_DEFAULT;
    private double meanConnectivityRatio;
    private int packetsSent;
    private int packetsForwarded;
    private int distance;

    private int totalPacketsSent;
    private int totalPacketsForwarded;
    private Link link;

    private double neighborDarwinForMe = 0;
    private double neighborDarwin = 0;
    private double edp = 0;

    public static Neighbor FromNode(Node node) {
        Neighbor neighbor = new Neighbor();
        neighbor.id = node.getId();
        neighbor.x = node.getX();
        neighbor.y = node.getY();
        neighbor.edp = 0;
        neighbor.neighborDarwinForMe = neighbor.neighborDarwin = 0;
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
        if (packetsSent == 0) connectivityRatio = CONNECTIVITY_RATIO_DEFAULT;
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

    public double getConnectivityRatio() {
        return connectivityRatio;
    }

    public void setMeanConnectivityRatio(double meanConnectivityRatio) {
        this.meanConnectivityRatio = meanConnectivityRatio;
    }

    public double getMeanConnectivityRatio() {
        return meanConnectivityRatio;
    }

    public void setNeighborDarwinForMe(double neighborDarwin) {
        this.neighborDarwinForMe = neighborDarwin;
    }

    public double getNeighborDarwinForMe() {
        return neighborDarwinForMe;
    }
    
    public void setEdp(double edp) {
        this.edp = edp;
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
    
    public void setNeighborDarwin(double neighborDarwin) {
        this.neighborDarwin = neighborDarwin;
    }
    
    public double getNeighborDarwin() {
        return neighborDarwin;
    }
}
