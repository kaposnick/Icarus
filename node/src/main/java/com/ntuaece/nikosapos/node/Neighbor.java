package com.ntuaece.nikosapos.node;

public class Neighbor {
	private final static float CONNECTIVITY_RATIO_DEFAULT = 1.0f;

	private long id;
	private int x;
	private int y;
	private float connectivityRatio;
	private float meanConnectivityRatio;
	private int packetsSent;
	private int packetsForwarded;
	private boolean isSelfish;

	private Link link;

	public static Neighbor FromNode(Node node) {
		Neighbor neighbor = new Neighbor();
		neighbor.id = node.getId();
		neighbor.x = node.getX();
		neighbor.y = node.getY();
		neighbor.packetsSent = 0;
		neighbor.packetsForwarded = 0;
		neighbor.connectivityRatio = CONNECTIVITY_RATIO_DEFAULT;
		neighbor.isSelfish = false;
		return neighbor;
	}

	private void updateConnectivityRatio() {
		if (packetsSent == 0)
			connectivityRatio = 0;
		else
			connectivityRatio = packetsForwarded / (packetsSent * 1.0f);
	}

	public void clearCounters() {
		packetsSent = 0;
		packetsForwarded = 0;
		updateConnectivityRatio();
	}

	public void incrementSentPacketCounter() {
		packetsSent++;
		System.out.println("Neighbor " + id + " sent " + packetsSent );
		updateConnectivityRatio();
	}

	public void incrementForwardedPacketCounter() {
		packetsForwarded++;
	      System.out.println("Neighbor " + id + " sent " + packetsForwarded );
		updateConnectivityRatio();
	}
	
	public void setMeanConnectivityRatio(float meanConnectivityRatio) {
        this.meanConnectivityRatio = meanConnectivityRatio;
    }
	
	public float getMeanConnectivityRatio() {
        return meanConnectivityRatio;
    }

	public void setSelfish(boolean isSelfish) {
		this.isSelfish = isSelfish;
	}

	public boolean isSelfish() {
		return isSelfish;
	}

	public void bindLink(Link link) {
		this.link = link;
	}

	public Link getLink() {
		return link;
	}

	public float getConnectivityRatio() {
		return connectivityRatio;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public long getId() {
		return id;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
