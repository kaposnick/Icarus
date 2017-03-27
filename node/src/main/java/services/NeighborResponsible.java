package services;

public interface NeighborResponsible {
    void discoverNeigbors();
    void exchangeDarwinInfo();
    void exchangeRoutingTables();
    void exchangeRoutingInformationForNode(long nodeId);
}
