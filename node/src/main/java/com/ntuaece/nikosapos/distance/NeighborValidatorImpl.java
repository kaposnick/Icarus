package com.ntuaece.nikosapos.distance;

import com.ntuaece.nikosapos.SimulationParameters;
import com.ntuaece.nikosapos.entities.Node;

public class NeighborValidatorImpl implements NeighborValidator {

	@Override
	public boolean areNeighbors(Node node1, Node node2) {
		return DistanceCalculator.calculateDistance(node1, node2) < SimulationParameters.MAX_NEIGHBOR_DISTANCE;
	}

}
