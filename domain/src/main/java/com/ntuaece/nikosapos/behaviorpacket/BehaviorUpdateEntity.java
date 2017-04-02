package com.ntuaece.nikosapos.behaviorpacket;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BehaviorUpdateEntity {
	@SerializedName("neighbor") @Expose private long neighId;
	@SerializedName("ratio") @Expose private double ratio;
	@SerializedName("darwinForNeighbor") @Expose private double neighborDarwin;
	
	public void setNeighId(long id) {
		this.neighId = id;
	}
	
	public long getNeighId() {
		return neighId;
	}
	
	public void setRatio(double forwardingRatio) {
		this.ratio = forwardingRatio;
	}
	
	public double getRatio() {
		return ratio;
	}
	
	public void setNeighborDarwin(double edp) {
        this.neighborDarwin = edp;
    }
	
	public double getNeighborDarwinForMe() {
        return neighborDarwin;
    }
}
