package node;

import com.ntuaece.nikosapos.entities.Packet;

public interface NeighborStatsRecorder {
	 void recordPacket(Packet p);
}
