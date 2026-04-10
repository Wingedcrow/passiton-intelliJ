/**
 * SettingsController.java handles the Profile Settings scene for the PassItOn application.
 * It allows the user to modify profile information such as their passwords. Additionally, it allows the user to delete their account.
 * Successful modification of profile information, are applied to the database table, 'tblusers'.
 *
 * @author Bradley Balram & Joshua Howard
 * @version 1.0
 * @date (10/04/2026)
 */

package com.cts.passiton;

import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.sql.ResultSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import java.util.Optional;



public class SettingsController {

    //Database connection
    DatabaseConnection dc = new DatabaseConnection();
    private static final Logger logger = Logger.getLogger(SettingsController.class.getName());

    @FXML
    private Button btnBack;
    @FXML
    private Button btnDelAcc;
    @FXML
    private Button btnUpdatePass;
    @FXML
    private TextField txtcurrPass;
    @FXML
    private TextField txtnewPass;
    @FXML
    private Label lblStatus;

    public static int loggedInUserId;

    //-------------------------------------------------
    // Navigates the user back to the student dashboard
    //-------------------------------------------------
    @FXML
    protected void actionBack() {
        try {
            JavaFxDemoApp app = new JavaFxDemoApp();
            app.changeScene("student-dashboard-view.fxml", 1100, 750);
        } catch (IOException e) {
            this.showStatus("Could not return to dashboard.", false);
            logger.severe("Error loading dashboard: " + e.getMessage());
        }

    }

    private void showStatus(String message, boolean success) {
        this.lblStatus.setText(message);
        this.lblStatus.setStyle(success ? "-fx-font-size: 13px; -fx-text-fill: #2e7d32;" : "-fx-font-size: 13px; -fx-text-fill: #c62828;");
        this.lblStatus.setVisible(true);
        this.lblStatus.setManaged(true);
    }


    //-------------------------------------------------------------

    // DELETE ACCOUNT CODE - When user clicks delete account button

    //-------------------------------------------------------------

    @FXML
    private void handleDeleteAccount(MouseEvent event) {
        // Confirmation popup
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Account");
        confirmAlert.setHeaderText("Are you sure you want to delete your account?");
        confirmAlert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmAlert.showAndWait();   // waits for user to click something

        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (deleteAccount()) {
                // Wipes user session data
                UserSession.clearSession();

                // Back to log in screen
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cts/passiton/javafx-demo-app-view.fxml"));
                    Parent root = loader.load();

                    // Change window to log in screen scene
                    Stage stage = (Stage) btnDelAcc.getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not return to login screen.");
                }

            } else {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete account. Please try again.");
            }
        }
    }

    // ---------------------------------------------------------------------
    // REMOVES USER FROM DATABASE - ALSO STORES IT IN 'deletedaccounts' TABLE
    // ---------------------------------------------------------------------
    private boolean deleteAccount() {
        try {
            DatabaseConnection db = new DatabaseConnection();


            String selectSql = "SELECT * FROM tblusers WHERE user_id = ?";
            db.ps = db.con.prepareStatement(selectSql);
            db.ps.setInt(1, UserSession.getUserId());
            ResultSet rs = db.ps.executeQuery();

            if (rs.next()) {
                // STORES THEIR INFO IN deletedaccounts TABLE (FOR ADMIN USE)
                String insertSql = "INSERT INTO deletedaccounts (user_id, email, first_name, last_name) VALUES (?, ?, ?, ?)";
                db.ps = db.con.prepareStatement(insertSql);
                db.ps.setInt(1, rs.getInt("user_id"));
                db.ps.setString(2, rs.getString("email"));
                db.ps.setString(3, rs.getString("first_name"));
                db.ps.setString(4, rs.getString("last_name"));
                db.ps.executeUpdate();

                // DELETES ACCOUNT FROM tblusers TABLE
                String deleteSql = "DELETE FROM passiton.tblusers WHERE user_id = ?";
                db.ps = db.con.prepareStatement(deleteSql);
                db.ps.setInt(1, UserSession.getUserId());
                int rowsAffected = db.ps.executeUpdate();
                return rowsAffected > 0;

            } else {
                System.out.println("User not found in database.");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Alert dialog
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    // -------------------------------------------------------------------------

    // CODE FOR CHANGING USER PASSWORD

    // -------------------------------------------------------------------------

    @FXML
    private void handleUpdatePass(ActionEvent event) {
        String currentPass = txtcurrPass.getText().trim();
        String newPass = txtnewPass.getText().trim();

        // Make sure neither field is empty
        if (currentPass.isEmpty() || newPass.isEmpty()) {
            showStatus("Please fill in both password fields.", false);
            return;
        }

        // Make sure the new password is actually different
        if (currentPass.equals(newPass)) {
            showStatus("New password must be different from current password.", false);
            return;
        }

        // Make sure the current password matches what's in the database
        if (!verifyCurrentPass(currentPass)) {
            showStatus("Current password is incorrect.", false);
            return;
        }


        if (updatePassDB(newPass)) {
            // Update the session password to reflect the change
            UserSession.startSession(UserSession.getUserId(), newPass);
            showStatus("Password updated successfully!", true);
            txtcurrPass.clear();
            txtnewPass.clear();
        } else {
            showStatus("Failed to update password. Please try again.", false);
        }
    }

    // Checks that the password the user typed matches their actual password in the database
    private boolean verifyCurrentPass(String currentPass) {
        String sql = "SELECT * FROM tblusers WHERE user_id = ? AND password = ?";

        try {
            DatabaseConnection db = new DatabaseConnection();
            db.ps = db.con.prepareStatement(sql);
            db.ps.setInt(1, UserSession.getUserId());
            db.ps.setString(2, currentPass);

            ResultSet rs = db.ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Updates the user's password in the database to the new email
    private boolean updatePassDB(String newPass) {
        String sql = "UPDATE tblusers SET password = ? WHERE user_id = ?";

        try {
            DatabaseConnection db = new DatabaseConnection();
            db.ps = db.con.prepareStatement(sql);
            db.ps.setString(1, newPass);
            db.ps.setInt(2, UserSession.getUserId());

            int rowsAffected = db.ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}


