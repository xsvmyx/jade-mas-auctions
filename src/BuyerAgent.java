import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPAException;

public class BuyerAgent extends Agent {
    private int budgetMax;
    private double chanceDeBid = 0.7;

    

    protected void setup() {
        // 1. On récupère les arguments passés par le MainLauncher
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            // On récupère le budget passé au moment du createNewAgent
            this.budgetMax = (int) args[0];
        } else {
            // Valeur de secours au cas où l'argument est oublié
            this.budgetMax = (int)(Math.random() * 6000) + 13000;
        }

        System.out.println("[" + getLocalName() + "] Prêt. Budget Max: " + budgetMax + " DZD");

        // 2. Inscription au DF (Annuaire)
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("auction-bidder");
        sd.setName("bidder-service");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) { fe.printStackTrace(); }

        // 3. Comportement d'écoute
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    if (msg.getPerformative() == ACLMessage.CFP) {
                        int prixPropose = Integer.parseInt(msg.getContent());
                        
                        // Condition de Budget + Chance de bid (Random)
                        if (prixPropose <= budgetMax) {
                            if (Math.random() < chanceDeBid) {
                                System.out.println("[" + getLocalName() + "] J'accepte l'offre de " + prixPropose);
                                ACLMessage reply = msg.createReply();
                                reply.setPerformative(ACLMessage.PROPOSE);
                                reply.setContent(String.valueOf(prixPropose));
                                send(reply);
                            } else {
                                System.out.println("[" + getLocalName() + "] J'attends un meilleur prix...");
                            }
                        } else {
                            System.out.println("[" + getLocalName() + "] " + prixPropose + " est au dessus de mon budget.");
                        }
                    }
                } else {
                    block();
                }
            }
        });
    }

    protected void takeDown() {
        try { DFService.deregister(this); } catch (FIPAException fe) {}
        System.out.println("Agent " + getLocalName() + " supprimé.");
    }
}