package com.cts.passiton;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Hyperlink;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class PassItOnRegisterController {
    private static final Logger logger = Logger.getLogger(PassItOnRegisterController.class.getName());

    @FXML private TextField txtFirstName;
    @FXML private TextField txtLastName;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private Label lblError;
    @FXML private Label lblSuccess;

    DatabaseConnection dc = new DatabaseConnection();


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


    @FXML
    protected void actionBack() {
        try {
            JavaFxDemoApp app = new JavaFxDemoApp();
            app.changeScene("javafx-demo-app-view.fxml", 1100, 750);
        } catch (IOException e) {
            showError("Could not load login screen.");
            logger.severe("Error loading login screen: " + e.getMessage());
        }
    }


    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
        lblSuccess.setVisible(false);
    }

    private void showSuccess(String message) {
        lblSuccess.setText(message);
        lblSuccess.setVisible(true);
        lblError.setVisible(false);
    }

    private void clearForm() {
        txtFirstName.clear();
        txtLastName.clear();
        txtEmail.clear();
        txtPassword.clear();
        txtConfirmPassword.clear();
    }
}

