package com.ntuaece.nikosapos.distance;

import com.ntuaece.nikosapos.entities.Node;

public class DistanceCalculator {
	public static double calculateDistance(Node node1, Node node2){
		return calculateDistance(node1.getX(), 
								node1.getY(), 
								node2.getX(), 
								node2.getY());
	}	
	
	public static double calculateDistance(double x1, double y1, double x2, double y2){
		double diff_x = x1 - x2;
		double diff_y = y1 - y2;
		return Math.sqrt(Math.pow(diff_x,2) + Math.pow(diff_y, 2));
	}
}
