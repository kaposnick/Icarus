package tasks;

import java.util.TimerTask;

import com.ntuaece.nikosapos.node.Node;

import services.IcasResponsible;

public class UpdateBehaviorTask extends TimerTask {
    
    private final Node node;
    private final IcasResponsible service;
    
    public UpdateBehaviorTask(final Node node, final IcasResponsible service) {
        this.node = node;
        this.service = service;
    }

	@Override
	public void run() {
		System.out.println("Node " + node.getId() + " updating neighbor stats");
		service.updateNeighborBehavior();
	}

}
