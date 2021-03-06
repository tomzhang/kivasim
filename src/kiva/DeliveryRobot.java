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

	boolean isAvailable;
	ACLMessage request;
	ACLMessage inform;
	ACLMessage confirm;

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
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		isAvailable = true;

		// Add cyclic behaviour for informing_free behaviour
		addBehaviour(new CyclicBehaviour(this) {
			public void action() {

				request = myAgent.receive(MessageTemplate
						.MatchPerformative(ACLMessage.REQUEST));
				inform = myAgent.receive(MessageTemplate
						.MatchPerformative(ACLMessage.INFORM));
				confirm = myAgent.receive(MessageTemplate
						.MatchPerformative(ACLMessage.CONFIRM));
				
				if(confirm != null){
					System.out.println("Delivery Agent returns");
					isAvailable = true;
				}

				/*
				 * The delivery robot confirms to the picker's request for
				 * availability.
				 */
				if (request != null) {
					if (isAvailable == true) {
						ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM);
						msg.setContent("Available");
						msg.addReceiver(request.getSender());
						send(msg);
						System.out.println("Delivery Robot: Confirmed "
								+ msg.getContent() + " to picker's request");
						isAvailable = false;
					}
					// No message if not available
				}
				if (inform != null) {
					System.out
							.println("Delivery Robot: Bringing the requested Shelf"
									+ inform.getContent() + " to the Picker.");

					// simulate delivery time
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
					}
					//Inform the picker that he can pick from the shelf
					ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
					msg.setContent("PICK " + inform.getContent());
					msg.addReceiver(inform.getSender());
					send(msg);
					System.out.println("Delivery Robot: "
							+ inform.getSender().getName() + " can now pick " + inform.getContent());
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
