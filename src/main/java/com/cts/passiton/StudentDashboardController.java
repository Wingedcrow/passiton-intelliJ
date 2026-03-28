package com.cts.passiton;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.io.IOException;
import java.util.logging.Logger;

public class StudentDashboardController {

    private static final Logger logger = Logger.getLogger(StudentDashboardController.class.getName());

    @FXML private Button btnProfile;
    @FXML private Button btnSupplies;
    @FXML private Button btnMarketboard;
    @FXML private Button btnTrades;
    @FXML private Button btnLogout;
    @FXML private Label lblWelcome;
    @FXML private Label lblContentArea;

    @FXML
    public void initialize () {
        lblWelcome.setText (" Welcome back, " + CurrentLogin.getFirstName () );
    }

    @FXML
    protected void showProfile() {
        setActiveButton(btnProfile);
        lblContentArea.setText("Profile Settings — Coming Soon.");
    }


    @FXML
    protected void showSupplies() {
        setActiveButton(btnSupplies);
        lblContentArea.setText("Your Owned Supplies — Coming Soon.");
    }


    @FXML
    protected void showMarketboard() {
        try {
            JavaFxDemoApp app = new JavaFxDemoApp();
            app.changeScene("marketboard-view.fxml", 1100, 750);
        } catch (IOException e) {
            logger.severe("Error loading marketboard: " + e.getMessage());
        }
    }


    @FXML
    protected void showTrades() {
        try {
            JavaFxDemoApp app = new JavaFxDemoApp();
            app.changeScene("trades-view.fxml", 1100, 750);
        } catch (IOException e) {
            logger.severe("Error loading trades: " + e.getMessage());
        }
    }


    @FXML
    protected void actionLogout() {
        try {
            CurrentLogin.clearLogin();
            JavaFxDemoApp app = new JavaFxDemoApp();
            app.changeScene("javafx-demo-app-view.fxml", 1100, 750);
        } catch (IOException e) {
            logger.severe("Error returning to login: " + e.getMessage());
        }
    }


    private void setActiveButton(Button active) {

        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 10;";

        btnProfile.setStyle(defaultStyle);
        btnSupplies.setStyle(defaultStyle);
        btnMarketboard.setStyle(defaultStyle);
        btnTrades.setStyle(defaultStyle);


        active.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 10; " +
                "-fx-background-radius: 6;");
    }

}