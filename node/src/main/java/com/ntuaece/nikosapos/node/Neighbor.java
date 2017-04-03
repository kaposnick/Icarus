package com.ntuaece.nikosapos.node;

import java.util.concurrent.atomic.AtomicInteger;

public class Neighbor {
    private final static float CONNECTIVITY_RATIO_DEFAULT = 1.0f;

    private long id;
    private int x;
    private int y;
    private double connectivityRatio = CONNECTIVITY_RATIO_DEFAULT;
    private double meanConnectivityRatio;
    private AtomicInteger packetsSent = new AtomicInteger();
    private AtomicInteger packetsForwarded = new AtomicInteger();
    private int distance;

    private int totalPacketsSent;
    private int totalPacketsForwarded;
    private Link link;

    private double neighborDarwinForMe = 0;
    private double neighborDarwin = 0;
    private double p = 0;

    public static Neighbor FromNode(Node node) {
        Neighbor neighbor = new Neighbor();
        neighbor.id = node.getId();
        neighbor.x = node.getX();
        neighbor.y = node.getY();
        neighbor.p = 0;
        neighbor.neighborDarwinForMe = neighbor.neighborDarwin = 0;
        neighbor.packetsSent = neighbor.packetsForwarded = new AtomicInteger();
        neighbor.totalPacketsForwarded = neighbor.totalPacketsSent = 0;
        neighbor.connectivityRatio = CONNECTIVITY_RATIO_DEFAULT;
        return neighbor;
    }

    public void incrementSentPacketCounter() {
        packetsSent.incrementAndGet();
        totalPacketsSent++;
        updateConnectivityRatio();
    }

    public void incrementForwardedPacketCounter() {
        packetsForwarded.incrementAndGet();
        totalPacketsForwarded++;
        updateConnectivityRatio();
    }

    private void updateConnectivityRatio() {
        if (packetsSent.get() == 0) connectivityRatio = CONNECTIVITY_RATIO_DEFAULT;
        else connectivityRatio = packetsForwarded.get() / (packetsSent.get() * 1.0f);
    }

    public void clearCounters() {
        packetsSent.set(0);
        packetsForwarded.set(0);
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
    
    public void setP(double edp) {
        this.p = edp;
    }
    
    public double getP() {
        return p;
    }
    
    public int getPacketsSent() {
        return packetsSent.get();
    }
    
    public int getPacketsForwarded() {
        return packetsForwarded.get();
    }
    
    public void setNeighborDarwin(double neighborDarwin) {
        this.neighborDarwin = neighborDarwin;
    }
    
    public double getNeighborDarwin() {
        return neighborDarwin;
    }
}
