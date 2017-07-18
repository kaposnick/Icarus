package node;

import com.ntuaece.nikosapos.entities.Packet;

public interface DropAckPolicy {
    boolean drop(Packet p);
    void follow(boolean follow);
}
