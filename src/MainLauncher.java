import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class MainLauncher {
    public static void main(String[] args) {
        
        Runtime rt = Runtime.instance();

        
        Profile p = new ProfileImpl();
        p.setParameter(Profile.GUI, "true"); 

    
        AgentContainer mainContainer = rt.createMainContainer(p);

        try {
            Object[] sellerArgs = new Object[] { 20000,  12000,    // Start Price, Min Price
                                                 750,      // Increment 
                                                 0  , // decrement
                                                2 };    // Max Attempts 

            AgentController seller = mainContainer.createNewAgent("Seller", "SellerAgent", sellerArgs);
            seller.start();



            int budgetB1 = 30000; 
            Object[] argsB1 = new Object[]{ budgetB1, 0.7 }; // Budget, Bid Chance
            int budgetB2 = 26000; 
            Object[] argsB2 = new Object[]{ budgetB2, 0.7 }; 
            
            AgentController b1 = mainContainer.createNewAgent("Buyer1", "BuyerAgent", argsB1);
            AgentController b2 = mainContainer.createNewAgent("Buyer2", "BuyerAgent", argsB2);
            b1.start();
            b2.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}