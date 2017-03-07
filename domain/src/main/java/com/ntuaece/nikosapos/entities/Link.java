package com.ntuaece.nikosapos.entities;

import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

public class Link {
	private final static int FIBER_LIGHT_SPEED = 3000;

	private final long id;
	private final static AtomicLong linkCounter = new AtomicLong();

	private final double distance;
	private final Node firstEndPoint;
	private final Node secondEndPoint;

	private Queue<Packet> firstEndPointUpLink;
	private Queue<Packet> firstEndPointDownLink;
	private Queue<Packet> secondEndPointUpLink;
	private Queue<Packet> secondEndPointDownLink;

	private Timer timer;

	public Link(final Node node1, final Node node2, final double distance) {
		this.id = linkCounter.getAndIncrement();

		this.firstEndPoint = node1;
		this.secondEndPoint = node2;
		this.distance = distance;

		firstEndPointUpLink = new ConcurrentLinkedQueue<>();
		firstEndPointDownLink = new ConcurrentLinkedQueue<>();
		secondEndPointUpLink = new ConcurrentLinkedQueue<>();
		secondEndPointDownLink = new ConcurrentLinkedQueue<>();

		timer = new Timer(true);
	}

	public void addPacketToLink(Node n, Packet p) {
		if (willDrop()) {
			return;
		}
		if (n.equals(firstEndPoint)) {
			firstEndPointUpLink.offer(p);
			setTimer(1);
		} else if (n.equals(secondEndPoint)) {
			secondEndPointUpLink.offer(p);
			setTimer(2);
		} else
			throw new IllegalArgumentException("Node " + n.getId() + " is not an endpoint of Link " + id);
	}

	public Optional<Packet> removePacketFromLink(Node n) {
		if (n.equals(firstEndPoint))
			return Optional.ofNullable(firstEndPointDownLink.poll());
		else if (n.equals(secondEndPoint))
			return Optional.ofNullable(secondEndPointDownLink.poll());
		else
			throw new IllegalArgumentException("Node " + n.getId() + " is not an endpoint of Link " + id);
	}

	private void sendPacketToNode(Packet p, Node nextNode) {
		// TODO Auto-generated method stub

	}

	private boolean willDrop() {
		return new Random().nextDouble() < 0.3f;
	}

	private void setTimer(final int i) {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				Packet p = null;
				if (i == 1)
					p = firstEndPointUpLink.poll();
				else if (i == 2)
					p = secondEndPointUpLink.poll();
				if (p != null) {
					if (i == 1) {
						sendPacketToNode(p, secondEndPoint);
					} else if (i == 2) {
						sendPacketToNode(p, firstEndPoint);
					}
				}
			}
		}, calculateDeliveryTimerBasedOnDistance());
	}

	private long calculateDeliveryTimerBasedOnDistance() {
		return 50;
	}

	@Override
	public String toString() {
		return String.format("Link: ID =  %d\t FirstEP= %d\t SecondEP= %d", id, firstEndPoint.getId(),
				secondEndPoint.getId());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Link link = (Link) obj;
		if (link.firstEndPoint.getId() == firstEndPoint.getId()
				&& link.secondEndPoint.getId() == secondEndPoint.getId())
			return true;
		return false;
	}
}
