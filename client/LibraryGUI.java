package client;

import common.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.net.*;

/**
 * Graphical User Interface client for the Online Library System.
 * Replaces the terminal-based LibraryClient with a full Swing GUI.
 * Demonstrates: Sockets, Exception Handling, GUI programming
 *
 * HOW TO RUN (start server first, then):
 *   java -cp out client.LibraryGUI
 */
public class LibraryGUI extends JFrame {

    // ── Network ──────────────────────────────────────────────
    private static final String HOST = "localhost";
    private static final int    PORT = 5000;

    private Socket             socket;
    private ObjectOutputStream out;
    private ObjectInputStream  in;
    private String             userId;

    // ── Colors ────────────────────────────────────────────────
    private static final Color BG         = new Color(245, 245, 243);
    private static final Color SURFACE    = Color.WHITE;
    private static final Color BORDER_C   = new Color(220, 218, 212);
    private static final Color PRIMARY    = new Color(30, 30, 28);
    private static final Color MUTED      = new Color(120, 118, 110);
    private static final Color SUCCESS_BG = new Color(234, 243, 222);
    private static final Color SUCCESS_FG = new Color(59, 109, 17);
    private static final Color DANGER_BG  = new Color(252, 235, 235);
    private static final Color DANGER_FG  = new Color(163, 45, 45);
    // ── UI Panels ────────────────────────────────────────────
    private JPanel     cardPanel;
    private CardLayout cardLayout;

    // Login screen
    private JTextField loginField;

    // Main screen
    private JLabel     welcomeLabel;
    private JTabbedPane tabs;

    // Catalog tab
    private DefaultTableModel catalogModel;
    private JTable            catalogTable;
    private JComboBox<String> filterBox;

    // Search tab
    private JTextField   searchField;
    private DefaultTableModel searchModel;
    private JTable       searchTable;

    // My Books tab
    private DefaultTableModel myModel;
    private JTable            myTable;

    // Status bar
    private JLabel statusBar;

    // ─────────────────────────────────────────────────────────

    public LibraryGUI() {
        setTitle("Online Library System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 620);
        setMinimumSize(new Dimension(750, 500));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);

        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.setBackground(BG);

        cardPanel.add(buildLoginPanel(), "LOGIN");
        cardPanel.add(buildMainPanel(),  "MAIN");

        add(cardPanel, BorderLayout.CENTER);
        cardLayout.show(cardPanel, "LOGIN");
    }

    // ══════════════════════════════════════════════════════════
    //  LOGIN PANEL
    // ══════════════════════════════════════════════════════════
    private JPanel buildLoginPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(BG);

        JPanel card = roundedCard(340, 320);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        // Icon label
        JLabel icon = new JLabel("📚", SwingConstants.CENTER);
        icon.setFont(new Font("Serif", Font.PLAIN, 40));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = styledLabel("Online Library System", 20, Font.BOLD, PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = styledLabel("Enter your username to continue", 13, Font.PLAIN, MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userLbl = styledLabel("Username", 12, Font.PLAIN, MUTED);
        userLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginField = styledTextField();
        loginField.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JButton loginBtn = primaryButton("Enter Library");
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        loginBtn.addActionListener(e -> doLogin());
        loginField.addActionListener(e -> doLogin());

        card.add(icon);
        card.add(Box.createVerticalStrut(12));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(24));
        card.add(userLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(loginField);
        card.add(Box.createVerticalStrut(16));
        card.add(loginBtn);

        outer.add(card);
        return outer;
    }

    // ══════════════════════════════════════════════════════════
    //  MAIN PANEL
    // ══════════════════════════════════════════════════════════
    private JPanel buildMainPanel() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG);

        // ── Header ──
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_C),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));

        JLabel logo = styledLabel("📚  Online Library System", 15, Font.BOLD, PRIMARY);
        welcomeLabel = styledLabel("", 13, Font.PLAIN, MUTED);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        logoutBtn.setForeground(MUTED);
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        logoutBtn.setBackground(SURFACE);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> doLogout());

        JPanel rightH = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightH.setBackground(SURFACE);
        rightH.add(welcomeLabel);
        rightH.add(logoutBtn);

        header.add(logo, BorderLayout.WEST);
        header.add(rightH, BorderLayout.EAST);

        // ── Tabs ──
        tabs = new JTabbedPane(JTabbedPane.LEFT);
        tabs.setBackground(BG);
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabs.setBorder(BorderFactory.createEmptyBorder());

        tabs.addTab("  Catalog  ",   buildCatalogTab());
        tabs.addTab("  Search   ",   buildSearchTab());
        tabs.addTab("  My Books ",   buildMyBooksTab());

        // ── Status bar ──
        statusBar = styledLabel("  Ready", 12, Font.PLAIN, MUTED);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 0, 0, 0, BORDER_C),
            BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));
        statusBar.setOpaque(true);
        statusBar.setBackground(SURFACE);

        root.add(header,    BorderLayout.NORTH);
        root.add(tabs,      BorderLayout.CENTER);
        root.add(statusBar, BorderLayout.SOUTH);
        return root;
    }

    // ── Catalog Tab ──────────────────────────────────────────
    private JPanel buildCatalogTab() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(BG);

        JLabel filterLbl = styledLabel("Filter:", 13, Font.PLAIN, MUTED);
        filterBox = new JComboBox<>(new String[]{"All", "Book", "EBook", "Magazine", "Available only"});
        filterBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
        filterBox.setBackground(SURFACE);

        JButton refreshBtn = secondaryButton("↻  Refresh");
        refreshBtn.addActionListener(e -> loadCatalog());
        filterBox.addActionListener(e -> loadCatalog());

        JButton borrowBtn = primaryButton("Borrow selected");
        borrowBtn.addActionListener(e -> borrowSelected());

        JButton returnBtn = secondaryButton("Return selected");
        returnBtn.addActionListener(e -> returnSelected());

        toolbar.add(filterLbl);
        toolbar.add(filterBox);
        toolbar.add(Box.createHorizontalStrut(12));
        toolbar.add(refreshBtn);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(borrowBtn);
        toolbar.add(returnBtn);

        // Table
        String[] cols = {"ID", "Type", "Title", "Author", "Year", "Details", "Status"};
        catalogModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        catalogTable = styledTable(catalogModel);
        setColumnWidths(catalogTable, new int[]{60, 75, 200, 140, 50, 160, 90});

        JScrollPane scroll = styledScroll(catalogTable);

        p.add(toolbar, BorderLayout.NORTH);
        p.add(scroll,  BorderLayout.CENTER);
        return p;
    }

    // ── Search Tab ───────────────────────────────────────────
    private JPanel buildSearchTab() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setBackground(BG);

        searchField = styledTextField();
        searchField.setToolTipText("Search by title, author, genre...");
        JButton searchBtn = primaryButton("Search");
        searchBtn.addActionListener(e -> doSearch());
        searchField.addActionListener(e -> doSearch());

        top.add(searchField, BorderLayout.CENTER);
        top.add(searchBtn,   BorderLayout.EAST);

        String[] cols = {"ID", "Type", "Title", "Author", "Year", "Details", "Status"};
        searchModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        searchTable = styledTable(searchModel);
        setColumnWidths(searchTable, new int[]{60, 75, 200, 140, 50, 160, 90});

        JButton borrowBtn = primaryButton("Borrow selected");
        borrowBtn.addActionListener(e -> borrowFromSearch());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setBackground(BG);
        bottom.add(borrowBtn);

        p.add(top,       BorderLayout.NORTH);
        p.add(styledScroll(searchTable), BorderLayout.CENTER);
        p.add(bottom,    BorderLayout.SOUTH);
        return p;
    }

    // ── My Books Tab ─────────────────────────────────────────
    private JPanel buildMyBooksTab() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        String[] cols = {"ID", "Type", "Title", "Author", "Year"};
        myModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        myTable = styledTable(myModel);
        setColumnWidths(myTable, new int[]{60, 75, 260, 180, 60});

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(BG);

        JButton refreshBtn = secondaryButton("↻  Refresh");
        refreshBtn.addActionListener(e -> loadMine());

        JButton returnBtn  = secondaryButton("Return selected");
        returnBtn.addActionListener(e -> returnFromMine());

        toolbar.add(refreshBtn);
        toolbar.add(returnBtn);

        p.add(toolbar,             BorderLayout.NORTH);
        p.add(styledScroll(myTable), BorderLayout.CENTER);
        return p;
    }

    // ══════════════════════════════════════════════════════════
    //  ACTIONS
    // ══════════════════════════════════════════════════════════

    private void doLogin() {
        String user = loginField.getText().trim();
        if (user.isEmpty()) { showStatus("Please enter a username.", false); return; }

        try {
            socket = new Socket(HOST, PORT);
            out    = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in     = new ObjectInputStream(socket.getInputStream());
            userId = user;

            welcomeLabel.setText("Welcome, " + userId);
            cardLayout.show(cardPanel, "MAIN");
            loadCatalog();
            showStatus("Connected to server. Welcome, " + userId + "!", true);

        } catch (ConnectException ex) {
            JOptionPane.showMessageDialog(this,
                "Cannot connect to server.\nMake sure the server is running on port " + PORT + ".",
                "Connection Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "Connection error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doLogout() {
        try {
            sendRequest(new LibraryRequest(LibraryRequest.EXIT, "", userId));
            socket.close();
        } catch (Exception ignored) {}
        socket = null; out = null; in = null; userId = null;
        loginField.setText("");
        catalogModel.setRowCount(0);
        searchModel.setRowCount(0);
        myModel.setRowCount(0);
        cardLayout.show(cardPanel, "LOGIN");
    }

    private void loadCatalog() {
        LibraryResponse resp = sendRequest(new LibraryRequest(LibraryRequest.LIST_ALL, "", userId));
        if (resp == null) return;
        catalogModel.setRowCount(0);
        String filter = (String) filterBox.getSelectedItem();
        if (resp.getResults() != null) {
            for (String line : resp.getResults()) {
                Object[] row = parseLine(line);
                if (shouldShow(row, filter)) catalogModel.addRow(row);
            }
        }
        colorizeTable(catalogTable, catalogModel);
        showStatus("Catalog loaded — " + catalogModel.getRowCount() + " item(s) shown.", true);
    }

    private void doSearch() {
        String q = searchField.getText().trim();
        if (q.isEmpty()) { showStatus("Enter a search term.", false); return; }
        LibraryResponse resp = sendRequest(new LibraryRequest(LibraryRequest.SEARCH, q, userId));
        if (resp == null) return;
        searchModel.setRowCount(0);
        if (resp.getResults() != null) {
            for (String line : resp.getResults()) searchModel.addRow(parseLine(line));
        }
        colorizeTable(searchTable, searchModel);
        showStatus(resp.getMessage(), resp.isSuccess());
    }

    private void loadMine() {
        LibraryResponse resp = sendRequest(new LibraryRequest(LibraryRequest.LIST_MINE, "", userId));
        if (resp == null) return;
        myModel.setRowCount(0);
        if (resp.getResults() != null) {
            for (String line : resp.getResults()) {
                Object[] full = parseLine(line);
                myModel.addRow(new Object[]{full[0], full[1], full[2], full[3], full[4]});
            }
        }
        showStatus(resp.getMessage(), resp.isSuccess());
    }

    private void borrowSelected() { borrowFromTable(catalogTable); }
    private void borrowFromSearch() { borrowFromTable(searchTable); }

    private void borrowFromTable(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { showStatus("Select an item first.", false); return; }
        String id = (String) table.getModel().getValueAt(row, 0);
        LibraryResponse resp = sendRequest(new LibraryRequest(LibraryRequest.BORROW, id, userId));
        if (resp == null) return;
        showStatus(resp.getMessage(), resp.isSuccess());
        loadCatalog();
        if (tabs.getSelectedIndex() == 1) doSearch();
    }

    private void returnSelected()  { returnFromTable(catalogTable); }
    private void returnFromMine()  {
        int row = myTable.getSelectedRow();
        if (row < 0) { showStatus("Select an item first.", false); return; }
        String id = (String) myModel.getValueAt(row, 0);
        LibraryResponse resp = sendRequest(new LibraryRequest(LibraryRequest.RETURN, id, userId));
        if (resp == null) return;
        showStatus(resp.getMessage(), resp.isSuccess());
        loadMine();
        loadCatalog();
    }

    private void returnFromTable(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { showStatus("Select an item first.", false); return; }
        String id = (String) table.getModel().getValueAt(row, 0);
        LibraryResponse resp = sendRequest(new LibraryRequest(LibraryRequest.RETURN, id, userId));
        if (resp == null) return;
        showStatus(resp.getMessage(), resp.isSuccess());
        loadCatalog();
    }

    // ══════════════════════════════════════════════════════════
    //  NETWORK HELPER
    // ══════════════════════════════════════════════════════════

    private LibraryResponse sendRequest(LibraryRequest req) {
        try {
            out.writeObject(req);
            out.flush();
            return (LibraryResponse) in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            showStatus("Network error: " + ex.getMessage(), false);
            return null;
        }
    }

    // ══════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════

    // Parse "ID | [Type] Title by Author (Year) | ... | Status"
    private Object[] parseLine(String line) {
        String[] parts = line.split("\\|");
        String id     = parts.length > 0 ? parts[0].trim() : "";
        String rest   = parts.length > 1 ? parts[1].trim() : "";
        String status = parts.length > 2 ? parts[parts.length - 1].trim() : "";
        String details = parts.length > 3 ? parts[2].trim() : "";

        // Extract type from [Type]
        String type = "";
        if (rest.startsWith("[")) {
            int end = rest.indexOf("]");
            if (end > 0) { type = rest.substring(1, end); rest = rest.substring(end + 2).trim(); }
        }
        // Extract title and author: "Title by Author (year)"
        String title = "", author = "", year = "";
        int byIdx = rest.lastIndexOf(" by ");
        if (byIdx > 0) {
            title = rest.substring(0, byIdx).trim();
            String authorYear = rest.substring(byIdx + 4).trim();
            int yStart = authorYear.lastIndexOf("(");
            int yEnd   = authorYear.lastIndexOf(")");
            if (yStart > 0 && yEnd > yStart) {
                author = authorYear.substring(0, yStart).trim();
                year   = authorYear.substring(yStart + 1, yEnd).trim();
            } else { author = authorYear; }
        } else { title = rest; }

        return new Object[]{id, type, title, author, year, details, status};
    }

    private boolean shouldShow(Object[] row, String filter) {
        if (filter == null || filter.equals("All")) return true;
        if (filter.equals("Available only")) return row[6].toString().contains("Available");
        return row[1].toString().equals(filter);
    }

    private void colorizeTable(JTable table, DefaultTableModel model) {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                String status = model.getValueAt(row, model.getColumnCount() - 1).toString();
                if (sel) {
                    c.setBackground(new Color(210, 225, 245));
                    c.setForeground(PRIMARY);
                } else if (status.contains("Available")) {
                    c.setBackground(col == model.getColumnCount()-1 ? SUCCESS_BG : SURFACE);
                    c.setForeground(col == model.getColumnCount()-1 ? SUCCESS_FG : PRIMARY);
                } else {
                    c.setBackground(col == model.getColumnCount()-1 ? DANGER_BG : SURFACE);
                    c.setForeground(col == model.getColumnCount()-1 ? DANGER_FG : PRIMARY);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });
        table.repaint();
    }

    private void showStatus(String msg, boolean ok) {
        statusBar.setText("  " + (ok ? "✓ " : "✗ ") + msg);
        statusBar.setForeground(ok ? SUCCESS_FG : DANGER_FG);
    }

    // ── UI Factory Helpers ────────────────────────────────────

    private JPanel roundedCard(int w, int h) {
        JPanel p = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(BORDER_C);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(w, h));
        return p;
    }

    private JLabel styledLabel(String text, int size, int style, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", style, size));
        l.setForeground(color);
        return l;
    }

    private JTextField styledTextField() {
        JTextField f = new JTextField();
        f.setFont(new Font("SansSerif", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_C, 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return f;
    }

    private JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setBackground(PRIMARY);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        return b;
    }

    private JButton secondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.PLAIN, 13));
        b.setBackground(SURFACE);
        b.setForeground(PRIMARY);
        b.setFocusPainted(false);
        b.setBorder(new LineBorder(BORDER_C, 1, true));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_C, 1, true),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        return b;
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setFont(new Font("SansSerif", Font.PLAIN, 13));
        t.setRowHeight(32);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setBackground(SURFACE);
        t.setSelectionBackground(new Color(210, 225, 245));
        t.setSelectionForeground(PRIMARY);
        t.setFillsViewportHeight(true);
        t.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        t.getTableHeader().setBackground(BG);
        t.getTableHeader().setForeground(MUTED);
        t.getTableHeader().setBorder(new MatteBorder(0, 0, 1, 0, BORDER_C));
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return t;
    }

    private JScrollPane styledScroll(JTable t) {
        JScrollPane s = new JScrollPane(t);
        s.setBorder(new LineBorder(BORDER_C, 1, true));
        s.getViewport().setBackground(SURFACE);
        return s;
    }

    private void setColumnWidths(JTable t, int[] widths) {
        for (int i = 0; i < widths.length && i < t.getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    // ══════════════════════════════════════════════════════════
    //  MAIN
    // ══════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new LibraryGUI().setVisible(true);
        });
    }
}
