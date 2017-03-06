package com.ntuaece.nikosapos.discover;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Expose;

public class DiscoverResponse extends DiscoverPacket {
	@SerializedName("neighbor") @Expose private boolean areNeighbors;
	
	public void setAreNeighbors(boolean areNeighbors) {
		this.areNeighbors = areNeighbors;
	}
}
