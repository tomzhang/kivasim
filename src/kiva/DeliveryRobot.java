package src.kiva;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

/**
 * The deliverer responds to product requests and delivers shelf to the picker
 * **/

public class DeliveryRobot extends Agent {

	boolean answer;

	ACLMessage inform;

	protected void setup() {

		// Registered behaviour in yellow pages
		 DFAgentDescription dfd = new DFAgentDescription();
		 dfd.setName(getAID());
		 ServiceDescription sd = new ServiceDescription();
		 sd.setType("shelfPicking");
		 sd.setName("Delivery robot");
		 dfd.addServices(sd);
		 try {
		 DFService.register(this, dfd);
		 }
		 catch (FIPAException fe) {
		 fe.printStackTrace();
		 }
		 
		// Add cyclic behaviour for informing_free behaviour
		addBehaviour(new CyclicBehaviour(this) {
			public void action() {

				inform = myAgent.receive(MessageTemplate
						.MatchPerformative(ACLMessage.INFORM));

				/*
				 * The delivery robots respond by informing the picker that they
				 * are ready/free to carry the shelves.
				 */
				if (!answer && inform != null) {
					ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
					msg.setContent("READY");
					msg.addReceiver(inform.getSender());
					send(msg);
					System.out.println("Informed by picker to be: " + inform);
					System.out.println("Informed the picker that I am: "
							+ msg.getContent());
					System.out.println("working..");
					answer = true;
				}
			};
		});
	}

	protected void takeDown() {
		System.out.println("Delivery Robots " + getAID().getName()
				+ " terminating");
		doDelete();
	}
}
