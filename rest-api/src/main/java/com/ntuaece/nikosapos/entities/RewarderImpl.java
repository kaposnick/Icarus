package com.ntuaece.nikosapos.entities;

import com.ntuaece.nikosapos.SimulationParameters;

public class RewarderImpl implements Rewarder {
    private final static float a = 0.5f;
    private final static float b = 2.3f;

    @Override
    public int rewardNode(NodeEntity node) {
        int reward = (int) (a + b * Math.pow(node.getNodeConnectivityRatio(), 2));
        node.setTokens(node.getTokens() + reward);
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
