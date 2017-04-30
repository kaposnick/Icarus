package com.ntuaece.nikosapos;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ntuaece.nikosapos.behaviorpacket.BehaviorUpdate;
import com.ntuaece.nikosapos.behaviorpacket.BehaviorUpdateEntity;
import com.ntuaece.nikosapos.entities.DistantValidator;
import com.ntuaece.nikosapos.entities.NodeEntity;
import com.ntuaece.nikosapos.entities.NodeStatus;
import com.ntuaece.nikosapos.entities.Rewarder;
import com.ntuaece.nikosapos.entities.RewarderImpl;
import com.ntuaece.nikosapos.permission.Permission;
import com.ntuaece.nikosapos.permission.PermissionPacket;
import com.ntuaece.nikosapos.registerpacket.RegisterPacket;

@RestController
public class IcasController {

    private Rewarder rewarder;
    private DistantValidator distantValidator;

    public IcasController() {
        rewarder = new RewarderImpl();
        distantValidator = new DistantValidator() {

            @Override
            public boolean isDistant(int totalneighbors) {
                return totalneighbors <= 2;
            }
        };
    }

    @RequestMapping(method = RequestMethod.GET, value = "/hi")
    public String hello() {
        return "Hello World";
    }

    // Tested
    @RequestMapping(method = RequestMethod.POST, value = "/register")
    public ResponseEntity<?> registerNode(@RequestBody RegisterPacket registerPacket) {
        if (registerPacket.getId() != -1) {
            if (NodeEntity.NodeExists(registerPacket.getId())) { return new ResponseEntity<>(HttpStatus.CONFLICT); }
            NodeEntity nodeEntity = new NodeEntity.Builder().setId(registerPacket.getId())
                                                            .setX(registerPacket.getX())
                                                            .setY(registerPacket.getY())
                                                            .setTotalNeighbors(registerPacket.getTotalNeighbors())
                                                            .build();
            NodeEntity.AddNode(nodeEntity);
            System.out.println("Node " + nodeEntity.getId() + " registered");
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            System.err.println("Node " + registerPacket.getId() + " COULDN'T REGISTER!");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/permission")
    public ResponseEntity<?> askPermission(@RequestBody PermissionPacket permissionPacket) {
        Optional<NodeEntity> node = NodeEntity.GetNodeEntityById(permissionPacket.getNodeId());
        if (node.isPresent()) {
            NodeStatus status = node.get().getStatus();
            if (node.get().isAllowedToSendPacketsForFree()) {
                System.out.println("Node [" + node.get().getId() + "] is allowed to sent packets for free");
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                switch (status) {
                    case ANY_SEND:
                        return new ResponseEntity<>(HttpStatus.OK);
                    case NEIGHBOR_SEND:
                        if (node.get()
                                .getNeighborConnectivityRatio()
                                .containsKey(permissionPacket.getDestinationNodeId())) { return new ResponseEntity<>(HttpStatus.OK); }
                    case NO_SEND:
                    default:
                        System.out.println("Node " + permissionPacket.getNodeId() + " not permitted for "
                                + permissionPacket.getDestinationNodeId() + " \tTokens: " + node.get().getTokens() 
                                + "\t CPi: " + node.get().getNodeConnectivityRatio());
                        return new ResponseEntity<String>("Node " + permissionPacket.getNodeId() + " is not permitted ",
                                                          HttpStatus.NOT_ACCEPTABLE);

                }
            }
        } else {
            return new ResponseEntity<String>("Node " + permissionPacket.getNodeId() + " not registered",
                                              HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/deliverysuccessful")
    public ResponseEntity<?> deliverySuccessful(@RequestBody List<Long> nodeIdList) {

        if (nodeIdList.size() < 2) { return new ResponseEntity<String>("Should contain at least two ids",
                                                                       HttpStatus.BAD_REQUEST); }

        // System.out.println("Successful delivery of packet");
        // System.out.println(nodeIdList);

        // no relays --> no rewards
        if (nodeIdList.size() == 2) { return new ResponseEntity<>(HttpStatus.OK); }

        int totalRelayRewards = 0;

        // not take into account source and destination for relay cost
        for (int index = 1; index < nodeIdList.size() - 1; index++) {
            Long nodeId = nodeIdList.get(index);
            Optional<NodeEntity> node = NodeEntity.GetNodeEntityById(nodeId);
            if (node.isPresent()) {
                // reward relay nodes
                totalRelayRewards += rewarder.rewardNode(node.get());
                // System.out.println("Node " + node.get().getId() + " tokens "
                // + node.get().getTokens());
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        // charge source node
        Optional<NodeEntity> sourceNode = NodeEntity.GetNodeEntityById(nodeIdList.get(0));
        if (sourceNode.isPresent()) {
            rewarder.chargeNode(sourceNode.get(), totalRelayRewards);
            // System.out.println("Node " + sourceNode.get().getId() + " tokens
            // " + sourceNode.get().getTokens());
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/neighborUpdate")
    public ResponseEntity<?> updateNeighborBehavior(@RequestBody BehaviorUpdate behaviorPacket) {
        // the sender node id

        long senderNodeId = behaviorPacket.getNodeID();

        // update the relayed packets field so that it gets computed for the
        // distant nodes
        NodeEntity sendingNode = NodeEntity.GetNodeEntityById(senderNodeId).get();
        sendingNode.setRelayedPackets(behaviorPacket.getRelayedPackets());
        sendingNode.setTotalNeighbors(behaviorPacket.getTotalNeighbors());

        // iterate through all the neighbors
        for (BehaviorUpdateEntity behaviorUpdateEntity : behaviorPacket.getNeighborList()) {
            long neighborId = behaviorUpdateEntity.getNeighId();

            // for each neighbor add the ConnectivityRatio sender node perceives
            Optional<NodeEntity> neighborNode = NodeEntity.GetNodeEntityById(neighborId);
            if (neighborNode.isPresent()) {
                neighborNode.get().getNeighborConnectivityRatio().put(senderNodeId, behaviorUpdateEntity.getRatio());
            } else {
                System.out.println(neighborId + " not registered");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // if all the packets from the neighbors have arrived then update
            // the connectivity ratio of
            // the node and clear the ratios
            neighborNode.get().updateConnectivityRatioIfNecessary();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/unregister")
    public ResponseEntity<?> unregister(@RequestBody Long nodeId) {
        Optional<NodeEntity> maybeNode = NodeEntity.GetNodeEntityById(nodeId);
        if (!maybeNode.isPresent()) return null;
        NodeEntity.RemoveNode(maybeNode.get());

        return new ResponseEntity(HttpStatus.OK);
    }
}
