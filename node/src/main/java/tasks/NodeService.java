package tasks;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;

public interface NodeService {
    void setHttpClient(OkHttpClient httpClient);
    void setGson(Gson gson);
}
