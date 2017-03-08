package com.ntuaece.nikosapos.registerpacket;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ntuaece.nikosapos.entities.Node;

public class RegisterPacket {
	@SerializedName("id") @Expose private long id;
	@SerializedName("x") @Expose private float x;
	@SerializedName("y") @Expose private float y;
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}
	
	public static RegisterPacket FromNode(Node node){
		RegisterPacket packet = new RegisterPacket();
		packet.setX(node.getX());
		packet.setY(node.getY());
		packet.setId(node.getId());
		return packet;
	}
}
