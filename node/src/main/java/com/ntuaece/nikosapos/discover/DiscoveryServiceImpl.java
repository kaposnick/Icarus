package com.ntuaece.nikosapos.discover;

import java.io.IOException;

import com.google.gson.Gson;
import com.ntuaece.nikosapos.node.Neighbor;
import com.ntuaece.nikosapos.node.Node;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DiscoveryServiceImpl implements DiscoveryService {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String DISCOVERY_ENDPOINT = "http://localhost:8081/discovery/";

    private final Node node;
    private final DiscoverPacket packet;
    private OkHttpClient httpClient;
    private Request.Builder requestBuilder;
    private Gson gson;

    public DiscoveryServiceImpl(Node node) {
        this.node = node;
        this.packet = DiscoverPacket.FromNode(node);
    }

    public void setHttpClient(OkHttpClient client) {
        this.httpClient = client;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    @Override
    public boolean discoverForNeighbors() {
        boolean result = true;

        validateResources();
        // Will use later to get to know if nodes have quitted
        // long neighboringIDs[] = node.getNeighbors().stream().mapToLong(node
        // -> node.getId()).toArray();

        /* Supposing we have maximum 256 nodes */
        for (int discoveringID = 0; discoveringID <= 256; discoveringID++) {

            // Target my own rest-api
            if (discoveringID == node.getId()) continue;
            Request request = requestBuilder.url(DISCOVERY_ENDPOINT + discoveringID).build();

            try {
                Response response = httpClient.newCall(request).execute();

                if (response.isSuccessful()) {
                    DiscoverResponse discoverResponse = gson.fromJson(response.body().charStream(),
                                                                      DiscoverResponse.class);
                    if (discoverResponse != null) {
                        if (!node.isNeighborWith(discoverResponse.getSourceID())) {
                            Neighbor neighbor = new Neighbor();
                            neighbor.setId(discoverResponse.getSourceID());
                            neighbor.setX(discoverResponse.getSourceX());
                            neighbor.setY(discoverResponse.getSourceY());
                            node.addNeighbor(neighbor);
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error discovering neighbors in node: " + node.getId());
                result = false;
                e.printStackTrace();
            }
        }
        // System.out.println("Node " + node.getId() + " has " +
        // node.getNeighbors().size() + " neighbors");
        return result;
    }

    private void validateResources() {
        if (httpClient == null) httpClient = new OkHttpClient();
        if (gson == null) gson = new Gson();
        if (requestBuilder == null)
            requestBuilder = new Request.Builder().post(RequestBody.create(JSON, gson.toJson(packet)));

    }
}
