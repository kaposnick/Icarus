package com.ntuaece.nikosapos.deliver;

import java.io.IOException;

import com.google.gson.Gson;
import com.ntuaece.nikosapos.entities.Packet;import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DeliverServiceImpl implements DeliveryService{

	private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	private static final String SEND_ENDPOINT = "http://localhost:8081/send/";
	
	private final OkHttpClient httpClient;
	private final Gson gson;
	
	public DeliverServiceImpl(OkHttpClient httpClient, Gson gson) {
		this.httpClient = httpClient;
		this.gson = gson;
	}
	
	@Override
	public void deliverPacketToNode(long nodeId, Packet p) {
//		System.out.println("Sending packet " + p.getId());
		Request request = new Request.Builder()
				.post(RequestBody.create(JSON, gson.toJson(p)))
				.url(SEND_ENDPOINT + nodeId)
				.build();
		try {
			httpClient.newCall(request).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//				System.out.println("Packet " + p.getId() + " sent");
//			if (response.isSuccessful()) return true;
//			else return false;		
	}

}
