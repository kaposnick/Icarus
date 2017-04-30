package node;

import java.util.ArrayList;
import java.util.Optional;

public class NodeList extends ArrayList<Node> {
    private static final long serialVersionUID = 1L;
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
	
	public static Optional<Node> GetNodeById(String id){
		return GetNodeById(Long.parseLong(id));
	}
	
	public static Optional<Node> GetNodeById(long id){
		return instance
			.stream()
			.filter(n -> n.getId() == id)
			.findFirst();
	}
}
