package com.ntuaece.nikosapos.entities;

public class Packet {
	private long id;

	public long getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Packet: " + id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		Packet packet = (Packet) obj;
		if (id == packet.getId()) {
			return true;
		}
		return false;
	}
}
