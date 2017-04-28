package mobilestatistics;

import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NodeInfo {
    @SerializedName("id") @Expose private long id;
    @SerializedName("x") @Expose private int x;
    @SerializedName("y") @Expose private int y;
    @SerializedName("destinations") @Expose private Set<Long> destinations;
    @SerializedName("active") @Expose private boolean active;
    @SerializedName("cheat") @Expose private boolean cheater;
    @SerializedName("totalSentPackets") @Expose private int totalSentPackets;
    @SerializedName("totalSuccessfullySentPackets") @Expose private int totalSuccessfullySentPackets;
    @SerializedName("totalReceivedPackets") @Expose private int totalReceivedPackets;
    @SerializedName("totalRelayedPackets") @Expose private int totalRelayedPackets;
    @SerializedName("totalDroppedPackets") @Expose private int totalDroppedPackets;
    @SerializedName("neighbors") @Expose private List<NeighborNode> neighbors;
    @SerializedName("distants") @Expose private List<DistantNode> distants;
    
    public void setId(long id) {
        this.id = id;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public void setDestinations(Set<Long> destinations) {
        this.destinations = destinations;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public void setCheater(boolean cheater) {
        this.cheater = cheater;
    }
    
    public void setTotalSentPackets(int totalSentPackets) {
        this.totalSentPackets = totalSentPackets;
    }
    
    public void setTotalSuccessfullySentPackets(int totalSuccessfullySentPackets) {
        this.totalSuccessfullySentPackets = totalSuccessfullySentPackets;
    }
    
    public void setTotalReceivedPackets(int totalReceivedPackets) {
        this.totalReceivedPackets = totalReceivedPackets;
    }
    
    public void setTotalRelayedPackets(int totalRelayedPackets) {
        this.totalRelayedPackets = totalRelayedPackets;
    }
    
    public void setTotalDroppedPackets(int totalDroppedPackets) {
        this.totalDroppedPackets = totalDroppedPackets;
    }
    
    public void setNeighbors(List<NeighborNode> neighbors) {
        this.neighbors = neighbors;
    }
    
    public void setDistants(List<DistantNode> distants) {
        this.distants = distants;
    }
}
