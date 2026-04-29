import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;


/**
 * AuctionGUI — A modern, dark-themed Swing interface for the MAS auction system.
 * Allows dynamic configuration of Seller params and Buyer agents, real-time
 * monitoring of auction progress, and displays the final winner.
 */
public class AuctionGUI extends JFrame {


    // ── Color Palette ──────────────────────────────────────────────
    private static final Color BG_DARK       = new Color(18, 18, 24);
    private static final Color BG_CARD       = new Color(28, 28, 40);
    private static final Color BG_CARD_HOVER = new Color(35, 35, 50);
    private static final Color BG_INPUT      = new Color(38, 38, 55);
    private static final Color BORDER_COLOR  = new Color(55, 55, 80);
    private static final Color ACCENT        = new Color(99, 102, 241);   // Indigo
    private static final Color ACCENT_HOVER  = new Color(129, 132, 255);
    private static final Color ACCENT_GREEN  = new Color(34, 197, 94);
    private static final Color ACCENT_RED    = new Color(239, 68, 68);
    private static final Color ACCENT_AMBER  = new Color(245, 158, 11);
    private static final Color TEXT_PRIMARY   = new Color(240, 240, 250);
    private static final Color TEXT_SECONDARY = new Color(160, 160, 185);
    private static final Color TEXT_MUTED     = new Color(100, 100, 130);


    private static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_MONO    = new Font("JetBrains Mono", Font.PLAIN, 12);
    private static final Font FONT_BIG_NUM = new Font("Segoe UI", Font.BOLD, 32);


    // ── JADE reference ─────────────────────────────────────────────
    private final AgentContainer container;


    // ── Seller config fields ───────────────────────────────────────
    private JTextField sellerStartField   = styledField("20000", 10);
    private JTextField sellerMinField     = styledField("12000", 10);
    private JTextField sellerIncField     = styledField("750", 8);
    private JTextField sellerDecField     = styledField("0", 8);
    private JTextField sellerMaxAttField  = styledField("2", 5);


    // ── Buyer config fields ────────────────────────────────────────
    private JTextField buyerNameField     = styledField("", 10);
    private JTextField buyerBudgetField   = styledField("30000", 10);
    private JTextField buyerChanceField   = styledField("0.7", 8);
    private DefaultListModel<String> buyersModel = new DefaultListModel<>();
    private JList<String> buyersList      = new JList<>(buyersModel);
    private final ArrayList<Object[]> buyersParams = new ArrayList<>();
    private int buyerCounter = 0;


    // ── Live status labels ─────────────────────────────────────────
    private JLabel priceValueLabel  = new JLabel("—");
    private JLabel winnerValueLabel = new JLabel("—");
    private JLabel roundValueLabel  = new JLabel("—");
    private JLabel attemptValueLabel = new JLabel("—");


    // ── Log area ───────────────────────────────────────────────────
    private JTextArea logArea = new JTextArea();
    private JButton startBtn;
    private boolean auctionStarted = false;


    // ── Constructor ────────────────────────────────────────────────
    public AuctionGUI(AgentContainer container) {
        super("MAS Auctions");
        this.container = container;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(BG_DARK);
        getContentPane().setBackground(BG_DARK);
        buildUI();
        setMinimumSize(new Dimension(920, 680));
        setSize(960, 720);
        setLocationRelativeTo(null);
    }


    // ════════════════════════════════════════════════════════════════
    //  UI CONSTRUCTION
    // ════════════════════════════════════════════════════════════════


    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_DARK);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        // ── Header ─────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("⚡ MAS Auctions");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRIMARY);
        JLabel subtitle = new JLabel("Configure agents, launch auction, watch in real-time");
        subtitle.setFont(FONT_SMALL);
        subtitle.setForeground(TEXT_MUTED);
        header.add(title, BorderLayout.WEST);
        header.add(subtitle, BorderLayout.EAST);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));


        // ── Top config row: Seller + Buyers ────────────────────────
        JPanel configRow = new JPanel(new GridLayout(1, 2, 14, 0));
        configRow.setOpaque(false);
        configRow.add(buildSellerCard());
        configRow.add(buildBuyerCard());


        // ── Bottom row: Status + Log ───────────────────────────────
        JPanel bottomRow = new JPanel(new GridLayout(1, 2, 14, 0));
        bottomRow.setOpaque(false);
        bottomRow.add(buildStatusCard());
        bottomRow.add(buildLogCard());


        // ── Start Button ───────────────────────────────────────────
        startBtn = createAccentButton("▶  Lancer l'Enchère");
        startBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        startBtn.setPreferredSize(new Dimension(260, 48));
        startBtn.setMinimumSize(new Dimension(220, 48));
        startBtn.setMaximumSize(new Dimension(280, 48));
        startBtn.addActionListener(e -> onStartAuction());

        JPanel startPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        startPanel.setOpaque(false);
        startPanel.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));
        startPanel.add(startBtn);
        
        // ── Assembly ───────────────────────────────────────────────
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        configRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        startPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        center.add(configRow);
        center.add(Box.createVerticalStrut(14));
        center.add(startPanel);
        center.add(Box.createVerticalStrut(14));
        center.add(bottomRow);

        // Make bottom row take more space
        bottomRow.setPreferredSize(new Dimension(0, 260));


        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        setContentPane(root);
    }


    // ── Seller Card ────────────────────────────────────────────────
    private JPanel buildSellerCard() {
        JPanel card = createCard("🏷️  Vendeur (Seller)");


        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;


        int row = 0;
        addFormRow(form, gbc, row++, "Prix de départ", sellerStartField, "DZD");
        addFormRow(form, gbc, row++, "Prix minimum", sellerMinField, "DZD");
        addFormRow(form, gbc, row++, "Incrément", sellerIncField, "DZD");
        addFormRow(form, gbc, row++, "Décrément", sellerDecField, "DZD");
        addFormRow(form, gbc, row++, "Tentatives max", sellerMaxAttField, "");


        card.add(form, BorderLayout.CENTER);
        return card;
    }


    // ── Buyer Card ─────────────────────────────────────────────────

    private JPanel buildBuyerCard() {
        JPanel card = createCard("🛒  Acheteurs (Buyers)");

        JPanel addRow = new JPanel(new GridBagLayout());
        addRow.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JButton addBtn = createSmallButton("+ Ajouter");
        addBtn.addActionListener(e -> onAddBuyer());

        // ligne 1
        gbc.gridy = 0;

        gbc.gridx = 0;
        gbc.weightx = 0;
        addRow.add(styledLabel("Nom:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        addRow.add(buyerNameField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        addRow.add(styledLabel("Budget:"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 1;
        addRow.add(buyerBudgetField, gbc);

        // ligne 2
        gbc.gridy = 1;

        gbc.gridx = 0;
        gbc.weightx = 0;
        addRow.add(styledLabel("Chance:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        addRow.add(buyerChanceField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.gridwidth = 2;
        addBtn.setPreferredSize(new Dimension(110, 32));
        addRow.add(addBtn, gbc);

        buyersList.setBackground(BG_INPUT);
        buyersList.setForeground(TEXT_PRIMARY);
        buyersList.setSelectionBackground(ACCENT);
        buyersList.setSelectionForeground(Color.WHITE);
        buyersList.setFont(FONT_MONO);
        buyersList.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        JScrollPane scroll = new JScrollPane(buyersList);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scroll.getViewport().setBackground(BG_INPUT);

        JButton removeBtn = createSmallButton("✕ Supprimer");
        removeBtn.addActionListener(e -> {
            int idx = buyersList.getSelectedIndex();
            if (idx >= 0) {
                buyersModel.remove(idx);
                buyersParams.remove(idx);
            }
        });

        JPanel bottomBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        bottomBtns.setOpaque(false);
        bottomBtns.add(removeBtn);

        card.add(addRow, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        card.add(bottomBtns, BorderLayout.SOUTH);
        return card;
    }

    // ── Status Card ────────────────────────────────────────────────
    private JPanel buildStatusCard() {
        JPanel card = createCard("📊  Statut en Direct");


        JPanel grid = new JPanel(new GridLayout(2, 2, 12, 12));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));


        grid.add(buildMetricTile("Prix Courant", priceValueLabel, ACCENT));
        grid.add(buildMetricTile("Meilleur Offrant", winnerValueLabel, ACCENT_GREEN));
        grid.add(buildMetricTile("Round", roundValueLabel, ACCENT_AMBER));
        grid.add(buildMetricTile("Tentative", attemptValueLabel, ACCENT_RED));


        card.add(grid, BorderLayout.CENTER);
        return card;
    }


    private JPanel buildMetricTile(String label, JLabel valueLabel, Color accentColor) {
        JPanel tile = new JPanel(new BorderLayout(0, 2)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_INPUT);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                // accent bar on top
                g2.setColor(accentColor);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), 3, 3, 3));
                g2.dispose();
            }
        };
        tile.setOpaque(false);
        tile.setBorder(BorderFactory.createEmptyBorder(12, 14, 10, 14));


        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXT_MUTED);


        valueLabel.setFont(FONT_BIG_NUM);
        valueLabel.setForeground(TEXT_PRIMARY);


        tile.add(lbl, BorderLayout.NORTH);
        tile.add(valueLabel, BorderLayout.CENTER);
        return tile;
    }


    // ── Log Card ───────────────────────────────────────────────────
    private JPanel buildLogCard() {
        JPanel card = createCard("📝  Journal (Log)");


        logArea.setEditable(false);
        logArea.setBackground(BG_INPUT);
        logArea.setForeground(TEXT_SECONDARY);
        logArea.setFont(FONT_MONO);
        logArea.setCaretColor(ACCENT);
        logArea.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);


        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scroll.getViewport().setBackground(BG_INPUT);


        card.add(scroll, BorderLayout.CENTER);
        return card;
    }


    // ════════════════════════════════════════════════════════════════
    //  ACTIONS
    // ════════════════════════════════════════════════════════════════


    private void onAddBuyer() {
        try {
            int budget = Integer.parseInt(buyerBudgetField.getText().trim());
            double chance = Double.parseDouble(buyerChanceField.getText().trim());
            if (chance < 0 || chance > 1) throw new NumberFormatException("Chance must be 0-1");
            buyerCounter++;
            String name = buyerNameField.getText().trim();
            if (name.isEmpty()) name = "Buyer" + buyerCounter;
            String entry = String.format("  %-12s │  Budget: %,d DZD  │  Chance: %.0f%%", name, budget, chance * 100);
            buyersModel.addElement(entry);
            buyersParams.add(new Object[]{name, budget, chance});
            buyerNameField.setText("");
        } catch (Exception ex) {
            showError("Paramètres invalides. Vérifiez le budget (entier) et la chance (0.0 à 1.0).");
        }
    }


    private void onStartAuction() {
        if (auctionStarted) {
            showError("L'enchère est déjà en cours !");
            return;
        }
        if (buyersParams.isEmpty()) {
            showError("Ajoutez au moins un acheteur avant de lancer l'enchère.");
            return;
        }
        try {
            int start  = Integer.parseInt(sellerStartField.getText().trim());
            int min    = Integer.parseInt(sellerMinField.getText().trim());
            int inc    = Integer.parseInt(sellerIncField.getText().trim());
            int dec    = Integer.parseInt(sellerDecField.getText().trim());
            int maxAtt = Integer.parseInt(sellerMaxAttField.getText().trim());


            // Create Seller
            Object[] sellerArgs = new Object[]{start, min, inc, dec, maxAtt};
            AgentController seller = container.createNewAgent("Seller", "SellerAgent", sellerArgs);
            seller.start();
            appendLog("Agent Seller créé — Prix départ: " + String.format("%,d", start) + " DZD");


            // Create Buyers
            for (int i = 0; i < buyersParams.size(); i++) {
                Object[] bp = buyersParams.get(i);
                String name = (String) bp[0];
                int budget = (int) bp[1];
                double chance = (double) bp[2];
                AgentController b = container.createNewAgent(name, "BuyerAgent", new Object[]{budget, chance});
                b.start();
                appendLog("Agent " + name + " créé — Budget: " + String.format("%,d", budget) + " DZD");
            }


            auctionStarted = true;
            startBtn.setEnabled(false);
            startBtn.setText("⏳ Enchère en cours...");
            appendLog("── Enchère démarrée ──");


        } catch (NumberFormatException ex) {
            showError("Paramètres du vendeur invalides. Vérifiez les valeurs numériques.");
        } catch (Exception ex) {
            showError("Erreur lors du lancement des agents:\n" + ex.getMessage());
        }
    }


    // ════════════════════════════════════════════════════════════════
    //  PUBLIC UPDATE METHODS (called by GuiAgent on EDT)
    // ════════════════════════════════════════════════════════════════


    public void setCurrentPrice(String price) {
        try {
            int p = Integer.parseInt(price.trim());
            priceValueLabel.setText(String.format("%,d", p));
        } catch (NumberFormatException e) {
            priceValueLabel.setText(price);
        }
        appendLog("💰  Prix courant → " + price + " DZD");
    }


    public void setCurrentWinner(String winnerInfo) {
        // Format: "BuyerName:Amount"
        String display = winnerInfo.replace(":", " — ") + " DZD";
        winnerValueLabel.setText("<html><center>" + winnerInfo.split(":")[0] + "</center></html>");
        winnerValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        appendLog("🏆  Nouveau meilleur → " + display);
    }


    public void setRound(String round) {
        roundValueLabel.setText(round);
    }


    public void setAttempt(String attempt) {
        attemptValueLabel.setText(attempt);
        appendLog("⏱️  Tentative " + attempt + " sans offre");
    }


    public void showFinalWinner(String finalInfo) {
        appendLog("═══════════════════════════════════");
        appendLog("🎉  ADJUGÉ — " + finalInfo);
        appendLog("═══════════════════════════════════");


        // Show a styled dialog
        JDialog dialog = new JDialog(this, "Résultat de l'Enchère", true);
        dialog.setSize(420, 280);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);


        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                // Top accent stripe
                g2.setColor(finalInfo.equals("aucun") ? ACCENT_RED : ACCENT_GREEN);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), 5, 5, 5));
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(30, 30, 20, 30)
        ));


        JLabel emoji = new JLabel(finalInfo.equals("aucun") ? "😔" : "🎉", SwingConstants.CENTER);
        emoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));


        JLabel titleLbl = new JLabel(
            finalInfo.equals("aucun") ? "Aucun Acheteur" : "ADJUGÉ !",
            SwingConstants.CENTER
        );
        titleLbl.setFont(FONT_TITLE);
        titleLbl.setForeground(finalInfo.equals("aucun") ? ACCENT_RED : ACCENT_GREEN);


        JLabel detailLbl = new JLabel(
            "<html><center>" + (finalInfo.equals("aucun")
                ? "L'enchère s'est terminée sans vente."
                : "Vendu à " + finalInfo.replace("(", "pour <b>").replace(")", "</b>"))
            + "</center></html>",
            SwingConstants.CENTER
        );
        detailLbl.setFont(FONT_BODY);
        detailLbl.setForeground(TEXT_SECONDARY);


        JButton closeBtn = createAccentButton("Fermer");
        closeBtn.addActionListener(ev -> dialog.dispose());


        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        emoji.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(emoji);
        content.add(Box.createVerticalStrut(8));
        content.add(titleLbl);
        content.add(Box.createVerticalStrut(10));
        content.add(detailLbl);
        content.add(Box.createVerticalStrut(18));
        content.add(closeBtn);


        panel.add(content, BorderLayout.CENTER);
        dialog.setContentPane(panel);
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.setVisible(true);


        // Re-enable start after auction ends
        auctionStarted = false;
        startBtn.setEnabled(true);
        startBtn.setText("▶  Lancer l'Enchère");
    }


    public void appendLog(String text) {
        logArea.append(text + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }


    // ════════════════════════════════════════════════════════════════
    //  COMPONENT FACTORIES
    // ════════════════════════════════════════════════════════════════


    private JPanel createCard(String title) {
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));


        JLabel header = new JLabel(title);
        header.setFont(FONT_HEADING);
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));


        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.setOpaque(false);
        wrapper.add(header, BorderLayout.NORTH);
        card.add(wrapper, BorderLayout.NORTH);


        // Return a delegating panel so callers can add to NORTH/CENTER/SOUTH
        // We wrap with a layout trick
        JPanel inner = new JPanel(new BorderLayout(0, 6));
        inner.setOpaque(false);
        card.add(inner, BorderLayout.CENTER);


        // Return a facade that delegates add() to inner
        return new JPanel(new BorderLayout()) {
            {
                setOpaque(false);
                super.add(card, BorderLayout.CENTER);
            }
            @Override
            public void add(Component comp, Object constraints) {
                if (BorderLayout.NORTH.equals(constraints)) {
                    inner.add(comp, BorderLayout.NORTH);
                } else if (BorderLayout.SOUTH.equals(constraints)) {
                    inner.add(comp, BorderLayout.SOUTH);
                } else {
                    inner.add(comp, BorderLayout.CENTER);
                }
            }
        };
    }


    private void addFormRow(JPanel form, GridBagConstraints gbc, int row, String label, JTextField field, String unit) {
        gbc.gridy = row;


        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel lbl = styledLabel(label);
        form.add(lbl, gbc);


        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(field, gbc);


        if (!unit.isEmpty()) {
            gbc.gridx = 2;
            gbc.weightx = 0;
            JLabel u = new JLabel(unit);
            u.setFont(FONT_SMALL);
            u.setForeground(TEXT_MUTED);
            form.add(u, gbc);
        }
    }


    private static JTextField styledField(String text, int cols) {
        JTextField f = new JTextField(text, cols);
        f.setBackground(BG_INPUT);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(ACCENT);
        f.setFont(FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return f;
    }


    private static JLabel styledLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY);
        l.setForeground(TEXT_SECONDARY);
        return l;
    }


    private JButton createAccentButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? ACCENT_HOVER : ACCENT;
                if (!isEnabled()) bg = BORDER_COLOR;
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_HEADING);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 36));
        return btn;
    }


    private JButton createSmallButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? BG_CARD_HOVER : BG_INPUT;
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(BORDER_COLOR);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(TEXT_PRIMARY);
        btn.setFont(FONT_SMALL);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }


    private void showError(String msg) {
        // Use a styled approach instead of default JOptionPane
        JOptionPane pane = new JOptionPane(msg, JOptionPane.ERROR_MESSAGE);
        JDialog dialog = pane.createDialog(this, "Erreur");
        dialog.setVisible(true);
    }
}


