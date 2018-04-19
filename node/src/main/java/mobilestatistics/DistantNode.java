package mobilestatistics;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DistantNode {
    @SerializedName("id") @Expose private long id;
    @SerializedName("distance") @Expose private int distance;
    @SerializedName("hops") @Expose private int hops;
    @SerializedName("relayId") @Expose private long relayId;
    
    public void setId(long id) {
        this.id = id;
    }
    
    public void setDistance(int distance) {
        this.distance = distance;
    }
    
    public void setHops(int hops) {
        this.hops = hops;
    }
    
    public void setRelayId(long relayId) {
        this.relayId = relayId;
    }
}
