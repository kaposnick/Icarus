package com.ntuaece.nikosapos.tasks;

import com.google.gson.Gson;
import com.ntuaece.nikosapos.darwin.DarwinUpdateService;
import com.ntuaece.nikosapos.darwin.DarwinUpdateServiceImpl;
import com.ntuaece.nikosapos.node.Node;

import okhttp3.OkHttpClient;

public class DarwinUpdateTask extends NodeTask implements Runnable {

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
