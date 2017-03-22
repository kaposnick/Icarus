package com.ntuaece.nikosapos.node;

import java.util.List;
import java.util.Optional;

import com.ntuaece.nikosapos.entities.Packet;

public class RouterImpl implements Router {

    private final Node node;

    private List<String> selfishNodeList;
    private List<Distant> distantNodeList;
    private List<Neighbor> neighborNodeList;

    public RouterImpl(Node node) {
        this.node = node;
        selfishNodeList = node.getSelfishNodes();
        distantNodeList = node.getDistantNodes();
        neighborNodeList = node.getNeighbors();
    }

    @Override
    public Neighbor routePacket(Packet packet) {
        Neighbor nextNode = null;

        if (packet.getSourceNodeID() == node.getId()) {
            packet.addPathlist(node.getId());
        }

        if (!packet.isAck()) {
            // nextNode = node.getNeighbors().get(0);
            nextNode = findNextNeighbor(packet);
            if (nextNode != null) {
                packet.addPathlist(nextNode.getId());
                packet.decrementTTL();
            }
        } else {
            // ACK
            int nextNeighborIndex = -1;

            for (int i = 0; i < packet.getPathlist().size(); i++) {
                long pathNode = packet.getPathlist().get(i);
                if (pathNode == node.getId()) {
                    nextNeighborIndex = i - 1;
                    break;
                }
            }

            if (nextNeighborIndex > packet.getPathlist().size()) { throw new RuntimeException("Index too high!"); }

            // next neighbor for an ack can be retrieved from the path list
            long nextNeighborID = packet.getPathlist().get(nextNeighborIndex);
            Optional<Neighbor> neighbor = node.findNeighborById(nextNeighborID);

            if (neighbor.isPresent()) {
                nextNode = neighbor.get();
            } else throw new RuntimeException("Ack " + packet.getId() + " Node " + node.getId()
                    + " next neighbor cannot be null!");
        }

        return nextNode;
    }

    private Neighbor findNextNeighbor(Packet p) {
        Optional<Neighbor> possibleNeighbor = null;

        long destinationId = p.getDestinationNodeID();
        long nextNodeId = -1;

        // boolean nextNodeSpecifiedByOldRoute = false;
        // either one will return -1
        nextNodeId = destinationNodeIsNeighbor(p);
        nextNodeId += destinationNodeProvidedByNeighbor(p);

        // or if path not specified by any old route then -->
        // nextNodeSpecifiedeByOldRoute = -2
        boolean nextNodeSpecifiedByOldRoute = nextNodeId > -2;
        if (nextNodeSpecifiedByOldRoute && !isSelfishNode(nextNodeId + 1) && !nodeExistsInPath(p, nextNodeId + 1)) {
            // send packet to next node
            possibleNeighbor = node.findNeighborById(nextNodeId + 1);
        } else {
            boolean newRouteToDstFound = false;
            long newNextNodeId = -1;
            double maxDistance = 1000;
            int maxHops = 15; // = MAX_HOPS;

            // iterate through neighbors
            for (Neighbor neighbor : neighborNodeList) {
                long neighborId = neighbor.getId();
                double neighborDistance = neighbor.getDistance();

                // neighbor not selfish neither in existin path
                if (!isSelfishNode(neighborId) && !nodeExistsInPath(p, neighborId)) {
                    // ask neighbor for more details
                    RouteDetails routingInformation = neighbor.askRouteDetailsForNode(destinationId);

                    // if more details not found continue to the next neighbor
                    if (routingInformation == null || routingInformation.isFound() ) continue;
                    double retrievedDistance = routingInformation.getDistance();
                    int retrievedHops = routingInformation.getMaxHops();

                    // trying to find the optimal route. first check
                    // totalDistance and then totalHops
                    if (neighborDistance + retrievedDistance < maxDistance) {

                        // new route has been found with the new relay node
                        newRouteToDstFound = true;
                        newNextNodeId = neighborId;
                        maxDistance = neighborDistance + retrievedDistance;
                        maxHops = retrievedHops + 1;

                    }
                }
            }

            if (newRouteToDstFound) {
                // we have found new route
                possibleNeighbor = node.findNeighborById(newNextNodeId);

                // if the old next neighbor is selfish then replace with the new
                // route
                if (nextNodeSpecifiedByOldRoute && isSelfishNode(nextNodeId + 1)) {
                    for (Distant distantNode : distantNodeList) {
                        if (distantNode.getId() == destinationId) {
                            distantNode.setDistance(maxDistance);
                            distantNode.setTotalHops(maxHops);
                            distantNode.setRelayId(nextNodeId);
                            break;
                        }
                    }
                }
            } else {
                if (nextNodeSpecifiedByOldRoute && nodeExistsInPath(p, nextNodeId + 1)) {
                    // in this case we don't care if the next node is selfish
                    possibleNeighbor = node.findNeighborById(nextNodeId + 1);
                } else {
                    boolean nextRouteHasBeenFound = false;
                    for (Neighbor neighbor : neighborNodeList) {
                        RouteDetails routingInformation = neighbor.askRouteDetailsForNode(destinationId);
                        if (routingInformation.isFound()) {
                            nextRouteHasBeenFound = true;
                            possibleNeighbor = node.findNeighborById(routingInformation.getNodeId());
                        }
                    }
                    if (!nextRouteHasBeenFound) {
                        /* return null; */ }
                }
            }
        }

        if (possibleNeighbor != null) { return possibleNeighbor.get(); }
        // return possibleNeighbor.get
        return node.getNeighbors().get(0);
    }

    private long destinationNodeIsNeighbor(Packet p) {
        Optional<Neighbor> maybeNeighbor = neighborNodeList.stream()
                                                           .filter(neighbor -> neighbor.getId() == p.getDestinationNodeID())
                                                           .findFirst();
        if (maybeNeighbor.isPresent()) {
            return maybeNeighbor.get().getId();
        } else return -1;
    }

    private long destinationNodeProvidedByNeighbor(Packet p) {
        Optional<Distant> distantNode = distantNodeList.stream()
                                                       .filter(distant -> distant.getId() == p.getDestinationNodeID())
                                                       .findFirst();
        if (distantNode.isPresent()) {
            return distantNode.get().getRelayId();
        } else return -1;
    }

    private boolean isSelfishNode(long id) {
        return selfishNodeList.contains(id);
    }

    private static boolean nodeExistsInPath(Packet p, long nodeId) {
        return p.getPathlist().contains(nodeId);
    }

}
