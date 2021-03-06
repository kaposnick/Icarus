package distance;

import node.Node;

public class DistanceCalculator {
	public static int calculateDistance(Node node1, Node node2){
		return calculateDistance(node1.getX(), 
								node1.getY(), 
								node2.getX(), 
								node2.getY());
	}	
	
	public static int calculateDistance(int x1, int y1, int x2, int y2){
		int diff_x = x1 - x2;
		int diff_y = y1 - y2;
		return (int) Math.sqrt(Math.pow(diff_x,2) + Math.pow(diff_y, 2));
	}
}
