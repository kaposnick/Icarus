package route;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NodeRoutingInfo {
    @SerializedName("id") @Expose private long id;
    @SerializedName("distance") @Expose private int distance;
    @SerializedName("hops") @Expose private int hops;
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getId() {
        return id;
    }
    
    public void setDistance(int distance) {
        this.distance = distance;
    }
    
    public int getDistance() {
        return distance;
    }
    
    public void setHops(int hops) {
        this.hops = hops;
    }
    
    public int getHops() {
        return hops;
    }
}
