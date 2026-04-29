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
            // create and start the GuiAgent (receives updates from Seller)
            AgentController guiAgent = mainContainer.createNewAgent("GuiAgent", "GuiAgent", new Object[] {});
            guiAgent.start();

            // Launch Swing UI on EDT and link it to GuiAgent
            javax.swing.SwingUtilities.invokeLater(() -> {
                AuctionGUI gui = new AuctionGUI(mainContainer);
                GuiAgent.setViewer(gui);
                gui.setVisible(true);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}