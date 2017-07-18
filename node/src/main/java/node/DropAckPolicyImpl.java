package node;

import java.util.Random;

import com.ntuaece.nikosapos.entities.Packet;

public class DropAckPolicyImpl implements DropAckPolicy {
    private final static double ackLossPercentage = 0.0001f;

    private final Node node;

    private boolean isFollowingCurrentPolicy = true;

    public DropAckPolicyImpl(Node node) {
        this.node = node;
    }

    @Override
    public boolean drop(Packet p) {
        if (!isFollowingCurrentPolicy) {
            return false;
        } else if (node.isCheater()) { 
            return new Random().nextDouble() <= ackLossPercentage; 
        }
        return false;
    }

    @Override
    public void follow(boolean follow) {
        isFollowingCurrentPolicy = follow;
    }

}
