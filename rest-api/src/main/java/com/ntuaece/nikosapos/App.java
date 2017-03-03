package com.ntuaece.nikosapos;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.gson.Gson;

@SpringBootApplication
public class App 
{
	
    public static void main( String[] args )
    { 
        SpringApplication.run(App.class, args);
        Gson gson = new Gson();
    }
}
