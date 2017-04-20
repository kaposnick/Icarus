package node;

import java.util.concurrent.atomic.AtomicInteger;

public class Neighbor {
    // TODO:
    private final static float CONNECTIVITY_RATIO_DEFAULT = 1.0f;

    private long id;
    private int x;
    private int y;
    private double connectivityRatio = CONNECTIVITY_RATIO_DEFAULT;
    private double meanConnectivityRatio;
    private AtomicInteger packetsSent = new AtomicInteger(1);
    private AtomicInteger packetsForwarded = new AtomicInteger(1);
    private int distance;

    private int totalPacketsSent;
    private int totalPacketsForwarded;
    private Link link;

    private double darwinI = 0;
    private double pI = 0;
    private double darwinMinusI = 0;
    private double pMinusI = 0;

    public static Neighbor FromNode(Node node) {
        Neighbor neighbor = new Neighbor();
        neighbor.id = node.getId();
        neighbor.x = node.getX();
        neighbor.y = node.getY();
        neighbor.pMinusI = neighbor.pI = 0;
        neighbor.darwinI = neighbor.darwinMinusI = 0;
        neighbor.packetsSent = new AtomicInteger(1);
        neighbor.packetsForwarded = new AtomicInteger(1);
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
        else connectivityRatio = (1.0f * packetsForwarded.get() / packetsSent.get());
    }

    private void clearCounters() {
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

    public void setDarwinI(double neighborDarwin) {
        this.darwinI = neighborDarwin;
    }

    public double getDarwinI() {
        return darwinI;
    }

    public void setPMinusI(double edp) {
        this.pMinusI = edp;
    }

    public double getPMinusI() {
        return pMinusI;
    }

    public void setPI(double pForMe) {
        this.pI = pForMe;
    }

    public double getPI() {
        return pI;
    }

    public int getPacketsSent() {
        return packetsSent.get();
    }

    public int getPacketsForwarded() {
        return packetsForwarded.get();
    }

    public void setDarwinMinusI(double neighborDarwin) {
        this.darwinMinusI = neighborDarwin;
    }

    public double getDarwinMinusI() {
        return darwinMinusI;
    }
}
