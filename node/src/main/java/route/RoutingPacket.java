package route;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RoutingPacket {
    @SerializedName("id") @Expose private long nodeId;
    @SerializedName("routingTable") @Expose private List<NodeRoutingInfo> nodeRoutingInfo;
    
    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
        nodeRoutingInfo = new ArrayList<>();
    }
    
    public long getNodeId() {
        return nodeId;
    }
    
    public List<NodeRoutingInfo> getNodeRoutingTable() {
        return nodeRoutingInfo;
    }
}
