import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class SellerAgent extends Agent {
    private int currentPrice = 20000;
    private int minPrice = 12000;
    private AID lastBidder = null;
    private int lastBidPrice = 0;
    private int attemps = 0;
    private int increment = 500; 
    private int decrement = 300;
    private int maxAttempts = 3;

    protected void setup() {
        // 1. Récupération des paramètres de l'enchère
        Object[] args = getArguments();
        if (args != null && args.length >= 5) {
            this.currentPrice = (int) args[0];
            this.minPrice = (int) args[1];
            this.increment = (int) args[2];
            this.decrement = (int) args[3];
            this.maxAttempts = (int) args[4];
            
            
           
        }

        System.out.println("Vendeur " + getLocalName() + " prêt. Prix de départ : " + currentPrice + " (Min: " + minPrice + ")");

        // On commence par une première diffusion
        diffuserOffre(currentPrice);

        addBehaviour(new TickerBehaviour(this, 5000) { 
            protected void onTick() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
                ACLMessage bid = receive(mt);
                
                boolean aRecuOffre = false;

                // On prend la première offre valide
                while (bid != null) {
                    int offeredPrice = Integer.parseInt(bid.getContent());
                    if (offeredPrice >= currentPrice) {
                        aRecuOffre = true;
                        lastBidder = bid.getSender();
                        lastBidPrice = offeredPrice;
                        currentPrice = lastBidPrice + increment; // On monte le prix
                        attemps = 0; // On a eu une offre, on reset le compteur !
                        System.out.println(">>> Nouveau meilleur offrant : " + lastBidder.getLocalName() + " (" + lastBidPrice + " DZD)");
                        break; 
                    }
                    bid = receive(mt);
                }

                if (aRecuOffre) {
                    // On relance un round avec le prix augmenté
                    diffuserOffre(currentPrice);
                } else {
                    // AUCUNE OFFRE ce tour-ci
                    attemps++;
                    System.out.println("Aucune offre (Tentative " + attemps + "/" + maxAttempts + ")");

                    if (attemps >= maxAttempts) {
                        conclureVente();
                        stop();
                    } else {
                        // On baisse un peu le prix pour tenter de relancer
                        if (currentPrice > minPrice) {
                            currentPrice -= decrement; 
                            if (currentPrice < minPrice) currentPrice = minPrice;
                            System.out.println("Tentative de relance au prix de : " + currentPrice);
                            diffuserOffre(currentPrice);
                        } else {
                            // Si on est déjà au prix min et que ça ne répond pas
                            conclureVente();
                            stop();
                        }
                    }
                }
            }
        });
    }

    private void diffuserOffre(int prix) {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("auction-bidder"); 
        template.addServices(sd);

        try {
            DFAgentDescription[] result = DFService.search(this, template);
            if (result.length > 0) {
                ACLMessage msg = new ACLMessage(ACLMessage.CFP);
                msg.setContent(String.valueOf(prix));
                for (DFAgentDescription agent : result) {
                    msg.addReceiver(agent.getName());
                }
                send(msg);
                System.out.println("[DF] Appel d'offre envoyé à " + result.length + " acheteurs pour " + prix + " DZD");
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private void conclureVente() {
        System.out.println("\n==============================================");
        if (lastBidder != null) {
            System.out.println("ADJUGÉ ! Vendu à : " + lastBidder.getLocalName());
            System.out.println("Prix final : " + lastBidPrice + " DZD");
        } else {
            System.out.println("FIN DE L'ENCHÈRE : Aucun acheteur trouvé.");
        }
        System.out.println("==============================================\n");
        doDelete();
    }

    protected void takeDown() {
        // Pas de deregister ici car le Seller n'était pas inscrit (il cherchait juste)
        System.out.println("Agent Vendeur " + getLocalName() + " quitte la plateforme.");
    }
}