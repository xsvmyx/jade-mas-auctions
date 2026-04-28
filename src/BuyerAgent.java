import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class BuyerAgent extends Agent {
    protected void setup() {
        // --- Enregistrement au DF ---
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("auction-buyer");
        sd.setName("JADE-auction");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            System.out.println(getLocalName() + " inscrit au DF.");
        } catch (FIPAException fe) { fe.printStackTrace(); }

        // --- Comportement de réception ---
        addBehaviour(new jade.core.behaviours.CyclicBehaviour() {
            public void action() {
                // On essaie de récupérer un message
                ACLMessage msg = receive();
                if (msg != null) {
                    // Si on a reçu un message, on affiche son contenu
                    String prix = msg.getContent();
                    System.out.println(">>> [" + getLocalName() + "] Message reçu ! Prix proposé : " + prix + " DZD");
                } else {
                    // Sinon, on met l'agent en pause pour économiser le CPU
                    block();
                }
            }
        });
    }

    protected void takeDown() {
        try { DFService.deregister(this); } catch (FIPAException fe) {}
        System.out.println("Acheteur " + getLocalName() + " quitte la plateforme.");
    }
}