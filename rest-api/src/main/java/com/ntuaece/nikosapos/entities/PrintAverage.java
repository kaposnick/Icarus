package com.ntuaece.nikosapos.entities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TimerTask;

public class PrintAverage extends TimerTask {

    File cheatingFile;
    File cooperativeFile;

    public PrintAverage() {
        cheatingFile = new File("/home/nickapostol/Desktop/darwin/tokenCheat.txt");
        cooperativeFile = new File("/home/nickapostol/Desktop/darwin/tokenCoop.txt");

        if (cheatingFile.exists()) {
            cheatingFile.delete();
        }

        if (cooperativeFile.exists()) {
            cooperativeFile.delete();
        }

        try {
            cooperativeFile.createNewFile();
            cheatingFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        synchronized (NodeEntity.NodeEntityList) {
            int totalCoop = 0;
            int totalCheat = 0;

            int tokenCoop = 0;
            int tokenCheat = 0;

            for (NodeEntity entity : NodeEntity.NodeEntityList) {
                if (entity.isSelfish()) {
                    totalCheat++;
                    tokenCheat += entity.getTokens();
                } else {
                    totalCoop++;
                    tokenCoop += entity.getTokens();
                }
            }

            if (totalCoop + totalCheat >= 30) {
                float coopAverage = (totalCoop > 0) ? tokenCoop / totalCoop : 0;
                float cheatAverage = (totalCheat > 0) ? tokenCheat / totalCheat : 0;

                FileWriter coopWriter = null;
                FileWriter cheatWriter = null;

                try {
                    coopWriter = new FileWriter(cooperativeFile, true);
                    coopWriter.write(String.valueOf(coopAverage) + "\n");
                    coopWriter.close();
                    cheatWriter = new FileWriter(cheatingFile, true);
                    cheatWriter.write(String.valueOf(cheatAverage) + "\n");
                    cheatWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
