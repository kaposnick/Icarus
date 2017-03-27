package com.ntuaece.nikosapos.node;

public class Distant {
    private long id;
    private long relayId = -1;
    private int totalHops;
    private int distance;
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getId() {
        return id;
    }
    
    public void setRelayId(long relayId) {
        this.relayId = relayId;
    }
    
    public long getRelayId() {
        return relayId;
    }
    
    public void setTotalHops(int totalHops) {
        this.totalHops = totalHops;
    }
    
    public int getTotalHops() {
        return totalHops;
    }
    
    public void setDistance(int distance) {
        this.distance = distance;
    }
    
    public int getDistance() {
        return distance;
    }
}
