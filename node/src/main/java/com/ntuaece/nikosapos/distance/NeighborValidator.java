package com.ntuaece.nikosapos.distance;

import com.ntuaece.nikosapos.entities.Node;

public interface NeighborValidator {
	boolean areNeighbors(Node node1, Node node2);
}
