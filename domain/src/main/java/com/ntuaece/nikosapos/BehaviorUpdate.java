package com.ntuaece.nikosapos;


import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BehaviorUpdate extends RequestPacket {
	@SerializedName("nodeId") @Expose private int nodeId;
	@SerializedName("neighborList") private List<BehaviorUpdateEntity> neighborList;
	
	public void setNodeId(int sourceNodeID) {
		this.nodeId = sourceNodeID;
	}
	
	public int getNodeID() {
		return nodeId;
	}
	
	public void setNeighborList(List<BehaviorUpdateEntity> neighborBehaviorList) {
		this.neighborList = neighborBehaviorList;
	}
	
	public List<BehaviorUpdateEntity> getNeighborList() {
		return neighborList;
	}
}
