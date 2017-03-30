package services;

import com.ntuaece.nikosapos.node.Neighbor;
import com.ntuaece.nikosapos.node.RouteDetails;

public interface NeighborResponsible {
    void discoverNeigbors();
    void exchangeDarwinInfo();
    void exchangeRoutingTables();
    RouteDetails exchangeRoutingInformationForNode(Neighbor neighbor, long nodeId);
}
