package com.ntuaece.nikosapos.node;

import com.google.gson.annotations.SerializedName;
import com.ntuaece.nikosapos.behaviorpacket.BehaviorUpdateEntity;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class DarwinPacket {
    @SerializedName("id") @Expose private long id;
    @SerializedName("neigh") @Expose private List<BehaviorUpdateEntity> neighborRatioList = new ArrayList<>();

    public DarwinPacket(Node node) {
        this.id = node.getId();
        node.getNeighbors().stream().forEach(neighbor -> {
            BehaviorUpdateEntity entity = new BehaviorUpdateEntity();
            entity.setNeighId(neighbor.getId());
            entity.setRatio(neighbor.getConnectivityRatio());
            neighborRatioList.add(entity);
        });
    }

    public long getId() {
        return id;
    }

    public List<BehaviorUpdateEntity> getNeighborRatioList() {
        return neighborRatioList;
    }

}
