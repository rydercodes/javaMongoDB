package org.example.load;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Load {

    public static List<Document> loadData() {
        String uri = "mongodb://localhost:27017";
        MongoClient mongoClient = MongoClients.create(uri);
        MongoDatabase database = mongoClient.getDatabase("signupDB");
        MongoCollection<Document> collection = database.getCollection("signup");

        List<Document> documents = collection.find().into(new ArrayList<>());

        mongoClient.close();
        return documents;
    }

    public static class DataDisplayForm extends JFrame {
        private DefaultTableModel tableModel;

        public DataDisplayForm(List<Document> data) {
            setTitle("User Data");
            setSize(500, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null); // Center the frame

            // Table column names
            String[] columnNames = {"Username", "Email", "Password"};

            // Create table model and set column names
            tableModel = new DefaultTableModel(columnNames, 0);
            populateTable(data);

            // Create the JTable and set the model
            JTable table = new JTable(tableModel);

            // Add the table to a scroll pane
            JScrollPane scrollPane = new JScrollPane(table);
            add(scrollPane, BorderLayout.CENTER);

            // Create and add the "Sync" button
            JButton syncButton = new JButton("Sync");
            syncButton.addActionListener(e -> refreshData());
            add(syncButton, BorderLayout.SOUTH);
        }

        private void populateTable(List<Document> data) {
            tableModel.setRowCount(0); // Clear existing data
            for (Document doc : data) {
                String username = doc.getString("username");
                String email = doc.getString("email");
                String password = doc.getString("password"); // Note: avoid showing passwords!
                Object[] row = {username, email, password};
                tableModel.addRow(row);
            }
        }

        private void refreshData() {
            List<Document> newData = Load.loadData();
            populateTable(newData);
        }
    }

    public static void main(String[] args) {
        // Load data from MongoDB
        List<Document> data = loadData();

        // Create and display the form
        SwingUtilities.invokeLater(() -> {
            DataDisplayForm form = new DataDisplayForm(data);
            form.setVisible(true);
        });
    }
}
