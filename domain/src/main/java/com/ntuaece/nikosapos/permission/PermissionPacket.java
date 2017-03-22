package com.ntuaece.nikosapos.permission;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PermissionPacket {
    @SerializedName("id") @Expose private long nodeId;
    
    public long getNodeId() {
        return nodeId;
    }
    
    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }
}
