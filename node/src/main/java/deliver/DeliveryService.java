package deliver;

import com.ntuaece.nikosapos.entities.Packet;


public interface DeliveryService {
	void deliverPacketToNode(long nodeId, Packet p);
}
