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
            Object[] sellerArgs = new Object[] { 20000, 20000 }; // PrixDépart, PrixMin

            AgentController seller = mainContainer.createNewAgent("Seller", "SellerAgent", sellerArgs);
            seller.start();



            int budgetB1 = 30000; 
            Object[] argsB1 = new Object[]{ budgetB1 };
            int budgetB2 = 26000; 
            Object[] argsB2 = new Object[]{ budgetB2 }; 
            // 5. Lancer les 2 Buyers
            AgentController b1 = mainContainer.createNewAgent("Buyer1", "BuyerAgent", argsB1);
            AgentController b2 = mainContainer.createNewAgent("Buyer2", "BuyerAgent", argsB2);
            b1.start();
            b2.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}