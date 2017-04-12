package com.ntuaece.nikosapos.spring;

import java.util.Optional;
import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;

import darwin.DarwinCalculator;
import node.Node;
import node.NodeList;
import node.NodePosition;
import node.NodeThread;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { JacksonAutoConfiguration.class })
public class NodeApplication {
    private static void initializeNodes() {
        for (int i = 0; i < NodePosition.x.length; i++) {
            Node node = new Node.Builder().setId(i)
                                          .setX(NodePosition.x[i])
                                          .setY(NodePosition.y[i])
                                          .setDestinationIds(NodePosition.destinationNodes[i % 13])
                                          .build();
            if (i == 12 || i == 19 || i == 20) node.setCheater(true);
            node.setDarwinImpl(new DarwinCalculator(node));
            NodeList.GetInstance().add(node);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(NodeApplication.class, args);

        initializeNodes();

        for (int i = 0; i < NodePosition.x.length; i++) {
            new NodeThread(NodeList.GetInstance().get(i)).start();
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                int nodeId = scanner.nextInt();
                Optional<Node> mNode = NodeList.GetNodeById(nodeId);
                if (mNode.isPresent()) {
                    Node node = mNode.get();
                    node.getNeighbors().forEach(neigbor -> {
                        String outputString = String.format("Neighbor [%d]\tDarwin: %.2f\tP: %.2f\tDarwin4Me: %.2f\tP4Me: %.2f\t Ratio: %.2f",
                                                            neigbor.getId(),
                                                            neigbor.getNeighborDarwin(),
                                                            neigbor.getP(),
                                                            neigbor.getDarwinForMe(),
                                                            neigbor.getPForMe(),
                                                            neigbor.getConnectivityRatio());
                        System.out.println(outputString);
                    });
                }
            } catch (Exception ie) {
                ie.printStackTrace();
            }
        }
    }

}
