package node;

import com.ntuaece.nikosapos.entities.Packet;

public interface Router {
    
    /**
     * 
     * @param p packet to route
     * @return true if next hop is found
     *         false if next hop not found and has to drop
     */        
    Neighbor routePacket(Packet p);
}
