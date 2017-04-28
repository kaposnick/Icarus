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
import node.Link;
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
            if (i == 12 || i == 19 || i == 20 || i == 10) node.setCheater(true);
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
                    System.out.println(node + " darwinPackets: " + node.getDarwinPacketList().values().size());
                    node.getDarwinPacketList().forEach((id, packet) -> {
                        System.out.println("[" + id + "] Round: " + packet.getRound());
                    });
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
