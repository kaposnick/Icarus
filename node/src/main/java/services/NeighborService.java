package services;
import java.io.IOException;

import com.google.gson.Gson;
import com.ntuaece.nikosapos.node.DarwinPacket;
import com.ntuaece.nikosapos.node.DiscoverPacket;
import com.ntuaece.nikosapos.node.DiscoverResponse;
import com.ntuaece.nikosapos.node.Neighbor;
import com.ntuaece.nikosapos.node.Node;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NeighborService extends CommunicationService implements NeighborResponsible {
    private final static String NEIGHBOR_URL = "http://localhost:8081/";
    private final static String ACTION_DISCOVER = "discovery/";
    private final static String ACTION_DARWIN = "darwin/";

    public NeighborService(Node node, OkHttpClient client, Gson gson) {
        super(node,client,gson);
    }

    @Override
    public void discoverNeigbors() {
        DiscoverPacket packet = DiscoverPacket.FromNode(node);
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
                e.printStackTrace();
            }
        }
    }

    @Override
    public void exchangeDarwinInfo() {
        DarwinPacket packet = new DarwinPacket(node);
        String body = gson.toJson(packet);
        Request.Builder builder = new Request.Builder().post(RequestBody.create(JSON, body));
        node.getNeighbors()
            .stream()
            .forEach(neighbor -> {
                Request request = builder.url(NEIGHBOR_URL + ACTION_DARWIN + neighbor.getId()).build();
                try {
                    httpClient.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
    }

}
