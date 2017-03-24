package deliver;

import com.ntuaece.nikosapos.entities.Packet;

import tasks.NodeService;

public interface DeliveryService {
	void deliverPacketToNode(long nodeId, Packet p);
}
