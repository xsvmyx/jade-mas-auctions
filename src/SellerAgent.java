import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class SellerAgent extends Agent {
    protected void setup() {
        System.out.println("Vendeur " + getLocalName() + " prêt.");

        // On attend 2 secondes que tout le monde soit inscrit avant de chercher
        addBehaviour(new jade.core.behaviours.WakerBehaviour(this, 2000) {
            protected void handleElapsedTimeout() {
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("auction-buyer");
                template.addServices(sd);

                try {

                    //myAgent contains the reference to the agent that owns this behaviour, i.e. SellerAgent , its a protected variable of the Behaviour class
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    if (result.length > 0) {
                        // Création du message ACL
                        ACLMessage msg = new ACLMessage(ACLMessage.CFP); // Call For Proposal
                        msg.setContent("20000"); // Prix starter
                        
                        System.out.println("Vendeur : Envoi du prix de départ (20000 DZD) à " + result.length + " acheteurs.");
                        
                        for (int i = 0; i < result.length; i++) {
                            msg.addReceiver(result[i].getName());
                        }
                        send(msg);
                    } else {
                        System.out.println("Vendeur : Aucun acheteur trouvé.");
                    }
                } catch (FIPAException fe) { fe.printStackTrace(); }
            }
        });
    }

    protected void takeDown() {
        // On se contente d'un log propre
        System.out.println("----------------------------------------------");
        System.out.println("Vendeur " + getLocalName() + " termine sa session.");
        System.out.println("Statut final : Enchère terminée ou interrompue.");
        System.out.println("----------------------------------------------");
    }

     
}