package darwin;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ntuaece.nikosapos.node.Node;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class DarwinUpdateServiceImpl implements DarwinUpdateService {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String DARWIN_ENDPOINT = "http://localhost:8081/darwin/";

    private final Node node;
    private final DarwinPacket packet;
    private OkHttpClient httpClient;
    private Gson gson;
    private Request.Builder builder;

    public DarwinUpdateServiceImpl(Node node) {
        this.node = node;
        this.packet = new DarwinPacket(node);
    }

    public void setHttpClient(OkHttpClient client) {
        this.httpClient = client;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void updateNeighbors() {
        validateVariables();
        node.getNeighbors().stream().forEach(neighbor -> {
            Request request = builder.url(DARWIN_ENDPOINT + neighbor.getId()).build();
            try {
                httpClient.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void validateVariables() {
        if (httpClient == null) httpClient = new OkHttpClient();
        if (gson == null) gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        if (builder == null) builder = new Request.Builder().post(RequestBody.create(JSON, gson.toJson(packet)));

    }

}
