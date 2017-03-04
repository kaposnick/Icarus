package com.ntuaece.nikosapos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BehaviorUpdateEntity {
	@SerializedName("neighId") @Expose private int neighId;
	@SerializedName("ratio") @Expose private float ratio;
	
	public void setNeighId(int id) {
		this.neighId = id;
	}
	
	public int getNeighId() {
		return neighId;
	}
	
	public void setRatio(float forwardingRatio) {
		this.ratio = forwardingRatio;
	}
	
	public float getRatio() {
		return ratio;
	}
}
