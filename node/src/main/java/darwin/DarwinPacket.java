package darwin;

import com.google.gson.annotations.SerializedName;
import com.ntuaece.nikosapos.behaviorpacket.BehaviorUpdateEntity;

import node.Node;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class DarwinPacket {
    @SerializedName("id") @Expose private long id;
    @SerializedName("neigh") @Expose private List<BehaviorUpdateEntity> neighborRatioList;

    public DarwinPacket(Node node) {
        this.id = node.getId();
        this.neighborRatioList = new ArrayList<>();
        node.getNeighbors().forEach(neighbor -> {
            BehaviorUpdateEntity entity = new BehaviorUpdateEntity();
            entity.setNeighId(neighbor.getId());
            entity.setRatio(neighbor.getConnectivityRatio());
            entity.setNeighborDarwin(neighbor.getDarwinMinusI());
            entity.setP(neighbor.getPMinusI());
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
