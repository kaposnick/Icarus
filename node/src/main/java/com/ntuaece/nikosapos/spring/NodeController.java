package com.ntuaece.nikosapos.spring;

import java.util.Optional;
import java.util.OptionalLong;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ntuaece.nikosapos.carrier.InterCarrier;
import com.ntuaece.nikosapos.carrier.InterCarrierImpl;
import com.ntuaece.nikosapos.darwin.DarwinPacket;
import com.ntuaece.nikosapos.discover.DiscoverPacket;
import com.ntuaece.nikosapos.discover.DiscoverResponse;
import com.ntuaece.nikosapos.distance.NeighborValidator;
import com.ntuaece.nikosapos.distance.NeighborValidatorImpl;
import com.ntuaece.nikosapos.entities.Packet;
import com.ntuaece.nikosapos.node.Neighbor;
import com.ntuaece.nikosapos.node.Node;
import com.ntuaece.nikosapos.node.NodeList;

@RestController
public class NodeController {

    private final NeighborValidator neighborValidator;
    private final InterCarrier interCarrier;

    public NodeController() {
        neighborValidator = new NeighborValidatorImpl();
        interCarrier = new InterCarrierImpl();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/discovery/{id}")
    public ResponseEntity onDiscovery(@PathVariable("id") String nodeID,

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
    public ResponseEntity onReceive(@PathVariable("id") String nodeID, @RequestBody Packet packet) {
        try {
            interCarrier.deliverToQueue(Long.parseLong(nodeID), packet);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/darwin/{id}", method = RequestMethod.POST)
    public ResponseEntity onDarwin(@PathVariable("id") String nodeID, @RequestBody DarwinPacket packet) {
        Optional<Node> mayNode = NodeList.GetNodeById(nodeID);
        if (mayNode.isPresent()) {
            Node node = mayNode.get();
            node.addDarwinPacket(packet);
            if (node.allDarwinPacketsArrived()) {
                node.computeNeighborMeanConnectivityRatio();
                node.clearDarwinPacketList();
            }
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

    }
}
