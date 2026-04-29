import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.CyclicBehaviour;
import javax.swing.SwingUtilities;

/**
 * GuiAgent — Listener agent that receives status updates from SellerAgent
 * and forwards them to the Swing GUI safely on the Event Dispatch Thread.
 *
 * Message protocol:
 *   PRICE:<value>                 → current asking price changed
 *   BEST:<buyerName>:<amount>     → new highest bidder
 *   ROUND:<roundNumber>           → new round started
 *   ATTEMPT:<current>/<max>       → no-bid attempt counter
 *   FINAL:<winnerName> (<amount>) → auction concluded
 *   FINAL:aucun                   → auction concluded with no winner
 *   LOG:<text>                    → general log line
 */
public class GuiAgent extends Agent {
    private static AuctionGUI ui;

    public static void setViewer(AuctionGUI gui) {
        ui = gui;
    }

    @Override
    protected void setup() {
        System.out.println("[GuiAgent] Agent GUI démarré.");

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    final String content = msg.getContent();
                    SwingUtilities.invokeLater(() -> {
                        if (ui == null) return;
                        if (content.startsWith("PRICE:")) {
                            ui.setCurrentPrice(content.substring(6));
                        } else if (content.startsWith("BEST:")) {
                            ui.setCurrentWinner(content.substring(5));
                        } else if (content.startsWith("ROUND:")) {
                            ui.setRound(content.substring(6));
                        } else if (content.startsWith("ATTEMPT:")) {
                            ui.setAttempt(content.substring(8));
                        } else if (content.startsWith("FINAL:")) {
                            ui.showFinalWinner(content.substring(6));
                        } else if (content.startsWith("LOG:")) {
                            ui.appendLog(content.substring(4));
                        }
                    });
                } else {
                    block();
                }
            }
        });
    }

    @Override
    protected void takeDown() {
        System.out.println("[GuiAgent] Agent GUI arrêté.");
    }
}
