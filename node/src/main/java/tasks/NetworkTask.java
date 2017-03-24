package tasks;

import com.google.gson.Gson;
import com.ntuaece.nikosapos.node.Node;

import okhttp3.OkHttpClient;

public abstract class NetworkTask extends NodeTask {

    public NetworkTask(Node node) {
        super(node);
    }
    
    public abstract void setHttpClient(OkHttpClient client);
    
    public abstract void setGson(Gson gson);

}
