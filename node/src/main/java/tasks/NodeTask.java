package tasks;

import com.ntuaece.nikosapos.node.Node;

public abstract class NodeTask implements Runnable {
	protected final Node node;
	
	public NodeTask(Node node){
		this.node = node;
	}
}
