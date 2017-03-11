package com.ntuaece.nikosapos.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NodeEntity  {
	public static final List<NodeEntity> NodeEntityList = new ArrayList<>();
	
	private long id;
	private int x,y;
	private int tokens;
	
	// connectivity ratio from other neighbors
	private HashMap<Long, Float> neighborConnectivityRatio;
	private float nodeConnectivityRatio;

	public void setTokens(int tokens) {
		this.tokens = tokens;
	}

	public int getTokens() {
		return tokens;
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
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public long getId() {
		return id;
	}
	
	public static class Builder {
		private long id;
		private int x,y;
		private int tokens;
		
		private float nodeConnectivityRatio;
		
		public Builder setId(long id) {
			this.id = id;
			return this;
		}
		
		public Builder setX(int x) {
			this.x = x;
			return this;
		}
		
		public Builder setY(int y) {
			this.y = y;
			return this;
		}
		
		public Builder setTokens(int tokens) {
			this.tokens = tokens;
			return this;
		}
		
		public Builder setNodeConnectivityRatio(float nodeConnectivityRatio) {
			this.nodeConnectivityRatio = nodeConnectivityRatio;
			return this;
		}
		
		public NodeEntity build(){
			NodeEntity entity = new NodeEntity();
			entity.x = x;
			entity.y = y;
			entity.id = id;
			entity.tokens = tokens;
			entity.nodeConnectivityRatio = nodeConnectivityRatio;
			entity.neighborConnectivityRatio = new HashMap<>();
			return entity;
		}
	}
	
	
}
