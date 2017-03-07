package com.ntuaece.nikosapos.registerpacket;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ntuaece.nikosapos.RequestPacket;

public class NodeRegisterRequest {
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
}
