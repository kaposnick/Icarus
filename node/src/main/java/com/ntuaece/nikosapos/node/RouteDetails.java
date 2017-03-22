package com.ntuaece.nikosapos.node;

public class RouteDetails {
    private long nodeId;
    private boolean isFound;
    private long destinationId;
    private double distance;
    private int maxHops;
    
    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }
    
    public void setFound(boolean isFound) {
        this.isFound = isFound;
    }
    
    public void setDestinationId(long destinationId) {
        this.destinationId = destinationId;
    }
    
    public void setDistance(double distance) {
        this.distance = distance;
    }
    
    public void setMaxHops(int maxHops) {
        this.maxHops = maxHops;
    }
    
    public long getNodeId() {
        return nodeId;
    }
    
    public boolean isFound() {
        return isFound;
    }
    
    public long getDestinationId() {
        return destinationId;
    }
    
    public double getDistance() {
        return distance;
    }
    
    public int getMaxHops() {
        return maxHops;
    }
}
