package com.ntuaece.nikosapos.entities;

import java.util.TimerTask;

import com.ntuaece.nikosapos.SimulationParameters;

public class DistantNodeAssist extends TimerTask {
    private final static int k = 1;

    @Override
    public void run() {
        System.out.println("Running distant node assistance service...");

        synchronized (NodeEntity.NodeEntityList) {

            NodeEntity.NodeEntityList.stream().forEach(node -> {
                double cpi = node.getNodeConnectivityRatio();

                // TODO: was included in Charisiadis code ( Css.extra2() ) and
                // not in the paper

//                if (!node.isSelfish()
//                        && node.getTokens() <= Math.pow(cpi, 2) * 2.5 * SimulationParameters.CREDITS_INITIAL) {
//                    int moreTokens = (int) Math.pow(cpi, 2) * SimulationParameters.CREDITS_EXTRA;
//                    node.setTokens(node.getTokens() + moreTokens);
//                }

                int me_th = (int) (SimulationParameters.CREDITS_INITIAL * Math.pow(cpi, 2));
                if (!node.isSelfish() && node.getTokens() <= me_th
                        && node.getRelayedPackets() <= k * SimulationParameters.DISTANT_NODES_THRESOLD_SECOND * cpi
                        && !node.isAllowedToSendPacketsForFree()) {
                    int additionalTokens = 0;
                    float sintelestis;
                    if (node.getRelayedPackets() <= k * SimulationParameters.DISTANT_NODES_THRESHOLD_FIRST * cpi) {
                        sintelestis = 1.5f;
                    } else {
                        sintelestis = 2.0f;
                    }
                    additionalTokens = (int) (sintelestis + SimulationParameters.CREDITS_EXTRA * Math.pow(cpi, 2));
                    node.setTokens(node.getTokens() + additionalTokens);
                }
                boolean isAllowedToSendFree = !node.isSelfish() && node.getStatus() != NodeStatus.ANY_SEND
                        && node.getRelayedPackets() <= k * SimulationParameters.DISTANT_NODES_THRESOLD_SECOND * cpi
                        && node.getTokens() < 0;

                node.setAllowedToSendPacketsForFree(isAllowedToSendFree);
            });
        }
    }
}
