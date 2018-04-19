package com.ntuaece.nikosapos.permission;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PermissionPacket {
    @SerializedName("id") @Expose private long nodeId;
    @SerializedName("dstId") @Expose private long dstId;
    
    public long getNodeId() {
        return nodeId;
    }
    
    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }
    
    public long getDestinationNodeId(){
        return dstId;
    }
    
    public void setDstId(long dstId) {
        this.dstId = dstId;
    }
}
