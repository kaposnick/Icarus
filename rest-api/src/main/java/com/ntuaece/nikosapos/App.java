package com.ntuaece.nikosapos;
import java.util.Timer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;

import com.ntuaece.nikosapos.entities.DistantNodeAssist;
import com.ntuaece.nikosapos.entities.SelfishNodesPublish;


@SpringBootApplication
@EnableAutoConfiguration(exclude = { JacksonAutoConfiguration.class })
public class App 
{
	
    public static void main( String[] args )
    { 
        SpringApplication.run(App.class, args);
        
        Timer icasTimer = new Timer("Icas Timer");
        icasTimer.scheduleAtFixedRate(new SelfishNodesPublish(), 15000, 15000);
        icasTimer.scheduleAtFixedRate(new DistantNodeAssist(), 20000, 20000);
    }
}
