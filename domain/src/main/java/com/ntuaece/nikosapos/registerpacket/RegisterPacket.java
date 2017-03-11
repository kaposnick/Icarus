package com.ntuaece.nikosapos.registerpacket;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterPacket {
	@SerializedName("id")
	@Expose
	private long id;
	@SerializedName("x")
	@Expose
	private int x;
	@SerializedName("y")
	@Expose
	private int y;
	@SerializedName("neighbors")
	@Expose
	private int totalNeighbors;

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setTotalNeighbors(int totalNeighbors) {
		this.totalNeighbors = totalNeighbors;
	}

	public int getTotalNeighbors() {
		return totalNeighbors;
	}

	public static class Builder {
		private int x, y;
		private long id;
		private int totalNeighbors;

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

		public Builder setTotalNeighbors(int total) {
			this.totalNeighbors = total;
			return this;
		}
		
		public RegisterPacket build(){
			RegisterPacket packet = new RegisterPacket();
			packet.id = id;
			packet.x = x;
			packet.y = y;
			packet.totalNeighbors = totalNeighbors;
			return packet;
		}
	}
}
