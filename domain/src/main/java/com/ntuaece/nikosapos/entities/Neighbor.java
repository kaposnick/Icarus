package com.ntuaece.nikosapos.entities;

public class Neighbor {
	private long id;
	private int x;
	private int y;
	
	public static Neighbor FromNode(Node node){
		Neighbor neighbor = new Neighbor();
		neighbor.setId(node.getId());
		neighbor.setX(node.getX());
		neighbor.setY(node.getY());
		return neighbor;
	}
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	
}
