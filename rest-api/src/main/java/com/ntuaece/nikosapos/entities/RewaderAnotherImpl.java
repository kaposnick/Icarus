package com.ntuaece.nikosapos.entities;

import com.ntuaece.nikosapos.SimulationParameters;

public class RewaderAnotherImpl implements Rewarder {

    private final static int TOKEN_FIRST_THRESHOLD = 600;
    private final static int TOKEN_SECOND_THREASHOLD = 1000;
    private final static int TOKEN_MAX = 1200;

    @Override
    public int rewardNode(NodeEntity node) {
        int initialTokens = node.getTokens();
        int reward = 0;
        if (initialTokens <= TOKEN_FIRST_THRESHOLD) {
            reward = (int) (0.5 + 2.5 * Math.pow(node.getNodeConnectivityRatio(), 2));
        } else if (initialTokens <= TOKEN_SECOND_THREASHOLD) {
            reward = (int) (0.5 + 0.5 * Math.pow(node.getNodeConnectivityRatio(), 2));
        } else {
            reward = (int) (0.01 * (TOKEN_MAX - initialTokens));
        }
        node.setTokens(initialTokens + reward);
        return reward;
    }

    @Override
    public void chargeNode(NodeEntity node, int totalRelayCost) {
        if (!node.isAllowedToSendPacketsForFree()) {
            int initialTokens = node.getTokens();
            if (initialTokens > SimulationParameters.CREDITS_INITIAL * 5.7f) {
                node.setTokens(initialTokens - (int) (2.6 * totalRelayCost));
            } else if (initialTokens > SimulationParameters.CREDITS_INITIAL * 2.5f) {
                node.setTokens(initialTokens - (int) (1.3 * totalRelayCost));
            } else node.setTokens(initialTokens - totalRelayCost);
        }
    }

}
