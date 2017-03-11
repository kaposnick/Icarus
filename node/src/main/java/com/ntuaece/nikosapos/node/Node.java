package com.ntuaece.nikosapos.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class Node {
	private long id;
	private int x;
	private int y;
	private List<Neighbor> neighbors = new ArrayList<Neighbor>();
	private List<String> selfishNodes = new ArrayList<String>();

	public boolean isNeighborWith(long maybeNeighborID) {
		return neighbors
			.stream()
			.anyMatch(n -> n.getId() == maybeNeighborID);
	}
	
	public Optional<Neighbor> findNeighborById(long id){
		return neighbors
				.stream()
				.filter(n -> n.getId() == id)
				.findFirst();
	}
	
	public void updateSelfishNodeList(List<String> updatedList){
		selfishNodes.clear();
		selfishNodes.addAll(updatedList);
	}

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

	public void addNeighbor(Neighbor node) {
		neighbors.add(node);
	}

	public List<Neighbor> getNeighbors() {
		return neighbors;
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
		if (node.getId() == this.id)
			return true;
		return false;
	}
}
