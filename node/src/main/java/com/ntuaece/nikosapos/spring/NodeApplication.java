package com.ntuaece.nikosapos.spring;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;

import com.ntuaece.nikosapos.entities.Node;
import com.ntuaece.nikosapos.node.NodeList;
import com.ntuaece.nikosapos.node.NodePosition;
import com.ntuaece.nikosapos.node.NodeThread;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { JacksonAutoConfiguration.class })
public class NodeApplication {

	private static void initializeNodes() {
		for (int i = 0; i < NodePosition.pos_x.length; i++) {
			Node node = new Node.Builder().setId(i).setX(NodePosition.pos_x[i]).setY(NodePosition.pos_y[i]).build();
			NodeList.GetInstance().add(node);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(NodeApplication.class, args);

		initializeNodes();
		
		for(int i = 0; i<= 50; i++){
			new NodeThread(NodeList.GetInstance().get(i)).start();			
		}
	}

}
