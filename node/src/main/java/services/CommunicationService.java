package services;

import com.google.gson.Gson;
import com.ntuaece.nikosapos.node.Node;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class CommunicationService {
    protected static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    protected final Node node;
    protected final OkHttpClient httpClient;
    protected final Gson gson;

    public CommunicationService(final Node node, final OkHttpClient client, final Gson gson) {
        this.node = node;
        this.httpClient = client;
        this.gson = gson;
    }
    
    protected void assertValidResources() {
        if (httpClient == null) throw new RuntimeException("You should call setHttpClient first.");
        if (gson == null) throw new RuntimeException("You should call setGson first.");
    }
}
