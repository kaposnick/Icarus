package com.ntuaece.nikosapos.deliver;

import com.ntuaece.nikosapos.entities.Packet;
import com.ntuaece.nikosapos.tasks.NodeService;

public interface DeliveryService {
	void deliverPacketToNode(long nodeId, Packet p);
}
