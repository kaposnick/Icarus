package com.ntuaece.nikosapos.discover;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ntuaece.nikosapos.entities.Node;

public class DiscoverPacket {
	@SerializedName("nodeID")
	@Expose
	private long sourceID;
	@SerializedName("sourceX")
	@Expose
	private int sourceX;
	@SerializedName("sourceY")
	@Expose
	private int sourceY;

	public void setSourceID(long sourceID) {
		this.sourceID = sourceID;
	}

	public long getSourceID() {
		return sourceID;
	}

	public void setSourceX(int sourceX) {
		this.sourceX = sourceX;
	}

	public int getSourceX() {
		return sourceX;
	}

	public void setSourceY(int sourceY) {
		this.sourceY = sourceY;
	}

	public int getSourceY() {
		return sourceY;
	}
	
	public static DiscoverPacket FromNode(Node node){
		DiscoverPacket packet = new DiscoverPacket();
		packet.sourceID = node.getId();
		packet.sourceX = node.getX();
		packet.sourceY = node.getY();
		return packet;
	}
}
