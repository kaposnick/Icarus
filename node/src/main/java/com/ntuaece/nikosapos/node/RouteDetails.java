package com.ntuaece.nikosapos.node;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RouteDetails {
    @SerializedName("nodeId") @Expose private long nodeId;
    @SerializedName("isFound") @Expose private boolean isFound;
    @SerializedName("destinationId") @Expose private long destinationId;
    @SerializedName("distance") @Expose private int distance;
    @SerializedName("maxHops") @Expose private int maxHops;
    
    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }
    
    public void setFound(boolean isFound) {
        this.isFound = isFound;
    }
    
    public void setDestinationId(long destinationId) {
        this.destinationId = destinationId;
    }
    
    public void setDistance(int distance) {
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
    
    public int getDistance() {
        return distance;
    }
    
    public int getMaxHops() {
        return maxHops;
    }
}
