package com.ntuaece.nikosapos.node;

import java.util.Optional;

import com.ntuaece.nikosapos.entities.Packet;

import services.NeighborResponsible;

public class RouterImpl implements Router {

    private final Node node;
    private final NeighborResponsible service;

    public RouterImpl(Node node, NeighborResponsible service) {
        this.node = node;
        this.service = service;
    }

    @Override
    public Neighbor routePacket(Packet packet) {
        Neighbor nextNode = null;

        if (packet.getSourceNodeID() == node.getId()) {
            packet.addPathlist(node.getId());
        }

        if (!packet.isAck()) {
            // check if last node was selfish. If yes drop it
            long lastNodeId = packet.getPathlist().get(packet.getPathlist().size() - 1);
            if (isSelfishNode(lastNodeId)) {
                System.out.println("Packet " + packet.getId() + " came from selfish ");
                return null;
            }

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

            if (nextNeighborIndex >= packet.getPathlist().size()) { throw new RuntimeException("Index too high!"); }

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
        Optional<Neighbor> possibleNextNode = null;

        long destinationId = p.getDestinationNodeID();
        long nextNodeId = -1;

        // boolean nextNodeSpecifiedByOldRoute = false;
        // either one will return -1
        nextNodeId = destinationNodeIsNeighbor(p);
        if (nextNodeId != -1){
            return node.findNeighborById(nextNodeId).get();
        } 
        
        nextNodeId += destinationNodeProvidedByNeighbor(p);

        // or if path not specified by any old route then -->
        // nextNodeSpecifiedeByOldRoute = -2
        boolean nextNodeSpecifiedByOldRoute = nextNodeId > -2;
        if (nextNodeSpecifiedByOldRoute && !isSelfishNode(nextNodeId + 1) && !nodeExistsInPath(p, nextNodeId + 1)) {
            // send packet to next node
            possibleNextNode = node.findNeighborById(nextNodeId + 1);
        } else {
//            System.out.println("Node " + node.getId() + ": " + (nextNodeId+1) + " is selfish");
//            System.out.println("Node " + node.getId() + " selfishNodes: " + node.getSelfishNodes());
            boolean newRouteToDstFound = false;
            long newNextNodeId = -1;
            int maxDistance = Integer.MAX_VALUE;
            int maxHops = 15; // = MAX_HOPS;

            // iterate through neighbors
            for (Neighbor neighbor : node.getNeighbors()) {
                // System.out.println("Searching for neighbors...");
                long neighborId = neighbor.getId();
                int neighborDistance = neighbor.getDistance();

                // neighbor not selfish neither in existin path
                if (!isSelfishNode(neighborId) && !nodeExistsInPath(p, neighborId)) {
                    // ask neighbor for more details
                    RouteDetails routingInformation = service.exchangeRoutingInformationForNode(neighbor,
                                                                                                destinationId);
                    // System.out.println("IsFound: "
                    // +routingInformation.isFound());
                    // if more details not found continue to the next neighbor
                    if (routingInformation == null || !routingInformation.isFound()) {
//                        System.out.println("Node " + node.getId() +" seeking for " + destinationId + " through " + neighbor.getId() + " failed.");
                        continue; 
                    }
                    int retrievedDistance = routingInformation.getDistance();
                    int retrievedHops = routingInformation.getMaxHops();
                    // System.out.println(retrievedDistance + " " +
                    // retrievedHops);
                    // trying to find the optimal route. first check
                    // totalDistance and then totalHops
                    if (neighborDistance + retrievedDistance < maxDistance) {
                        // System.out.println("New route to dst found...");
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
                possibleNextNode = node.findNeighborById(newNextNodeId);

                // if the old next node is selfish then replace with the new
                // route or if there is no next node
                if (!nextNodeSpecifiedByOldRoute || (nextNodeSpecifiedByOldRoute && isSelfishNode(nextNodeId + 1))) {
                    Distant distantNode = null;

                    for (Distant node : this.node.getDistantNodes()) {
                        if (node.getId() == destinationId) {
                            distantNode = node;
                            break;
                        }
                    }

                    if (distantNode == null) {
                        // not existing already in distant node list
                        distantNode = new Distant();
                        distantNode.setId(destinationId);
                        node.getDistantNodes().add(distantNode);
                    }
                    /*System.out.println("Node " + node.getId() + " added routing entry for " + destinationId
                            + " through " + newNextNodeId);*/
                    distantNode.setDistance(maxDistance);
                    distantNode.setTotalHops(maxHops);
                    distantNode.setRelayId(newNextNodeId);
                }
            } else {
                if (nextNodeSpecifiedByOldRoute && !nodeExistsInPath(p, nextNodeId + 1)) {
                    // in this case we don't care if the next node is selfish
                    System.out.println("Node " + node.getId() + " sending packet to selfish " + (nextNodeId + 1));
                    possibleNextNode = node.findNeighborById(nextNodeId + 1);
                } else {
                    boolean nextRouteHasBeenFound = false;
                    for (Neighbor neighbor : node.getNeighbors()) {
                        if (nodeExistsInPath(p, neighbor.getId())) continue;
                        RouteDetails routingInformation = service.exchangeRoutingInformationForNode(neighbor,
                                                                                                    destinationId);
                        if (routingInformation != null && routingInformation.isFound()) {
                            nextRouteHasBeenFound = true;
                            possibleNextNode = Optional.of(neighbor);
                            break;
                        }
                    }
                    if (!nextRouteHasBeenFound) { return null; }
                }
            }
        }

        if (possibleNextNode != null && possibleNextNode.isPresent()) { return possibleNextNode.get(); }
        return null;
    }

    private long destinationNodeIsNeighbor(Packet p) {
        Optional<Neighbor> maybeNeighbor = node.getNeighbors()
                                               .stream()
                                               .filter(neighbor -> neighbor.getId() == p.getDestinationNodeID())
                                               .findFirst();
        if (maybeNeighbor.isPresent()) {
            return maybeNeighbor.get().getId();
        } else return -1;
    }

    private long destinationNodeProvidedByNeighbor(Packet p) {
        Optional<Distant> distantNode = node.getDistantNodes()
                                            .stream()
                                            .filter(distant -> distant.getId() == p.getDestinationNodeID())
                                            .findFirst();
        if (distantNode.isPresent()) {
            return distantNode.get().getRelayId();
        } else return -1;
    }

    private boolean isSelfishNode(long id) {
        return node.getSelfishNodes().contains(id);
    }

    private static boolean nodeExistsInPath(Packet p, long nodeId) {
        return p.getPathlist().contains(nodeId);
    }

}
