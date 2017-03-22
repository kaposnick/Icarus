package com.ntuaece.nikosapos.entities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import com.google.gson.Gson;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SelfishNodesPublish extends TimerTask {
    private final static MediaType mediaType = MediaType.parse("application/json");
    
    private final OkHttpClient client;
    private final Gson gson;
    
    public SelfishNodesPublish(){
        client = new OkHttpClient();
        gson = new Gson();
    }

    @Override
    public void run() {
        System.out.println("Publishing selfish nodes...");
        List<Long> selfishList = new ArrayList<>();
        NodeEntity
            .NodeEntityList
            .stream()
            .filter ( node -> node.isSelfish())
            .forEach( node -> selfishList.add(node.getId()));

        RequestBody body = RequestBody.create(mediaType, gson.toJson(selfishList));
        
        NodeEntity
            .NodeEntityList
            .stream()
            .forEach( node -> {
                Request request = new Request.Builder()
                  .url("http://localhost:8081/selfishBroadcast/" + node.getId())
                  .post(body)
                  .build();
                client.newCall(request).enqueue(new Callback() {
                    
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        
                    }
                    
                    @Override
                    public void onFailure(Call call, IOException e) {
                        
                    }
                });
            });
        selfishList.clear();
    }

}
