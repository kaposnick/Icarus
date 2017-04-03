package services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.google.gson.Gson;
import com.ntuaece.nikosapos.node.DarwinPacket;
import com.ntuaece.nikosapos.node.DiscoverPacket;
import com.ntuaece.nikosapos.node.DiscoverResponse;
import com.ntuaece.nikosapos.node.Distant;
import com.ntuaece.nikosapos.node.Link;
import com.ntuaece.nikosapos.node.Neighbor;
import com.ntuaece.nikosapos.node.Node;
import com.ntuaece.nikosapos.node.RouteDetails;

import distance.DistanceCalculator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import route.NodeRoutingInfo;
import route.RoutingPacket;

public class NeighborService extends CommunicationService implements NeighborResponsible {
    private final static String NEIGHBOR_URL = "http://localhost:8081/";
    private final static String ACTION_DISCOVER = "discovery/";
    private final static String ACTION_DARWIN = "darwin/";
    private final static String ACTION_ROUTING_EXCHANGE = "routing/";
    private final static String ACTION_ROUTING_EXCHANGE_NODE = "routingnode/";

    public NeighborService(Node node, OkHttpClient client, Gson gson) {
        super(node, client, gson);
    }

    @Override
    public void discoverNeigbors() {
        DiscoverPacket packet = DiscoverPacket.FromNode(node);
        ArrayList<Long> discoveredIDs = new ArrayList<>();
        Request.Builder builder = new Request.Builder().post(RequestBody.create(JSON, gson.toJson(packet)));
        for (int id = 0; id < 256; id++) {
            if (id == node.getId()) continue;
            Request request = builder.url(NEIGHBOR_URL + ACTION_DISCOVER + id).build();
            try {
                Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    DiscoverResponse discoverResponse = gson.fromJson(response.body().charStream(),
                                                                      DiscoverResponse.class);
                    if (discoverResponse != null) {
                        discoveredIDs.add((long) id);
                        if (!node.isNeighborWith(discoverResponse.getSourceID())) {
                            Neighbor neighbor = new Neighbor();
                            neighbor.setId(discoverResponse.getSourceID());
                            neighbor.setX(discoverResponse.getSourceX());
                            neighbor.setY(discoverResponse.getSourceY());
                            neighbor.setDistance(DistanceCalculator.calculateDistance(node.getX(),
                                                                                      node.getY(),
                                                                                      neighbor.getX(),
                                                                                      neighbor.getY()));
                            node.addNeighbor(neighbor);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // for discovering new neighbors
        node.getNeighbors().stream().forEach(neighbor -> {
            if (!discoveredIDs.contains(neighbor.getId())) {
                neighbor.getLink().setPacketReceiver(node, null);
                Link.LinkList.remove(neighbor.getLink());
                node.getNeighbors().remove(neighbor);
            }
        });
        discoveredIDs.clear();
    }

    @Override
    public void exchangeDarwinInfo() {
        DarwinPacket packet = new DarwinPacket(node);
        String body = gson.toJson(packet);
        Request.Builder builder = new Request.Builder().post(RequestBody.create(JSON, body));
        node.getNeighbors().stream().forEach(neighbor -> {
            Request request = builder.url(NEIGHBOR_URL + ACTION_DARWIN + neighbor.getId()).build();
            try {
                httpClient.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void exchangeRoutingTables() {
        RoutingPacket routingPacket = new RoutingPacket();
        routingPacket.setNodeId(node.getId());

        for (Neighbor neighbor : node.getNeighbors()) {
            NodeRoutingInfo nodeRoutingInfo = new NodeRoutingInfo();
            nodeRoutingInfo.setId(neighbor.getId());
            nodeRoutingInfo.setDistance(neighbor.getDistance());
            nodeRoutingInfo.setHops(1);
            routingPacket.getNodeRoutingTable().add(nodeRoutingInfo);
        }

        for (Iterator<Distant> iterator = node.getDistantNodes().iterator(); iterator.hasNext();) {
            Distant distant = iterator.next();
            NodeRoutingInfo nodeRoutingInfo = new NodeRoutingInfo();
            nodeRoutingInfo.setId(distant.getId());
            nodeRoutingInfo.setDistance(distant.getDistance());
            nodeRoutingInfo.setHops(distant.getTotalHops());
            routingPacket.getNodeRoutingTable().add(nodeRoutingInfo);
        }

        String body = gson.toJson(routingPacket);
        Request.Builder builder = new Request.Builder().post(RequestBody.create(JSON, body));
        node.getNeighbors().stream().forEach(neighbor -> {
            Request request = builder.url(NEIGHBOR_URL + ACTION_ROUTING_EXCHANGE + neighbor.getId()).build();
            try {
                httpClient.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public RouteDetails exchangeRoutingInformationForNode(Neighbor neighbor, long nodeId) {
        Request request = new Request.Builder().url(NEIGHBOR_URL + ACTION_ROUTING_EXCHANGE_NODE + neighbor.getId() + "/"
                + nodeId).build();
        try {
            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return gson.fromJson(response.body().charStream(), RouteDetails.class);
            } else {
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
