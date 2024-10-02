import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class StaffManagement {

    private static final String DATABASE_URL = "jdbc:sqlite:staff.db";

    public static void main(String[] args) {
        createTable();
        new StaffManagement().createAndShowGUI();
    }

    private static void createTable() {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS Staff (" +
                    "id CHAR(9) NOT NULL PRIMARY KEY, " +
                    "lastName VARCHAR(15), " +
                    "firstName VARCHAR(15), " +
                    "mi CHAR(1), " +
                    "address VARCHAR(20), " +
                    "city VARCHAR(20), " +
                    "state CHAR(2), " +
                    "telephone CHAR(10), " +
                    "email VARCHAR(40))";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Staff Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(4, 2));


        JTextField entryId = new JTextField();
        JTextField entryLastName = new JTextField();
        JTextField entryFirstName = new JTextField();
        JTextField entryMi = new JTextField();
        JTextField entryAddress = new JTextField();
        JTextField entryCity = new JTextField();
        JTextField entryState = new JTextField();
        JTextField entryTelephone = new JTextField();
        JTextField entryEmail = new JTextField();


        frame.add(new JLabel("ID:"));
        frame.add(entryId);
        frame.add(new JLabel("Last Name:"));
        frame.add(entryLastName);
        frame.add(new JLabel("First Name:"));
        frame.add(entryFirstName);
        frame.add(new JLabel("MI:"));
        frame.add(entryMi);
        frame.add(new JLabel("Address:"));
        frame.add(entryAddress);
        frame.add(new JLabel("City:"));
        frame.add(entryCity);
        frame.add(new JLabel("State:"));
        frame.add(entryState);
        frame.add(new JLabel("Telephone:"));
        frame.add(entryTelephone);
        frame.add(new JLabel("Email:"));
        frame.add(entryEmail);


        JButton btnView = new JButton("View");
        JButton btnInsert = new JButton("Insert");
        JButton btnUpdate = new JButton("Update");

        frame.add(btnView);
        frame.add(btnInsert);
        frame.add(btnUpdate);


        btnView.addActionListener(e -> viewRecord(entryId.getText(), entryLastName, entryFirstName, entryMi, entryAddress, entryCity, entryState, entryTelephone, entryEmail));
        btnInsert.addActionListener(e -> insertRecord(entryId.getText(), entryLastName.getText(), entryFirstName.getText(), entryMi.getText(), entryAddress.getText(), entryCity.getText(), entryState.getText(), entryTelephone.getText(), entryEmail.getText()));
        btnUpdate.addActionListener(e -> updateRecord(entryId.getText(), entryLastName.getText(), entryFirstName.getText(), entryMi.getText(), entryAddress.getText(), entryCity.getText(), entryState.getText(), entryTelephone.getText(), entryEmail.getText()));


        frame.pack();
        frame.setVisible(true);
    }

    private void viewRecord(String id, JTextField lastNameField, JTextField firstNameField, JTextField miField,
                            JTextField addressField, JTextField cityField, JTextField stateField,
                            JTextField telephoneField, JTextField emailField) {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Staff WHERE id = ?")) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                lastNameField.setText(rs.getString("lastName"));
                firstNameField.setText(rs.getString("firstName"));
                miField.setText(rs.getString("mi"));
                addressField.setText(rs.getString("address"));
                cityField.setText(rs.getString("city"));
                stateField.setText(rs.getString("state"));
                telephoneField.setText(rs.getString("telephone"));
                emailField.setText(rs.getString("email"));
            } else {
                JOptionPane.showMessageDialog(null, "Record not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertRecord(String id, String lastName, String firstName, String mi, String address,
                              String city, String state, String telephone, String email) {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Staff (id, lastName, firstName, mi, address, city, state, telephone, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            pstmt.setString(1, id);
            pstmt.setString(2, lastName);
            pstmt.setString(3, firstName);
            pstmt.setString(4, mi);
            pstmt.setString(5, address);
            pstmt.setString(6, city);
            pstmt.setString(7, state);
            pstmt.setString(8, telephone);
            pstmt.setString(9, email);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Record inserted successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error inserting record: " + e.getMessage());
        }
    }

    private void updateRecord(String id, String lastName, String firstName, String mi, String address,
                              String city, String state, String telephone, String email) {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement("UPDATE Staff SET lastName = ?, firstName = ?, mi = ?, address = ?, city = ?, state = ?, telephone = ?, email = ? WHERE id = ?")) {
            pstmt.setString(1, lastName);
            pstmt.setString(2, firstName);
            pstmt.setString(3, mi);
            pstmt.setString(4, address);
            pstmt.setString(5, city);
            pstmt.setString(6, state);
            pstmt.setString(7, telephone);
            pstmt.setString(8, email);
            pstmt.setString(9, id);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Record updated successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "Record not found.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error updating record: " + e.getMessage());
        }
    }
}
