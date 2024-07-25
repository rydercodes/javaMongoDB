package org.example.loginsignup;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class LoginSignup extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel insertPanel;
    private JPanel loadPanel;
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private DefaultTableModel tableModel;

    public LoginSignup() {
        setTitle("User Management");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set JetBrains Mono font
        try {
            Font jetBrainsMono = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/JetBrainsMono.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(jetBrainsMono.deriveFont(14f)); // Specify font size
        } catch (Exception e) {
            e.printStackTrace();
        }

        Font font = new Font("JetBrains Mono", Font.PLAIN, 14);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create and add Insert Panel
        insertPanel = new JPanel(new GridBagLayout());
        insertPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(font);
        insertPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setFont(font);
        gbc.gridx = 1;
        insertPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(font);
        insertPanel.add(emailLabel, gbc);

        emailField = new JTextField(20);
        emailField.setFont(font);
        gbc.gridx = 1;
        insertPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(font);
        insertPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(font);
        gbc.gridx = 1;
        insertPanel.add(passwordField, gbc);

        JButton insertButton = new JButton("Insert");
        insertButton.setFont(font);
        insertButton.setBackground(new Color(34, 193, 195));
        insertButton.setForeground(Color.WHITE);
        insertButton.setFocusPainted(false);
        insertButton.addActionListener(e -> insertData());
        gbc.gridx = 1;
        gbc.gridy = 3;
        insertPanel.add(insertButton, gbc);

        // Create and add Load Panel
        loadPanel = new JPanel(new BorderLayout());
        loadPanel.setBackground(Color.WHITE);
        String[] columnNames = {"Username", "Email", "Password"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        table.setFont(font);
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(100, 150, 200));
        JScrollPane scrollPane = new JScrollPane(table);
        loadPanel.add(scrollPane, BorderLayout.CENTER);

        JButton loadButton = new JButton("Load");
        loadButton.setFont(font);
        loadButton.setBackground(new Color(34, 193, 195));
        loadButton.setForeground(Color.WHITE);
        loadButton.setFocusPainted(false);
        loadButton.addActionListener(e -> refreshData());
        loadPanel.add(loadButton, BorderLayout.SOUTH);

        // Add both panels to the CardLayout container
        mainPanel.add(insertPanel, "Insert");
        mainPanel.add(loadPanel, "Load");
        cardLayout.show(mainPanel, "Insert");

        // Create button panel to switch views
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.LIGHT_GRAY);
        JButton switchToInsert = new JButton("Insert View");
        switchToInsert.setFont(font);
        switchToInsert.setBackground(new Color(34, 193, 195));
        switchToInsert.setForeground(Color.WHITE);
        switchToInsert.setFocusPainted(false);
        switchToInsert.addActionListener(e -> cardLayout.show(mainPanel, "Insert"));

        JButton switchToLoad = new JButton("Load View");
        switchToLoad.setFont(font);
        switchToLoad.setBackground(new Color(34, 193, 195));
        switchToLoad.setForeground(Color.WHITE);
        switchToLoad.setFocusPainted(false);
        switchToLoad.addActionListener(e -> {
            cardLayout.show(mainPanel, "Load");
            refreshData();
        });

        buttonPanel.add(switchToInsert);
        buttonPanel.add(switchToLoad);

        add(buttonPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void insertData() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        String uri = "mongodb://localhost:27017";
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("signupDB");
            MongoCollection<Document> collection = database.getCollection("signup");
            Document doc = new Document("username", username)
                    .append("email", email)
                    .append("password", password);
            collection.insertOne(doc);
            JOptionPane.showMessageDialog(this, "Data inserted successfully!");
        }
    }

    private void refreshData() {
        List<Document> data = loadData();
        tableModel.setRowCount(0);
        for (Document doc : data) {
            String username = doc.getString("username");
            String email = doc.getString("email");
            String password = doc.getString("password");
            Object[] row = {username, email, password};
            tableModel.addRow(row);
        }
    }

    private List<Document> loadData() {
        String uri = "mongodb://localhost:27017";
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("signupDB");
            MongoCollection<Document> collection = database.getCollection("signup");
            return collection.find().into(new ArrayList<>());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginSignup form = new LoginSignup();
            form.setVisible(true);
        });
    }
}
