package services;

import node.Neighbor;
import node.RouteDetails;

public interface NeighborResponsible {
    void discoverNeigbors();
    void exchangeDarwinInfo();
    void exchangeRoutingTables();
    RouteDetails exchangeRoutingInformationForNode(Neighbor neighbor, long nodeId);
    void unregister();
}
