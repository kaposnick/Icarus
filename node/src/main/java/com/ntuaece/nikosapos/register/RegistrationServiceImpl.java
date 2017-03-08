package com.ntuaece.nikosapos.register;

import java.io.IOException;

import com.google.gson.Gson;
import com.ntuaece.nikosapos.entities.Node;
import com.ntuaece.nikosapos.registerpacket.RegisterPacket;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegistrationServiceImpl implements RegistrationService {

	private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	private static final String DISCOVERY_ENDPOINT = "http://localhost:8080/register/";

	private final Node node;

	public RegistrationServiceImpl(Node node) {
		this.node = node;
	}

	@Override
	public boolean registerToICAS() {
		boolean result = true;

		Request request = new Request.Builder()
				.post(RequestBody.create(JSON, new Gson().toJson(RegisterPacket.FromNode(node))))
				.url(DISCOVERY_ENDPOINT)
				.build();
		
		try {
			Response response = new OkHttpClient().newCall(request).execute();
			if (!response.isSuccessful()){
				result = false;
			} 
		} catch (IOException e) {
			result = false;
			e.printStackTrace();
		}
		return result;
	}

}
