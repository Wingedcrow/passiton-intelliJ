package com.cts.passiton;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * MarketboardController.java
 * This controller manages the marketboard screen displaying
 * all open trade request posted by other student.
 * Logged-in students can browse and claim requests.
 * The controller prevents claiming a request that has already been claimed or completed.
 * On successful claim, the request status is updated in the database and the table refreshed.
 *
 * @author Joshua Howard & Bradley Balram
 * @version 1.0
 * @date (08/04/2026)
 */

public class MarketboardController {

    private static final Logger logger = Logger.getLogger(MarketboardController.class.getName());

    @FXML private Button btnBack;
    @FXML private Button btnPostRequest;
    @FXML private Button btnClaimRequest;
    @FXML private Label lblStatus;

    @FXML private TableView<MarketRequest> tblMarketboard;
    @FXML private TableColumn<MarketRequest, String> colItemName;
    @FXML private TableColumn<MarketRequest, String> colCategory;
    @FXML private TableColumn<MarketRequest, String> colPostedBy;
    @FXML private TableColumn<MarketRequest, String> colDatePosted;
    @FXML private TableColumn<MarketRequest, String> colStatus;

    DatabaseConnection dc = new DatabaseConnection();

    //Load the marketboard data when the screen opens
    @FXML
    public void initialize() {
        loadMarketboardData();
    }

    // Return to the student dashboard
    @FXML
    protected void actionBack() {
        try {
            PassItOnApp app = new PassItOnApp();
            app.changeScene("student-dashboard-view.fxml", 1100, 750);
        } catch (IOException e) {
            showStatus("Could not return to dashboard.", false);
            logger.severe("Error loading dashboard: " + e.getMessage());
        }
    }

    // Goes to the "Trades" screen
    @FXML
    protected void showPostRequest() {
        try {
            PassItOnApp app = new PassItOnApp();
            app.changeScene("trades-view.fxml", 1100, 750);
        } catch (IOException e) {
            showStatus("Could not load trades screen.", false);
            logger.severe("Error loading trades: " + e.getMessage());
        }
    }

    //Claims the selected request and update its status to CLAIMED in the database
    @FXML
    protected void claimRequest() {
        MarketRequest selected = tblMarketboard.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showStatus("Please select a request from the table first.", false);
            return;
        }

        // Prevents the student from claiming their own request
        if (selected.getRequesterId() == CurrentLogin.getUserId()) {
            showStatus("You cannot claim your own request.", false);
            return;
        }

        if (selected.getStatus().equals("CLAIMED")) {
            showStatus("This request has already been claimed.", false);
            return;
        }

        if (selected.getStatus().equals("COMPLETED")) {
            showStatus("This request has already been completed.", false);
            return;
        }

        try {
            // Update the request status and record who claimed it as well as when
            String updateQuery = "UPDATE tblrequest SET status = 'CLAIMED', " +
                    "claimed_at = NOW(), " +
                    "benefactor_id = ? " +
                    "WHERE requestid = ?";
            PreparedStatement ps = dc.con.prepareStatement(updateQuery);
            ps.setInt(1, CurrentLogin.getUserId());
            ps.setInt(2, selected.getRequestId());
            ps.executeUpdate();

            showStatus("Successfully claimed: " + selected.getItemName(), true);
            loadMarketboardData();

        } catch (SQLException e) {
            showStatus("Database error. Could not claim request.", false);
            logger.severe("Error claiming request: " + e.getMessage());
        }
    }

    // Query the database for all open requests and populate the marketboard
    private void loadMarketboardData() {
        ObservableList<MarketRequest> requests = FXCollections.observableArrayList();

        try {
            //Join tblrequest with tblusers to get the full name of the student who posted each request
            String query = "SELECT r.requestid, r.requester_id, r.item_name, r.category, " +
                    "r.status, r.created_at, u.first_name, u.last_name " +
                    "FROM tblrequest r " +
                    "JOIN tblusers u ON r.requester_id = u.user_id " +
                    "WHERE r.status = 'OPEN' " +  // only show OPEN requests
                    "ORDER BY r.created_at DESC";

            ResultSet rs = dc.stat.executeQuery(query);

            while (rs.next()) {
                requests.add(new MarketRequest(
                        rs.getInt("requestid"),
                        rs.getInt("requester_id"),   //
                        rs.getString("item_name"),
                        rs.getString("category"),
                        rs.getString("first_name") + " " + rs.getString("last_name"),
                        rs.getString("created_at"),
                        rs.getString("status")
                ));
            }

            colItemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
            colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
            colPostedBy.setCellValueFactory(new PropertyValueFactory<>("postedBy"));
            colDatePosted.setCellValueFactory(new PropertyValueFactory<>("datePosted"));
            colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

            tblMarketboard.setItems(requests);

        } catch (SQLException e) {
            showStatus("Could not load marketboard data.", false);
            logger.severe("Error loading marketboard: " + e.getMessage());
        }
    }

    //Display a status message below the table in green for success or red for failure
    private void showStatus(String message, boolean success) {
        lblStatus.setText(message);
        lblStatus.setStyle(success
                ? "-fx-font-size: 13px; -fx-text-fill: #2e7d32;"
                : "-fx-font-size: 13px; -fx-text-fill: #c62828;");
        lblStatus.setVisible(true);
    }
}