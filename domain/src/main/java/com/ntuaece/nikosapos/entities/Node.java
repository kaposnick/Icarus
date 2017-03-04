package com.ntuaece.nikosapos.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Node {
	@SerializedName("id")
	@Expose
	private long id;
	@SerializedName("x")
	@Expose
	private double x;
	@SerializedName("y")
	@Expose
	private double y;

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getX() {
		return x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getY() {
		return y;
	}

	@Override
	public String toString() {
		return String.format("Node: ID= %d (%f,%f)", id, x, y);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Node node = (Node) obj;
		if ((node.getX() == this.x && node.getY() == this.y) || node.getId() == this.id)
			return true;
		return false;
	}
}
