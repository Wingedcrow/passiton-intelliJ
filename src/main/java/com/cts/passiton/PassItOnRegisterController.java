package com.cts.passiton;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * PassItOnRegisterController.java
 * This controller handles the new user registration screen.
 * It validates the input fields before attempting to create a new account.
 * Checks that all fields are filled and email properly formatted not already in use
 * and that the passwords match and meet the minimum length
 * On success the details are inserted into tblusers.
 *
 * @author Bradley Balram & Joshua Howard
 * @version 1.0
 * @date (08/04/2026)
 */
public class PassItOnRegisterController {
    private static final Logger logger = Logger.getLogger(PassItOnRegisterController.class.getName());
    public Hyperlink lnkBack;
    public Button btnRegister;

    @FXML private TextField txtFirstName;
    @FXML private TextField txtLastName;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private Label lblError;
    @FXML private Label lblSuccess;

    DatabaseConnection dc = new DatabaseConnection();

    // Validates all the fields and registers a new user account if checks are positive
    @FXML
    protected void actionRegister() {
        String firstName    = txtFirstName.getText().trim();
        String lastName     = txtLastName.getText().trim();
        String email        = txtEmail.getText().trim();
        String password     = txtPassword.getText().trim();
        String confirmPass  = txtConfirmPassword.getText().trim();


        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showError("Please enter a valid email address.");
            return;
        }

        if (!password.equals(confirmPass)) {
            showError("Passwords do not match.");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        }

        // Check if an account with this email already exists before inserting
        try {
            String checkQuery = "SELECT * FROM tblusers WHERE email = ?";
            PreparedStatement checkPs = dc.con.prepareStatement(checkQuery);
            checkPs.setString(1, email);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                showError("An account with this email already exists.");
                return;
            }
        } catch (SQLException e) {
            showError("Database error. Please try again.");
            logger.severe("Error checking email: " + e.getMessage());
            return;
        }

        // Insert the new user record into tblusers
        try {
            String insertQuery = "INSERT INTO tblusers (first_name, last_name, email, password) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = dc.con.prepareStatement(insertQuery);
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.executeUpdate();


            showSuccess("Account created successfully! You can now sign in.");
            clearForm();

        } catch (SQLException e) {
            showError("Could not create account. Please try again.");
            logger.severe("Error inserting user: " + e.getMessage());
        }
    }

    // Return to the login screen
    @FXML
    protected void actionBack() {
        try {
            PassItOnApp app = new PassItOnApp();
            app.changeScene("passiton-login-app-view.fxml", 1100, 750);
        } catch (IOException e) {
            showError("Could not load login screen.");
            logger.severe("Error loading login screen: " + e.getMessage());
        }
    }

    // Display and error message and hide the success label
    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
        lblSuccess.setVisible(false);
    }

    //Display a success message and hide the error label
    private void showSuccess(String message) {
        lblSuccess.setText(message);
        lblSuccess.setVisible(true);
        lblError.setVisible(false);
    }
    //Reset all input fields after a successful registration
    private void clearForm() {
        txtFirstName.clear();
        txtLastName.clear();
        txtEmail.clear();
        txtPassword.clear();
        txtConfirmPassword.clear();
    }
}

