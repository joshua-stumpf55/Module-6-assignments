import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class BatchInsertPerformance {

    private JFrame frame;
    private JTextField urlField;
    private JTextField userField;
    private JPasswordField passwordField;
    private Connection connection;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BatchInsertPerformance::new);
    }

    public BatchInsertPerformance() {
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame = new JFrame("Database Connection");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // DB Connection Panel
        JPanel dbPanel = new JPanel(new GridLayout(4, 2));
        dbPanel.add(new JLabel("Database URL:"));
        urlField = new JTextField("jdbc:sqlite:temp.db");
        dbPanel.add(urlField);

        dbPanel.add(new JLabel("Username:"));
        userField = new JTextField();
        dbPanel.add(userField);

        dbPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        dbPanel.add(passwordField);

        JButton connectButton = new JButton("Connect to Database");
        connectButton.addActionListener(e -> connectToDatabase());

        frame.add(dbPanel, BorderLayout.CENTER);
        frame.add(connectButton, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
    }

    private void connectToDatabase() {
        String url = urlField.getText();
        String user = userField.getText();
        String password = new String(passwordField.getPassword());

        try {
            connection = DriverManager.getConnection(url, user, password);
            createTable();
            showInsertOptions();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Connection failed: " + e.getMessage());
        }
    }

    private void createTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS Temp (num1 DOUBLE, num2 DOUBLE, num3 DOUBLE)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showInsertOptions() {
        JFrame insertFrame = new JFrame("Insert Records");
        insertFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        insertFrame.setLayout(new FlowLayout());

        JButton btnInsertWithBatch = new JButton("Insert with Batch");
        JButton btnInsertWithoutBatch = new JButton("Insert without Batch");

        btnInsertWithBatch.addActionListener(e -> {
            long startTime = System.currentTimeMillis();
            insertRecords(true);
            long endTime = System.currentTimeMillis();
            JOptionPane.showMessageDialog(insertFrame, "Batch Insert Time: " + (endTime - startTime) + " ms");
        });

        btnInsertWithoutBatch.addActionListener(e -> {
            long startTime = System.currentTimeMillis();
            insertRecords(false);
            long endTime = System.currentTimeMillis();
            JOptionPane.showMessageDialog(insertFrame, "Single Insert Time: " + (endTime - startTime) + " ms");
        });

        insertFrame.add(btnInsertWithBatch);
        insertFrame.add(btnInsertWithoutBatch);
        insertFrame.pack();
        insertFrame.setVisible(true);
    }

    private void insertRecords(boolean useBatch) {
        String sql = "INSERT INTO Temp(num1, num2, num3) VALUES(?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < 1000; i++) {
                double num1 = Math.random();
                double num2 = Math.random();
                double num3 = Math.random();

                pstmt.setDouble(1, num1);
                pstmt.setDouble(2, num2);
                pstmt.setDouble(3, num3);

                if (useBatch) {
                    pstmt.addBatch();
                    if (i % 100 == 0) { 
                        pstmt.executeBatch();
                    }
                } else {
                    pstmt.executeUpdate();
                }
            }
            if (useBatch) {
                pstmt.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
