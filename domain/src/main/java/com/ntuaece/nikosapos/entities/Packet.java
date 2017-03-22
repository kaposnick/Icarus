package com.ntuaece.nikosapos.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ntuaece.nikosapos.SimulationParameters;

public class Packet {
	private final static AtomicLong packetCounter = new AtomicLong();
	@SerializedName("packetId")
	@Expose
	private long id;
	@SerializedName("src")
	@Expose
	private long sourceNodeID;
	@SerializedName("dst")
	@Expose
	private long destinationNodeID;
	@SerializedName("ttl")
	@Expose
	private int hopsRemaining;
	@SerializedName("pathlist")
	@Expose
	private List<Long> pathlist;
	@SerializedName("data")
	@Expose
	private byte data;

	@SerializedName("ack")
	@Expose
	private boolean isAck = false;

	private Packet() {
	}

	public void addPathlist(Long id) {
		pathlist.add(id);
	}

	public void decrementTTL() {
		hopsRemaining--;
	}

	public void drop() {
		pathlist.clear();
		pathlist = null;
	}

	public long getId() {
		return id;
	}

	public long getSourceNodeID() {
		return sourceNodeID;
	}

	public long getDestinationNodeID() {
		return destinationNodeID;
	}

	public int getHopsRemaining() {
		return hopsRemaining;
	}

	public List<Long> getPathlist() {
		return pathlist;
	}

	public byte getData() {
		return data;
	}
	
	public void setAck(boolean isAck) {
		this.isAck = isAck;
	}
	
	public boolean isAck() {
		return isAck;
	}

	public static class Builder {
		private long id, sourceNodeID = -1, destinationID = -1;
		private byte data = 0;

		public Builder() {
			this.id = packetCounter.getAndIncrement();
		}

		public Builder setSourceNodeId(long id) {
			this.sourceNodeID = id;
			return this;
		}

		public Builder setDestinationNodeId(long id) {
			this.destinationID = id;
			return this;
		}

		public Builder setData(byte data) {
			this.data = data;
			return this;
		}

		public Packet build() {
			Packet packet = new Packet();
			packet.id = this.id;

			if (sourceNodeID < 0) {
				throw new IllegalArgumentException("You have set source node id");
			}
			packet.sourceNodeID = this.sourceNodeID;
			if (destinationID < 0) {
				throw new IllegalArgumentException("You have set destination node id");
			}
			packet.destinationNodeID = this.destinationID;
			packet.hopsRemaining = SimulationParameters.MAX_HOPS;
			packet.data = this.data;
			packet.pathlist = new ArrayList<>();
			return packet;
		}

	}

}
