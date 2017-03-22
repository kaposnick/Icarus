package com.ntuaece.nikosapos;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.gson.Gson;
import com.ntuaece.nikosapos.entities.NodeEntity;
import com.ntuaece.nikosapos.permission.Permission;
import com.ntuaece.nikosapos.permission.PermissionPacket;
import com.ntuaece.nikosapos.registerpacket.RegisterPacket;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Unit test for simple App.
 */
public class AppTest {

    private List<RegisterPacket> nodeList = new ArrayList<>();
    private final MediaType json = MediaType.parse("application/json; charset=utf-8");
    private final String registerEndPoint = "http://localhost:8080/register/";
    private final String permssionEndPoint = "http://localhost:8080/permission/";

    private int[] x = { 2, 1, 2, 3, 2 };
    private int[] y = { 1, 2, 2, 2, 3 };

    OkHttpClient client = new OkHttpClient();
    Gson gson = new Gson();

    @Test
    public void createNodes() {
        for (int id = 0; id < x.length; id++) {
            RegisterPacket packet = new RegisterPacket();
            packet.setId(id);
            packet.setX(x[id]);
            packet.setY(y[id]);
            packet.setTotalNeighbors(1);
            nodeList.add(packet);
        }

        for (int index = 0; index < nodeList.size(); index++) {
            Request request = new Request.Builder().post(RequestBody.create(json, gson.toJson(nodeList.get(index))))
                                                   .url(registerEndPoint)
                                                   .build();
            try {
                Response response = client.newCall(request).execute();
                assertTrue(response.isSuccessful());

                if (index == 0) {
                    response = client.newCall(request).execute();
                    assertFalse(response.isSuccessful());
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Test
    public void askPermission() {
        PermissionPacket permission = new PermissionPacket();
        permission.setNodeId(0);
        Request request = new Request.Builder().post(RequestBody.create(json, gson.toJson(permission)))
                                               .url(permssionEndPoint)
                                               .build();
        try {
            Response response = client.newCall(request).execute();
            assertTrue(response.body().string().contains(Permission.ANY_SEND.getText()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 

    }
}
