package com.ntuaece.nikosapos.behaviorpacket;


import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BehaviorUpdate  {
	@SerializedName("nodeId") @Expose private long nodeId;
	@SerializedName("neighborList") private List<BehaviorUpdateEntity> neighborList = new ArrayList<BehaviorUpdateEntity>();
	@SerializedName("relayedPackets") private int relayedPackets;
	@SerializedName("totalNeighbors") private int totalNeighbors;
	
	public int getTotalNeighbors() {
        return totalNeighbors;
    }
	
	public void setTotalNeighbors(int totalNeighbors) {
        this.totalNeighbors = totalNeighbors;
    }
	
	public void setNodeId(long sourceNodeID) {
		this.nodeId = sourceNodeID;
	}
	
	public long getNodeID() {
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
	
	@Override
	public String toString() {
	    StringBuilder builder  = new StringBuilder("Node " + nodeId + " relayedPackets: " + relayedPackets + " neighbors " + totalNeighbors + "\n");
	    for(BehaviorUpdateEntity entity: neighborList) {
	        builder.append("Neighbor " + entity.getNeighId() + " Ratio: " + entity.getRatio() + "\n");
	    }
	    return builder.toString();
	}
}
