package com.ntuaece.nikosapos;

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
	@SerializedName("tokens")
	@Expose
	private int tokens;

	public void setTokens(int tokens) {
		this.tokens = tokens;
	}

	public int getTokens() {
		return tokens;
	}

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

	public static class Builder {
		private long id;
		
		private double x, y;
		private int tokens;

		public Builder setId(long id) {
			this.id = id;
			return this;
		}

		public Builder setX(double x) {
			this.x = x;
			
			
			return this;
		}

		public Builder setY(double y) {
			this.y = y;
			return this;
		}

		public Builder setTokens(int tokens) {
			this.tokens = tokens;
			return this;
		}

		public Node build() {
			Node node = new Node();
			node.setX(x);
			node.setY(y);
			node.setId(id);
			node.setTokens(tokens);
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
