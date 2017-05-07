package com.ntuaece.nikosapos.spring;

import java.util.Optional;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;

import darwin.DarwinAlternativeCalculator;
import node.Node;
import node.NodeList;
import node.NodePosition;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { JacksonAutoConfiguration.class })
public class NodeApplication {
    private static void initializeNodes() {
        for (int nodeId = 0; nodeId < NodePosition.x.length; nodeId++) {
            boolean isDistant = false;
            boolean isCheater = false;

            for (int index = 0; index < NodePosition.selfishNodes.length; index++) {
                if (NodePosition.selfishNodes[index] == nodeId) {
                    isCheater = true;
                    break;
                }
            }

            for (int index = 0; index < NodePosition.distantNodes.length; index++) {
                if (NodePosition.distantNodes[index] == nodeId) {
                    isDistant = true;
                    break;
                }
            }

            Node node = new Node.Builder().setId(nodeId)
                                          .setX(NodePosition.x[nodeId])
                                          .setY(NodePosition.y[nodeId])
                                          .setDestinationIds(NodePosition.destinationNodes[nodeId % 13])
                                          .setDistant(isDistant)
                                          .build();
            node.setCheater(isCheater);
            node.setDarwinImpl(new DarwinAlternativeCalculator(node));
            NodeList.GetInstance().add(node);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(NodeApplication.class, args);

        initializeNodes();

        for (int i = 0; i < NodePosition.x.length; i++) {
            NodeList.GetInstance().get(i).start();
        }

        Timer timer = new Timer("Kill Timer");
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                System.exit(0);
            }
        }, 400000);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                int nodeId = scanner.nextInt();
                Optional<Node> mNode = NodeList.GetNodeById(nodeId);
                if (mNode.isPresent()) {
                    Node node = mNode.get();
                    System.out.println(node + " Selfish Nodes: " + node.getSelfishNodes());
                    node.getNeighbors().forEach(neigbor -> {

                        String outputString = String.format("Neighbor [%d]\tDarwinΙ: %.2f\tPMinusI: %.2f\tDarwinMinusI: %.2f\t Ratio: %.2f\t Forwarded :%d\t Sent :%d",
                                                            neigbor.getId(),
                                                            neigbor.getDarwinI(),
                                                            neigbor.getPMinusI(),
                                                            neigbor.getDarwinMinusI(),
                                                            neigbor.getConnectivityRatio(),
                                                            neigbor.getPacketsForwarded(),
                                                            neigbor.getPacketsSent());
                        System.out.println(outputString);
                    });
                }
            } catch (Exception ie) {
                ie.printStackTrace();
            }
        }
    }

}
