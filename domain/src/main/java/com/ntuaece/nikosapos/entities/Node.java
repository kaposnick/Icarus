package com.ntuaece.nikosapos.entities;

import java.util.ArrayList;
import java.util.List;

public class Node {
	private long id;
	private int x;
	private int y;
	private List<Node> neighbors = new ArrayList<Node>();

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getX() {
		return x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getY() {
		return y;
	}

	public void addNeighbor(Node node) {
		neighbors.add(node);
	}

	public List<Node> getNeighbors() {
		return neighbors;
	}

	public boolean isNeighborWith(long maybeNeighborID) {
		return neighbors
			.stream()
			.anyMatch(n -> n.getId() == maybeNeighborID);
	}

	public static class Builder {
		private long id;
		private int x;
		private int y;

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

		public Node build() {
			Node node = new Node();
			node.id = id;
			node.x = x;
			node.y = y;
			return node;
		}
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
