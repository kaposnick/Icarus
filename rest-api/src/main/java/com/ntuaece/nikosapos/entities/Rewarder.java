package com.ntuaece.nikosapos.entities;

public interface Rewarder {
    int rewardNode(NodeEntity node);
    void chargeNode(NodeEntity node, int totalRelayCost);
}
