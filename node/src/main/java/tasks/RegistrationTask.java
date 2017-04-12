package tasks;

import node.Node;
import services.IcasResponsible;

public class RegistrationTask implements Runnable {

	private final Node node;
	private final IcasResponsible icasService;

	public RegistrationTask(Node node, IcasResponsible kati) {
		this.node = node;
		this.icasService = kati;
	}

	@Override
	public void run() {
		System.out.println("Node: " + node.getId() + " registrating to ICAS...");
		icasService.registerToIcas();
	}

}
