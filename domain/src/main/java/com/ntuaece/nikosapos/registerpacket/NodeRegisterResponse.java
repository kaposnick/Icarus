package com.ntuaece.nikosapos.registerpacket;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Expose;
import com.ntuaece.nikosapos.ResponsePacket;
import com.ntuaece.nikosapos.entities.Node;

public class NodeRegisterResponse {
	@SerializedName("x") @Expose private float x;
	@SerializedName("y") @Expose private float y;
	@SerializedName("id") @Expose private long id;
	
	public NodeRegisterResponse(Node node){
		this.x = node.getX();
		this.y = node.getY();
		this.id = node.getId();
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public float getX() {
		return x;
	}
	
	public void setY(float y) {
		this.y = y;
	}
}
