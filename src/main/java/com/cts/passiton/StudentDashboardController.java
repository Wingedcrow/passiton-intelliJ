package com.cts.passiton;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.io.IOException;
import java.util.logging.Logger;

/** StudentDashboardController.java
 * This controller manages the student dashboard screen.
 * It displays after a student logs in and consists of a welcome message, an image,
 * and buttons to navigate to Supplies, Marketboard, Trades.
 * It also contains the logout action which clears the current session and returns to the login screen.
 *
 * @author Joshua Howard & Bradley Balram
 * @version 1.0
 * @date (08/04/2026)
 */
public class StudentDashboardController {

    private static final Logger logger = Logger.getLogger(StudentDashboardController.class.getName());
    @FXML private Button btnProfile;
    @FXML private Button btnSupplies;
    @FXML private Button btnMarketboard;
    @FXML private Button btnTrades;
    @FXML private Button btnLogout;
    @FXML private Label lblWelcome;
    @FXML private Label lblContentArea;

    // Displays the welcome message
    // alongside the currently logged in user's first name
    @FXML
    public void initialize () {
        lblWelcome.setText (" Welcome back, " + CurrentLogin.getFirstName () );
    }

    @FXML
    protected void showProfile() {
        try {
            PassItOnApp app = new PassItOnApp();
            app.changeScene("Settings-view.fxml", 1100, 750);
        } catch (IOException e) {
            logger.severe("Error loading supplies: " + e.getMessage());
        }
    }

    // navigates to the supplies screen
    @FXML
    protected void showSupplies() {
        try {
            PassItOnApp app = new PassItOnApp();
            app.changeScene("supplies-view.fxml", 1100, 750);
        } catch (IOException e) {
            logger.severe("Error loading supplies: " + e.getMessage());
        }
    }

    // Navigate to the marketboard screen
    @FXML
    protected void showMarketboard() {
        try {
            PassItOnApp app = new PassItOnApp();
            app.changeScene("marketboard-view.fxml", 1100, 750);
        } catch (IOException e) {
            logger.severe("Error loading marketboard: " + e.getMessage());
        }
    }

    // Navigate to the trades screen
    @FXML
    protected void showTrades() {
        try {
            PassItOnApp app = new PassItOnApp();
            app.changeScene("trades-view.fxml", 1100, 750);
        } catch (IOException e) {
            logger.severe("Error loading trades: " + e.getMessage());
        }
    }

    // Clears the current session and return to the login screen
    @FXML
    protected void actionLogout() {
        try {
            CurrentLogin.clearLogin();
            PassItOnApp app = new PassItOnApp();
            app.changeScene("passiton-login-app-view.fxml", 1100, 750);
        } catch (IOException e) {
            logger.severe("Error returning to login: " + e.getMessage());
        }
    }

    // Highlight the selected navigation button
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