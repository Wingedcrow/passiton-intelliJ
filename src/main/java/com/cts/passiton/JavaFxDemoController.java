package com.cts.passiton;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JavaFxDemoController {


    public VBox btnRegUser;
    public Hyperlink lnkRegister;
    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblError;


    DatabaseConnection dc = new DatabaseConnection();


    @FXML
    protected void handleStudentLogin() {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();


        if (email.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        try {
            String query = "SELECT * FROM tblusers WHERE email = ? AND password = ?";
            PreparedStatement ps = dc.con.prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                CurrentLogin.setLogin(
                        rs.getInt("user_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email")
                );

                lblError.setVisible(false);
                JavaFxDemoApp app = new JavaFxDemoApp();
                app.changeScene("student-dashboard-view.fxml", 1100, 750);
            } else {
                showError("Invalid email or password.");
            }

        } catch (SQLException e) {
            showError("Database error. Please try again.");
            e.printStackTrace();
        } catch (IOException e) {
            showError("Could not load dashboard.");
            e.printStackTrace();
        }
    }


    @FXML
    protected void handleAdminLogin() {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();


        if (email.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        try {
            String query = "SELECT * FROM tbladmin WHERE email = ? AND password = ?";
            PreparedStatement ps = dc.con.prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                lblError.setVisible(false);
                JavaFxDemoApp app = new JavaFxDemoApp();
                app.changeScene("admin-dashboard-view.fxml", 1100, 750);
            } else {
                showError("Invalid admin credentials.");
            }

        } catch (SQLException e) {
            showError("Database error. Please try again.");
            e.printStackTrace();
        } catch (IOException e) {
            showError("Could not load dashboard.");
            e.printStackTrace();
        }
    }


    @FXML
    protected void handleRegister() {
        try {
            JavaFxDemoApp app = new JavaFxDemoApp();
            app.changeScene("new-reg-user-view.fxml", 1100, 750);
        } catch (IOException e) {
            showError("Could not load registration screen.");
            e.printStackTrace();
        }
    }


    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
    }
}