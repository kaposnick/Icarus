package darwin;

import com.google.gson.annotations.SerializedName;
import com.ntuaece.nikosapos.behaviorpacket.BehaviorUpdateEntity;

import node.Node;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class DarwinPacket {
    @SerializedName("round") @Expose private int round;
    @SerializedName("id") @Expose private long id;
    @SerializedName("neigh") @Expose private List<BehaviorUpdateEntity> neighborRatioList;

    public DarwinPacket(Node node, int round) {
        this.id = node.getId();
        this.round = round;
        this.neighborRatioList = new ArrayList<>();
        node.getNeighbors().forEach(neighbor -> {
            BehaviorUpdateEntity entity = new BehaviorUpdateEntity();
            entity.setNeighId(neighbor.getId());
            entity.setRatio(neighbor.getConnectivityRatio());
            entity.setNeighborDarwin(neighbor.getDarwinI());
            entity.setP(neighbor.getPMinusI());
            neighborRatioList.add(entity);
        });
    }
    
    public int getRound() {
        return round;
    }

    public long getId() {
        return id;
    }

    public List<BehaviorUpdateEntity> getNeighborRatioList() {
        return neighborRatioList;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DarwinPacket another = (DarwinPacket) obj;
        if (another.getId() == this.id) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return (int) id;
    }
}
