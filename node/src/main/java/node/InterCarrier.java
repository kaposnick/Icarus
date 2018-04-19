package node;

import com.ntuaece.nikosapos.entities.Packet;

public interface InterCarrier {
	void deliverToQueue(long id, Packet p);
}
