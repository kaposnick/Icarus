package com.ntuaece.nikosapos.behaviorpacket;


import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BehaviorUpdate  {
	@SerializedName("nodeId") @Expose private int nodeId;
	@SerializedName("neighborList") private List<BehaviorUpdateEntity> neighborList;
	@SerializedName("relayedPackets") private int relayedPackets;
	
	public void setNodeId(int sourceNodeID) {
		this.nodeId = sourceNodeID;
	}
	
	public int getNodeID() {
		return nodeId;
	}
	
	public void setRelayedPackets(int relayedPackets) {
        this.relayedPackets = relayedPackets;
    }
	
	public int getRelayedPackets() {
        return relayedPackets;
    }
	
	public void setNeighborList(List<BehaviorUpdateEntity> neighborBehaviorList) {
		this.neighborList = neighborBehaviorList;
	}
	
	public List<BehaviorUpdateEntity> getNeighborList() {
		return neighborList;
	}
}
