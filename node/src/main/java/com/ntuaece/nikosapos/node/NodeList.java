package com.ntuaece.nikosapos.node;

import java.util.ArrayList;

import com.ntuaece.nikosapos.entities.Node;

public class NodeList extends ArrayList<Node> {
	private static NodeList instance;
	
	private NodeList(){}
	
	public static NodeList GetInstance(){
		if (instance == null){
			synchronized (NodeList.class) {
				if (instance == null){
					instance = new NodeList();
				}
			}
		}
		return instance;
	}
}
