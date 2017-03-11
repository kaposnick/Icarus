package com.ntuaece.nikosapos.node;

public class Neighbor {
	private final static float CONNECTIVITY_RATIO_DEFAULT = 1.0f;
	
	private long id;
	private int x;
	private int y;
	private float connectivityRatio;
	
	private Link link;
	
	public static Neighbor FromNode(Node node){
		Neighbor neighbor = new Neighbor();
		neighbor.setId(node.getId());
		neighbor.setX(node.getX());
		neighbor.setY(node.getY());
		neighbor.setConnectivityRatio(CONNECTIVITY_RATIO_DEFAULT);
		return neighbor;
	}
	
	public void bindLink(Link link) {
		this.link = link;
	}
	
	public Link getLink() {
		return link;
	}
	
	public void setConnectivityRatio(float connectivityRatio) {
		this.connectivityRatio = connectivityRatio;
	}
	
	public float getConnectivityRatio() {
		return connectivityRatio;
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
