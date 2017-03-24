package com.ntuaece.nikosapos.entities;

import java.util.TimerTask;

import com.ntuaece.nikosapos.SimulationParameters;

public class DistantNodeAssist extends TimerTask {
    private final static int k = 1;

    @Override
    public void run() {
       System.out.println("Running distant node assistance service...");
       NodeEntity
      .NodeEntityList
      .stream()
      .filter(node -> node.isDistant())
      .forEach(node -> {
          float cpi = node.getNodeConnectivityRatio();
          int me_th = (int)( SimulationParameters.CREDITS_INITIAL 
                           * Math.pow(cpi, 2));
          if ( !node.isSelfish() 
            && node.getTokens() <= me_th 
            && node.getRelayedPackets() <= k * SimulationParameters.DISTANT_NODES_THRESOLD_SECOND * cpi
            && !node.isAllowedToSendPacketsForFree()) {
               int additionalTokens = 0;
               float sintelestis;
               if ( node.getRelayedPackets() <= k * SimulationParameters.DISTANT_NODES_THRESHOLD_FIRST * cpi) {
                   sintelestis = 1.5f;
               } else {
                   sintelestis = 2.0f;
               }
               additionalTokens = (int) (sintelestis + SimulationParameters.CREDITS_DISTANT_INITIAL * Math.pow(cpi, 2));
               node.setTokens(node.getTokens() + additionalTokens);
          }
          boolean isAllowedToSendFree =  !node.isSelfish()
            && node.getStatus() == NodeStatus.ANY_SEND
            && node.getRelayedPackets() <= k * SimulationParameters.DISTANT_NODES_THRESOLD_SECOND * cpi
            && node.getTokens() < 0;
            
          node.setAllowedToSendPacketsForFree(isAllowedToSendFree);           
      });
    }

}
