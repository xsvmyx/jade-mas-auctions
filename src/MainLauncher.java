import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class MainLauncher {
    public static void main(String[] args) {
        // 1. Récupérer l'instance du runtime JADE
        Runtime rt = Runtime.instance();

        // 2. Créer un profil par défaut (Main Container)
        Profile p = new ProfileImpl();
        p.setParameter(Profile.GUI, "true"); // Affiche l'interface RMA pour voir tes agents

        // 3. Créer le Main Container
        AgentContainer mainContainer = rt.createMainContainer(p);

        try {
            // 4. Lancer le Seller
            AgentController seller = mainContainer.createNewAgent("Seller", "SellerAgent", null);
            seller.start();

            // 5. Lancer les 2 Buyers
            AgentController b1 = mainContainer.createNewAgent("Buyer1", "BuyerAgent", null);
            AgentController b2 = mainContainer.createNewAgent("Buyer2", "BuyerAgent", null);
            b1.start();
            b2.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}