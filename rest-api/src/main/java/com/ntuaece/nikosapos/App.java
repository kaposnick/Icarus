package com.ntuaece.nikosapos;

import java.util.Timer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;

import com.ntuaece.nikosapos.entities.DistantNodeAssist;
import com.ntuaece.nikosapos.entities.SelfishNodesPublish;
import com.ntuaece.nikosapos.entities.UpdateNodeStatus;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { JacksonAutoConfiguration.class })
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);

        Timer icasTimer = new Timer("Icas Timer");
        icasTimer.scheduleAtFixedRate(new SelfishNodesPublish(),
                                      IcasConstants.SELFISH_UPDATE_INITIAL_DELAY,
                                      IcasConstants.SELFISH_UPDATE_PERIOD);
        icasTimer.scheduleAtFixedRate(new UpdateNodeStatus(),
                                      IcasConstants.NODE_UPDATE_INITIAL_DELAY,
                                      IcasConstants.NODE_UPDATE_PERIOD);
        icasTimer.scheduleAtFixedRate(new DistantNodeAssist(),
                                      IcasConstants.DISTANT_ASSIST_INITIAL_DELAY,
                                      IcasConstants.DISTANT_ASSIST_PERIOD);
    }
}
