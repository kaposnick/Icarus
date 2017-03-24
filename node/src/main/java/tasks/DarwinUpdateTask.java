package tasks;

import com.google.gson.Gson;
import com.ntuaece.nikosapos.node.Node;

import darwin.DarwinUpdateService;
import darwin.DarwinUpdateServiceImpl;
import okhttp3.OkHttpClient;

public class DarwinUpdateTask extends NetworkTask {

    private final DarwinUpdateService darwinUpdateService;

    public DarwinUpdateTask(Node node) {
        super(node);
        darwinUpdateService = new DarwinUpdateServiceImpl(node);
    }

    public void setHttpClient(OkHttpClient client) {
        if (darwinUpdateService instanceof DarwinUpdateServiceImpl) {
            ((DarwinUpdateServiceImpl) darwinUpdateService).setHttpClient(client);
        }
    }

    public void setGson(Gson gson) {
        if (darwinUpdateService instanceof DarwinUpdateServiceImpl) {
            ((DarwinUpdateServiceImpl) darwinUpdateService).setGson(gson);
        }
    }

    @Override
    public void run() {
        darwinUpdateService.updateNeighbors();
    }
}
