package com.ntuaece.nikosapos.entities;

import java.util.HashMap;
import java.util.List;

public class NodeEntity extends Node {
	private int tokens;
	private List<NodeEntity> neighborList;
	private HashMap<Long, Float> neighborConnectivityRatio;
	private float nodeConnectivityRatio;

	public void setTokens(int tokens) {
		this.tokens = tokens;
	}

	public int getTokens() {
		return tokens;
	}

	public void setNeighborList(List<NodeEntity> neighborList) {
		this.neighborList = neighborList;
	}

	public List<NodeEntity> getNeighborList() {
		return neighborList;
	}

	public void setNeighborConnectivityRatio(HashMap<Long, Float> neighborConnectivityRatio) {
		this.neighborConnectivityRatio = neighborConnectivityRatio;
	}

	public HashMap<Long, Float> getNeighborConnectivityRatio() {
		return neighborConnectivityRatio;
	}

	public void setNodeConnectivityRatio(float nodeConnectivityRatio) {
		this.nodeConnectivityRatio = nodeConnectivityRatio;
	}

	public float getNodeConnectivityRatio() {
		return nodeConnectivityRatio;
	}
}
