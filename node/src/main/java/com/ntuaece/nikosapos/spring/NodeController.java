package com.ntuaece.nikosapos.spring;

import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ntuaece.nikosapos.entities.Packet;

import darwin.DarwinPacket;
import distance.DistanceCalculator;
import distance.NeighborValidator;
import distance.NeighborValidatorImpl;
import node.DiscoverPacket;
import node.DiscoverResponse;
import node.Distant;
import node.InterCarrier;
import node.InterCarrierImpl;
import node.Neighbor;
import node.Node;
import node.NodeList;
import node.RouteDetails;
import route.NodeRoutingInfo;
import route.RoutingPacket;

@RestController
public class NodeController {

    private final NeighborValidator neighborValidator;
    private final InterCarrier interCarrier;

    public NodeController() {
        neighborValidator = new NeighborValidatorImpl();
        interCarrier = new InterCarrierImpl();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/discovery/{id}")
    public ResponseEntity<?> onDiscovery(@PathVariable("id") String nodeID,

            @RequestBody DiscoverPacket incomingDiscoveryMessage) {

        /* verify incoming ID is a valid node ID */
        OptionalLong maxID = NodeList.GetInstance().stream().mapToLong(n -> n.getId()).max();

        if (!maxID.isPresent()
                || Long.parseLong(nodeID) > maxID.getAsLong()) { return new ResponseEntity<>(HttpStatus.NOT_FOUND); }

        /* sending node */
        Node sourceNode = new Node.Builder().setId(incomingDiscoveryMessage.getSourceID())
                                            .setX(incomingDiscoveryMessage.getSourceX())
                                            .setY(incomingDiscoveryMessage.getSourceY())
                                            .build();

        // get the target node from the common list which is visible across all
        // nodes
        Node targetNode = NodeList.GetNodeById(nodeID).get();

        if (neighborValidator.areNeighbors(sourceNode, targetNode)) {
            if (!targetNode.isNeighborWith(sourceNode.getId())) {
                Neighbor neighbor = Neighbor.FromNode(sourceNode);
                neighbor.setDistance(DistanceCalculator.calculateDistance(sourceNode, targetNode));
                targetNode.addNeighbor(Neighbor.FromNode(sourceNode));
            }
            DiscoverResponse response = new DiscoverResponse();
            response.setSourceID(targetNode.getId());
            response.setSourceX(targetNode.getX());
            response.setSourceY(targetNode.getY());
            response.setAreNeighbors(true);
            return new ResponseEntity<DiscoverResponse>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/send/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> onReceive(@PathVariable("id") String nodeID, @RequestBody Packet packet) {
        try {
            interCarrier.deliverToQueue(Long.parseLong(nodeID), packet);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/darwin/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> onDarwin(@PathVariable("id") String nodeID, @RequestBody DarwinPacket packet) {
        Optional<Node> mayNode = NodeList.GetNodeById(nodeID);
        if (mayNode.isPresent()) {
            Node node = mayNode.get();
            node.addDarwinPacket(packet);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(method = RequestMethod.GET, value = "/routingnode/{id}/{dst}/{src}")
    public ResponseEntity<?> onRouteHelp(@PathVariable("id") String nodeID, @PathVariable("dst") String wantedID, @PathVariable("src") String sourceID) {
        Optional<Node> node = NodeList.GetNodeById(nodeID);
        // validate id
        if (!node.isPresent()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        RouteDetails routeDetails = new RouteDetails();
        boolean isFound = false;

        Optional<Neighbor> neighbor = node.get().findNeighborById(Long.parseLong(wantedID));
        if (neighbor.isPresent()) {
            // is neighbor node
            isFound = true;
            routeDetails.setNodeId(Long.parseLong(nodeID));
            routeDetails.setDestinationId(Long.parseLong(wantedID));
            routeDetails.setDistance(neighbor.get().getDistance());
            routeDetails.setMaxHops(1);
        }

        if (!isFound) {
            // if not neighbor then distant
            Optional<Distant> distant = node.get().findDistantById(Long.parseLong(wantedID));
            if (distant.isPresent() && distant.get().getRelayId() != Long.parseLong(sourceID)) {
                // is distant node
                isFound = true;
                routeDetails.setNodeId(Long.parseLong(nodeID));
                routeDetails.setDestinationId(Long.parseLong(wantedID));
                routeDetails.setDistance(distant.get().getDistance());
                routeDetails.setMaxHops(distant.get().getTotalHops());
            }
        }
        routeDetails.setFound(isFound);
        return new ResponseEntity<RouteDetails>(routeDetails, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "routing/{id}")
    public ResponseEntity<?> onRoutingInfoExchange(@PathVariable("id") String nodeID,
            @RequestBody RoutingPacket routingInfoPacket) {
        Optional<Node> node = NodeList.GetNodeById(nodeID);
        if (!node.isPresent()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        // we do not accept information by selfish nodes
        if (node.get().existsInSelfishNodeList(routingInfoPacket.getNodeId()))
            return new ResponseEntity<>(HttpStatus.OK);
        Optional<Neighbor> maybeNeighbor = node.get().findNeighborById(routingInfoPacket.getNodeId());

        for (NodeRoutingInfo nodeInfo : routingInfoPacket.getNodeRoutingTable()) {
            // same node
            if (nodeInfo.getId() == Long.parseLong(nodeID)) continue;
            long distantNodeId = nodeInfo.getId();

            // common neighbors
            if (node.get().isNeighborWith(distantNodeId)) continue;

            Optional<Distant> maybeDistant = node.get().findDistantById(distantNodeId);
            if (maybeDistant.isPresent() && maybeNeighbor.isPresent()) {
                Distant distant = maybeDistant.get();
                Neighbor neighbor = maybeNeighbor.get();
                if ((distant.getTotalHops() > nodeInfo.getHops() + 1)
               || ((distant.getTotalHops() == (nodeInfo.getHops() + 1)
               && (distant.getDistance() > neighbor.getDistance() + nodeInfo.getDistance())))) {
                    distant.setRelayId(routingInfoPacket.getNodeId());
                    distant.setDistance(nodeInfo.getDistance() + neighbor.getDistance());
                    distant.setTotalHops(1 + nodeInfo.getHops());
                }
            } else {
                Distant distant = new Distant();
                distant.setId(nodeInfo.getId());
                distant.setRelayId(routingInfoPacket.getNodeId());
                distant.setDistance(nodeInfo.getDistance() + maybeNeighbor.get().getDistance());
                distant.setTotalHops(1 + nodeInfo.getHops());
                node.get().addDistant(distant);
            }
        }

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.POST, value = "selfishBroadcast/{id}")
    public ResponseEntity<?> onSelfishUpdate(@PathVariable("id") String nodeID, @RequestBody Set<Long> selfishNodes) {
        Optional<Node> node = NodeList.GetNodeById(nodeID);
        if (!node.isPresent())
            return new ResponseEntity<String>("Node " + nodeID + " not active", HttpStatus.BAD_REQUEST);
        node.get().updateSelfishNodeList(selfishNodes);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
