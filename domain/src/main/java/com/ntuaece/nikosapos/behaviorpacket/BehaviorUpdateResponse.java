package com.ntuaece.nikosapos.behaviorpacket;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import com.google.gson.annotations.Expose;

public class BehaviorUpdateResponse {
	@SerializedName("nodeId") @Expose private long nodeId;
	@SerializedName("selfishNodes") @Expose private List<String> selfishNodes;
	
	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}
	
	public void setSelfishNodes(List<String> selfishNodes) {
		this.selfishNodes = selfishNodes;
	}
}
