package com.ntuaece.nikosapos;

public class Node {
	private long id;
	private int tokens;

	private final double x, y;

	public Node(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public long getId() {
		return id;
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
