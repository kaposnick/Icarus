package services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ntuaece.nikosapos.behaviorpacket.BehaviorUpdate;
import com.ntuaece.nikosapos.behaviorpacket.BehaviorUpdateEntity;
import com.ntuaece.nikosapos.entities.Packet;
import com.ntuaece.nikosapos.permission.PermissionPacket;
import com.ntuaece.nikosapos.registerpacket.RegisterPacket;

import node.Neighbor;
import node.Node;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class IcasService extends CommunicationService implements IcasResponsible {
    private final static String URL_ICAS = "http://localhost:8080/";
    private final static String ACTION_REGISTER = "register";
    private final static String ACTION_UPDATE = "neighborUpdate";
    private final static String ACTION_PERMISSION = "permission";
    private final static String ACTION_DELIVERY = "deliverysuccessful";

    public IcasService(Node node, OkHttpClient client, Gson gson) {
        super(node, client, gson);
    }

    @Override
    public boolean askForSendPermission(long destinationNodeId) {
        assertValidResources();
        PermissionPacket packet = new PermissionPacket();
        packet.setNodeId(node.getId());
        packet.setDstId(destinationNodeId);
        Request request = new Request.Builder().post(RequestBody.create(JSON, gson.toJson(packet)))
                                               .url(URL_ICAS + ACTION_PERMISSION)
                                               .build();
        try {
            Response response = httpClient.newCall(request).execute();
            return response.isSuccessful();
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public void confirmSuccessfulDelivery(Packet packet) {
        assertValidResources();
        Request request = new Request.Builder().post(RequestBody.create(JSON, gson.toJson(packet.getPathlist())))
                                               .url(URL_ICAS + ACTION_DELIVERY)
                                               .build();

        httpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }

            @Override
            public void onFailure(Call call, IOException e) {

            }
        });

    }

    @Override
    public void registerToIcas() {
        assertValidResources();
        RegisterPacket packet = new RegisterPacket.Builder().setId(node.getId())
                                                            .setX(node.getX())
                                                            .setY(node.getY())
                                                            .setTotalNeighbors(node.getNeighbors().size())
                                                            .build();
        String packetBody = gson.toJson(packet);
        Request request = new Request.Builder().post(RequestBody.create(JSON, packetBody))
                                               .url(URL_ICAS + ACTION_REGISTER)
                                               .build();
        httpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }

            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
    }

    @Override
    public void updateNeighborBehavior() {
        assertValidResources();
        BehaviorUpdate packet = new BehaviorUpdate();
        packet.setNodeId(node.getId());
        packet.setRelayedPackets(node.getRelayedPackets());
        packet.setTotalNeighbors(node.getNeighbors().size());
        List<BehaviorUpdateEntity> mList = new ArrayList<>();
        node.clearRelayedPacketsCounter();
        for (Neighbor neighbor : node.getNeighbors()) {
            BehaviorUpdateEntity entity = new BehaviorUpdateEntity();
            entity.setNeighId(neighbor.getId());
            entity.setRatio(neighbor.getConnectivityRatio());
            mList.add(entity);
        }
        packet.setNeighborList(mList);
        String packetBody = gson.toJson(packet);
        Request request = new Request.Builder().post(RequestBody.create(JSON, packetBody))
                                               .url(URL_ICAS + ACTION_UPDATE)
                                               .build();
        httpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
            }

            @Override
            public void onFailure(Call call, IOException e) {
            }
        });

    }

}
