package com.ntuaece.nikosapos.behaviorpacket;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BehaviorUpdateEntity {
	@SerializedName("neighbor") @Expose private long neighId;
	@SerializedName("ratio") @Expose private float ratio;
	
	public void setNeighId(long id) {
		this.neighId = id;
	}
	
	public long getNeighId() {
		return neighId;
	}
	
	public void setRatio(float forwardingRatio) {
		this.ratio = forwardingRatio;
	}
	
	public float getRatio() {
		return ratio;
	}
}
