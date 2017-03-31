package com.ntuaece.nikosapos.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;

import com.ntuaece.nikosapos.node.Node;
import com.ntuaece.nikosapos.node.NodeList;
import com.ntuaece.nikosapos.node.NodePosition;
import com.ntuaece.nikosapos.node.NodeThread;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { JacksonAutoConfiguration.class })
public class NodeApplication {
    public static int ThreadsFinishedDiscovering = 0;

    private static void initializeNodes() {
        for (int i = 0; i < NodePosition.x.length; i++) {
            Node node = new Node.Builder()
                    .setId(i)
                    .setX(NodePosition.x[i])
                    .setY(NodePosition.y[i])
                    .setDestinationIds(NodePosition.destinationNodes[i%13])
                    .build();
            if (i == 12 || i == 19 || i == 20) node.setCheater(true);
            
            NodeList.GetInstance().add(node);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(NodeApplication.class, args);

        initializeNodes();

        for (int i = 0; i < NodePosition.x.length; i++) {
            new NodeThread(NodeList.GetInstance().get(i)).start();
        }
    }

}
