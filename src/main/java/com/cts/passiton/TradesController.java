package com.cts.passiton;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * TradesController.java
 *This controller manages the trade screen, which allows student to post
 * requests for items the need and manage requests they have already posted.
 * Students who have claimed another student's request can set a meeting location
 * and swap time using dropdown menus directly inside the table.
 * Once a meetup is agreed, both the requester and the benefactor must each press
 * "satisfied" to confirm the trade.
 * When both parties interact with the button, the items are updated in the supplies.
 * I also put a limit of 3 active request.
 *
 * @author Joshua Howard & Bradley Balram
 * @version 1.0
 * @date (09/04/2026)
 */

public class TradesController implements Initializable {

    private static final Logger logger = Logger.getLogger(TradesController.class.getName());

    @FXML private ComboBox<String> cmbCategory;
    @FXML private ComboBox<String> cmbItemName;
    @FXML private ComboBox<String> cmbUrgency;
    @FXML private Button btnPostRequest;
    @FXML private Label lblPostStatus;
    @FXML private Label lblRequestLimit;
    @FXML private TableView<TradeRequest> tblMyRequests;
    @FXML private TableColumn<TradeRequest, String> colMyItemName;
    @FXML private TableColumn<TradeRequest, String> colMyCategory;
    @FXML private TableColumn<TradeRequest, String> colMyUrgency;
    @FXML private TableColumn<TradeRequest, String> colMyStatus;
    @FXML private TableColumn<TradeRequest, String> colMyClaimedBy;
    @FXML private TableColumn<TradeRequest, String> colMyLocation;
    @FXML private TableColumn<TradeRequest, String> colMyTime;
    @FXML private TableColumn<TradeRequest, String> colMyExpires;
    @FXML private TableColumn<TradeRequest, String> colMyAction;
    @FXML private Button btnDeleteRequest;
    @FXML private TableView<TradeRequest> tblClaimedTrades;
    @FXML private TableColumn<TradeRequest, String> colClaimedItemName;
    @FXML private TableColumn<TradeRequest, String> colClaimedCategory;
    @FXML private TableColumn<TradeRequest, String> colClaimedRequestedBy;
    @FXML private TableColumn<TradeRequest, String> colClaimedStatus;
    @FXML private TableColumn<TradeRequest, String> colClaimedLocation;
    @FXML private TableColumn<TradeRequest, String> colClaimedTime;
    @FXML private TableColumn<TradeRequest, String> colClaimedExpires;
    @FXML private TableColumn<TradeRequest, String> colClaimedAction;
    @FXML private Button btnBack;

    DatabaseConnection dc = new DatabaseConnection();

    // Populates the category and urgency dropdowns, cleans up expired requests , loads both tables.
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmbCategory.setItems(FXCollections.observableArrayList(
                "Books", "Tools", "Computer Components"
        ));
        cmbUrgency.setItems(FXCollections.observableArrayList(
                "LOW", "MEDIUM", "HIGH"
        ));
        //When a category is selected, populate the item name dropdown with matching items from tblitems.
        cmbCategory.setOnAction(e -> loadItemsForCategory());
        deleteExpiredRequests();
        loadMyRequestsData();
        loadClaimedTradesData();
        checkRequestLimit();
    }

    // Load items from tblitems based on selected category.
    private void loadItemsForCategory() {
        String selectedCategory = cmbCategory.getValue();
        if (selectedCategory == null) return;

        try {
            ObservableList<String> items = FXCollections.observableArrayList();
            String query = "SELECT item_name FROM tblitems " +
                    "WHERE category = ? AND is_active = TRUE " +
                    "ORDER BY item_name";
            PreparedStatement ps = dc.con.prepareStatement(query);
            ps.setString(1, selectedCategory);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) items.add(rs.getString("item_name"));
            cmbItemName.setItems(items);
            cmbItemName.setValue(null);
            cmbItemName.setPromptText("Select item");
        } catch (SQLException e) {
            showPostStatus("Could not load items.", false);
            logger.severe("Error loading items: " + e.getMessage());
        }
    }

    // Validate and post a new trade request into the database.
    @FXML
    protected void actionPostRequest() {
        String itemName = cmbItemName.getValue();
        String category = cmbCategory.getValue();
        String urgency  = cmbUrgency.getValue();

        if (itemName == null || category == null || urgency == null) {
            showPostStatus("Please fill in all fields.", false);
            return;
        }

        if (getUserRequestCount() >= 3) {
            showPostStatus("You have reached the maximum of 3 requests.", false);
            lblRequestLimit.setVisible(true);
            return;
        }

        try {
            String query = "INSERT INTO tblrequest " +
                    "(requester_id, item_name, category, urgency, status) " +
                    "VALUES (?, ?, ?, ?, 'OPEN')";
            PreparedStatement ps = dc.con.prepareStatement(query);
            ps.setInt(1, CurrentLogin.getUserId());
            ps.setString(2, itemName);
            ps.setString(3, category);
            ps.setString(4, urgency);
            ps.executeUpdate();

            showPostStatus("Request posted successfully!", true);
            clearPostForm();
            loadMyRequestsData();
            checkRequestLimit();

        } catch (SQLException e) {
            showPostStatus("Database error. Could not post request.", false);
            logger.severe("Error posting request: " + e.getMessage());
        }
    }

    // Delete selected request from the database, also checks that the request belongs to the login.
    @FXML
    protected void actionDeleteRequest() {
        TradeRequest selected = tblMyRequests.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showPostStatus("Please select a request to delete.", false);
            return;
        }

        try {
            String query = "DELETE FROM tblrequest WHERE requestid = ? AND requester_id = ?";
            PreparedStatement ps = dc.con.prepareStatement(query);
            ps.setInt(1, selected.getRequestId());
            ps.setInt(2, CurrentLogin.getUserId());
            ps.executeUpdate();

            showPostStatus("🗑 Request deleted successfully.", true);
            loadMyRequestsData();
            checkRequestLimit();

        } catch (SQLException e) {
            showPostStatus("Database error. Could not delete request.", false);
            logger.severe("Error deleting request: " + e.getMessage());
        }
    }

    //Return to the student dashboard
    @FXML
    protected void actionBack() {
        try {
            PassItOnApp app = new PassItOnApp();
            app.changeScene("student-dashboard-view.fxml", 1100, 750);
        } catch (IOException e) {
            logger.severe("Error loading dashboard: " + e.getMessage());
        }
    }

    // Load all requests posted by the current user into my requests table.
    private void loadMyRequestsData() {
        ObservableList<TradeRequest> trades = FXCollections.observableArrayList();

        try {
            // Uses COALESCE to display a readable messages when the request has not yet been claimed.
            String query = "SELECT r.requestid, r.item_name, r.category, r.urgency, " +
                    "r.status, r.location, r.swap_time, r.expires_at, " +
                    "r.requester_confirmed, r.benefactor_confirmed, " +
                    "COALESCE(CONCAT(b.first_name, ' ', b.last_name), 'Not yet claimed') AS claimed_by " +
                    "FROM tblrequest r " +
                    "LEFT JOIN tblusers b ON r.benefactor_id = b.user_id " +
                    "WHERE r.requester_id = ? " +
                    "ORDER BY r.created_at DESC";

            PreparedStatement ps = dc.con.prepareStatement(query);
            ps.setInt(1, CurrentLogin.getUserId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                trades.add(new TradeRequest(
                        rs.getInt("requestid"),
                        rs.getString("item_name"),
                        rs.getString("category"),
                        rs.getString("urgency"),
                        rs.getString("status"),
                        rs.getString("claimed_by"),
                        "",
                        rs.getString("location") != null ? rs.getString("location") : "Not set",
                        rs.getString("swap_time") != null ? rs.getString("swap_time") : "Not set",
                        rs.getString("expires_at"),
                        rs.getBoolean("requester_confirmed"),
                        rs.getBoolean("benefactor_confirmed")
                ));
            }

            colMyItemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
            colMyCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
            colMyUrgency.setCellValueFactory(new PropertyValueFactory<>("urgency"));
            colMyStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            colMyClaimedBy.setCellValueFactory(new PropertyValueFactory<>("claimedBy"));
            colMyLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
            colMyTime.setCellValueFactory(new PropertyValueFactory<>("swapTime"));
            colMyExpires.setCellValueFactory(new PropertyValueFactory<>("expiresAt"));

            // Column displays different controls depending on the current status of the request.
            colMyAction.setCellFactory(col -> new TableCell<TradeRequest, String>() {
                private final Button btnAgree      = new Button("Agree");
                private final Button btnSatisfied  = new Button("Satisfied");
                private final Label  lblWaiting    = new Label("Waiting...");

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) { setGraphic(null); return; }

                    TradeRequest request = getTableView().getItems().get(getIndex());

                    switch (request.getStatus()) {
                        //CLAIMED - Show the Agree button so the requester can confirm the meetup details.
                        case "CLAIMED":
                            btnAgree.setStyle("-fx-background-color: #2e7d32; " +
                                    "-fx-text-fill: white; -fx-font-weight: bold; " +
                                    "-fx-background-radius: 6; -fx-cursor: hand;");
                            btnAgree.setOnAction(e -> {
                                markAgreed(request);
                                loadMyRequestsData();
                            });
                            setGraphic(btnAgree);
                            break;
                        // AGREED -  Meet up details are set , waiting for both parties to confirm the trade was completed.
                        case "AGREED":
                            if (request.isRequesterConfirmed()) {
                                // Requester already confirmed , show waiting message until the benefactor confirms.
                                lblWaiting.setStyle("-fx-text-fill: #757575; -fx-font-size: 11px;");
                                setGraphic(lblWaiting);
                            } else {
                                // Requester has not confirmed yet, all them to mark as completed.
                                btnSatisfied.setStyle("-fx-background-color: #1565c0; " +
                                        "-fx-text-fill: white; -fx-font-weight: bold; " +
                                        "-fx-background-radius: 6; -fx-cursor: hand;");
                                btnSatisfied.setOnAction(e -> {
                                    markRequesterSatisfied(request);
                                    loadMyRequestsData();
                                    loadClaimedTradesData();
                                });
                                setGraphic(btnSatisfied);
                            }
                            break;
                        // SATISFIED -  both parties confirmed , trade is fully complete.
                        case "SATISFIED":
                            Label lblDone = new Label("Complete");
                            lblDone.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
                            setGraphic(lblDone);
                            break;

                        default:
                            setGraphic(null);
                            break;
                    }
                }
            });

            tblMyRequests.setItems(trades);

        } catch (SQLException e) {
            showPostStatus("Could not load your requests.", false);
            logger.severe("Error loading my requests: " + e.getMessage());
        }
    }

    private void loadClaimedTradesData() {
        ObservableList<TradeRequest> claimed = FXCollections.observableArrayList();

        try {
            String query = "SELECT r.requestid, r.item_name, r.category, r.urgency, " +
                    "r.status, r.location, r.swap_time, r.expires_at, " +
                    "r.requester_confirmed, r.benefactor_confirmed, " +
                    "CONCAT(u.first_name, ' ', u.last_name) AS requested_by " +
                    "FROM tblrequest r " +
                    "JOIN tblusers u ON r.requester_id = u.user_id " +
                    "WHERE r.benefactor_id = ? " +
                    "ORDER BY r.claimed_at DESC";

            PreparedStatement ps = dc.con.prepareStatement(query);
            ps.setInt(1, CurrentLogin.getUserId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                claimed.add(new TradeRequest(
                        rs.getInt("requestid"),
                        rs.getString("item_name"),
                        rs.getString("category"),
                        rs.getString("urgency"),
                        rs.getString("status"),
                        "",
                        rs.getString("requested_by"),
                        rs.getString("location") != null ? rs.getString("location") : "Not set",
                        rs.getString("swap_time") != null ? rs.getString("swap_time") : "Not set",
                        rs.getString("expires_at"),
                        rs.getBoolean("requester_confirmed"),
                        rs.getBoolean("benefactor_confirmed")
                ));
            }

            colClaimedItemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
            colClaimedCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
            colClaimedRequestedBy.setCellValueFactory(new PropertyValueFactory<>("requestedBy"));
            colClaimedStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            colClaimedExpires.setCellValueFactory(new PropertyValueFactory<>("expiresAt"));

            colClaimedLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
            colClaimedLocation.setCellFactory(col -> new TableCell<TradeRequest, String>() {
                private final ComboBox<String> comboBox = new ComboBox<>(
                        FXCollections.observableArrayList("School Cafeteria", "Library")
                );

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) { setGraphic(null); return; }
                    TradeRequest request = getTableView().getItems().get(getIndex());
                    if (request.getStatus().equals("CLAIMED") || request.getStatus().equals("AGREED")) {
                        comboBox.setValue(item.equals("Not set") ? null : item);
                        comboBox.setPromptText("Select location");
                        comboBox.setOnAction(e -> updateLocation(request, comboBox.getValue()));
                        setGraphic(comboBox);
                    } else {
                        setText(item);
                        setGraphic(null);
                    }
                }
            });
            // Location column shows a dropdown for a ClAIMED or AGREED requests.
            colClaimedTime.setCellValueFactory(new PropertyValueFactory<>("swapTime"));
            colClaimedTime.setCellFactory(col -> new TableCell<TradeRequest, String>() {
                private final ComboBox<String> comboBox = new ComboBox<>(
                        FXCollections.observableArrayList(
                                "12:00 to 12:15", "12:15 to 12:30", "5:30 to 5:45"
                        )
                );

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    //Only show the dropdown while the trade is still active.
                    if (empty) { setGraphic(null); return; }
                    TradeRequest request = getTableView().getItems().get(getIndex());
                    if (request.getStatus().equals("CLAIMED") || request.getStatus().equals("AGREED")) {
                        comboBox.setValue(item.equals("Not set") ? null : item);
                        comboBox.setPromptText("Select time");
                        comboBox.setOnAction(e -> updateSwapTime(request, comboBox.getValue()));
                        setGraphic(comboBox);
                    } else {
                        setText(item);
                        setGraphic(null);
                    }
                }
            });

            //Column for claimed trades shows the benefactor's Satisfied button or a waiting message
            colClaimedAction.setCellFactory(col -> new TableCell<TradeRequest, String>() {
                private final Button btnSatisfied = new Button("🎉 Satisfied");
                private final Label  lblWaiting   = new Label("⏳ Waiting...");

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) { setGraphic(null); return; }

                    TradeRequest request = getTableView().getItems().get(getIndex());

                    switch (request.getStatus()) {
                        case "AGREED":
                            // If the benefactor has already confirmed, show a waiting message until the requester confirms .
                            if (request.isBenefactorConfirmed()) {
                                lblWaiting.setStyle("-fx-text-fill: #757575; -fx-font-size: 11px;");
                                setGraphic(lblWaiting);
                            } else {
                                // Benefactor has not confirmed yet, so show Satisfied button.
                                btnSatisfied.setStyle("-fx-background-color: #1565c0; " +
                                        "-fx-text-fill: white; -fx-font-weight: bold; " +
                                        "-fx-background-radius: 6; -fx-cursor: hand;");
                                btnSatisfied.setOnAction(e -> {
                                    markBenefactorSatisfied(request);
                                    loadClaimedTradesData();
                                    loadMyRequestsData();
                                });
                                setGraphic(btnSatisfied);
                            }
                            break;

                        case "SATISFIED":
                            // Trade is fully complete , display a label.
                            Label lblDone = new Label("Complete");
                            lblDone.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
                            setGraphic(lblDone);
                            break;

                        default:
                            setGraphic(null);
                            break;
                    }
                }
            });

            tblClaimedTrades.setItems(claimed);

        } catch (SQLException e) {
            showPostStatus("Could not load claimed trades.", false);
            logger.severe("Error loading claimed trades: " + e.getMessage());
        }
    }

    // Mark request as AGREED when requester confirms meetup.
    private void markAgreed(TradeRequest request) {
        try {
            String query = "UPDATE tblrequest SET status = 'AGREED' " +
                    "WHERE requestid = ?";
            PreparedStatement ps = dc.con.prepareStatement(query);
            ps.setInt(1, request.getRequestId());
            ps.executeUpdate();
            showPostStatus("You have agreed to the meetup details!", true);
        } catch (SQLException e) {
            showPostStatus("Could not update status.", false);
            logger.severe("Error marking agreed: " + e.getMessage());
        }
    }

    // Record that the requester is satisfied  and check both parties for confirmation.
    private void markRequesterSatisfied(TradeRequest request) {
        try {
            String query = "UPDATE tblrequest SET requester_confirmed = TRUE " +
                    "WHERE requestid = ?";
            PreparedStatement ps = dc.con.prepareStatement(query);
            ps.setInt(1, request.getRequestId());
            ps.executeUpdate();

            // Check if both confirmed
            checkAndCompleteTrade(request.getRequestId());
            showPostStatus("You confirmed the trade was completed!", true);

        } catch (SQLException e) {
            showPostStatus("Could not confirm trade.", false);
            logger.severe("Error confirming requester satisfied: " + e.getMessage());
        }
    }

    // Check if both parties have confirmed, mark the trade as satisfied and auto-update ownership.
    private void markBenefactorSatisfied(TradeRequest request) {
        try {
            String query = "UPDATE tblrequest SET benefactor_confirmed = TRUE " +
                    "WHERE requestid = ?";
            PreparedStatement ps = dc.con.prepareStatement(query);
            ps.setInt(1, request.getRequestId());
            ps.executeUpdate();
            checkAndCompleteTrade(request.getRequestId());
            showPostStatus("You confirmed the trade was completed!", true);

        } catch (SQLException e) {
            showPostStatus("Could not confirm trade.", false);
            logger.severe("Error confirming benefactor satisfied: " + e.getMessage());
        }
    }

    // Check if both parties confirmed — if so mark SATISFIED and auto-toggle ownership.
    private void checkAndCompleteTrade(int requestId) {
        try {
            // Check if both confirmed.
            String checkQuery = "SELECT requester_confirmed, benefactor_confirmed, " +
                    "requester_id, itemid " +
                    "FROM tblrequest r " +
                    "JOIN tblitems i ON r.item_name = i.item_name " +
                    "WHERE r.requestid = ?";
            PreparedStatement checkPs = dc.con.prepareStatement(checkQuery);
            checkPs.setInt(1, requestId);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                boolean requesterConfirmed  = rs.getBoolean("requester_confirmed");
                boolean benefactorConfirmed = rs.getBoolean("benefactor_confirmed");

                // Only Complete the trade if both parties have confirmed.
                if (requesterConfirmed && benefactorConfirmed) {
                    int requesterId = rs.getInt("requester_id");
                    int itemId      = rs.getInt("itemid");

                    // Mark trade as SATISFIED.
                    String satisfyQuery = "UPDATE tblrequest SET status = 'SATISFIED' " +
                            "WHERE requestid = ?";
                    PreparedStatement satisfyPs = dc.con.prepareStatement(satisfyQuery);
                    satisfyPs.setInt(1, requestId);
                    satisfyPs.executeUpdate();

                    // Auto-toggle ownership for requester.
                    autoToggleOwnership(requesterId, itemId);

                    showPostStatus("🎉 Trade completed! Item added to requester's supplies.", true);
                }
            }

        } catch (SQLException e) {
            logger.severe("Error completing trade: " + e.getMessage());
        }
    }

    // Update or insert a tblusersupplies record to mark the item as owned via TRADE.
    private void autoToggleOwnership(int userId, int itemId) {
        try {
            // Check if row already exists
            String checkQuery = "SELECT supplyid FROM tblusersupplies " +
                    "WHERE user_id = ? AND itemid = ?";
            PreparedStatement checkPs = dc.con.prepareStatement(checkQuery);
            checkPs.setInt(1, userId);
            checkPs.setInt(2, itemId);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                // If record exists, update the owned flag and mark the method as trade.
                String updateQuery = "UPDATE tblusersupplies " +
                        "SET owned = TRUE, acquired_via = 'TRADE' " +
                        "WHERE user_id = ? AND itemid = ?";
                PreparedStatement updatePs = dc.con.prepareStatement(updateQuery);
                updatePs.setInt(1, userId);
                updatePs.setInt(2, itemId);
                updatePs.executeUpdate();
            } else {
                // No record exists yet so insert new row , and mark the method as trade.
                String insertQuery = "INSERT INTO tblusersupplies " +
                        "(user_id, itemid, owned, acquired_via) " +
                        "VALUES (?, ?, TRUE, 'TRADE')";
                PreparedStatement insertPs = dc.con.prepareStatement(insertQuery);
                insertPs.setInt(1, userId);
                insertPs.setInt(2, itemId);
                insertPs.executeUpdate();
            }

        } catch (SQLException e) {
            logger.severe("Error auto-toggling ownership: " + e.getMessage());
        }
    }

    // Save the selected meeting location for a claimed request to the database.
    private void updateLocation(TradeRequest request, String location) {
        if (location == null) return;
        try {
            String query = "UPDATE tblrequest SET location = ? WHERE requestid = ?";
            PreparedStatement ps = dc.con.prepareStatement(query);
            ps.setString(1, location);
            ps.setInt(2, request.getRequestId());
            ps.executeUpdate();
            showPostStatus("Location updated to: " + location, true);
        } catch (SQLException e) {
            showPostStatus("Could not update location.", false);
            logger.severe("Error updating location: " + e.getMessage());
        }
    }

    // Update swap time
    private void updateSwapTime(TradeRequest request, String swapTime) {
        if (swapTime == null) return;
        try {
            String query = "UPDATE tblrequest SET swap_time = ? WHERE requestid = ?";
            PreparedStatement ps = dc.con.prepareStatement(query);
            ps.setString(1, swapTime);
            ps.setInt(2, request.getRequestId());
            ps.executeUpdate();
            showPostStatus("Swap time updated to: " + swapTime, true);
        } catch (SQLException e) {
            showPostStatus("Could not update swap time.", false);
            logger.severe("Error updating swap time: " + e.getMessage());
        }
    }
    // Delete expired requests
    private void deleteExpiredRequests() {
        try {
            dc.stat.executeUpdate(
                    "DELETE FROM tblrequest WHERE expires_at < NOW() AND status = 'OPEN'"
            );
            dc.stat.executeUpdate(
                    "UPDATE tblrequest SET expires_at = DATE_ADD(claimed_at, INTERVAL 24 HOUR) " +
                            "WHERE status = 'CLAIMED' AND claimed_at IS NOT NULL"
            );
        } catch (SQLException e) {
            logger.severe("Error managing expired requests: " + e.getMessage());
        }
    }

    // Get how many active requests the user has
    private int getUserRequestCount() {
        try {
            String query = "SELECT COUNT(*) FROM tblrequest WHERE requester_id = ?";
            PreparedStatement ps = dc.con.prepareStatement(query);
            ps.setInt(1, CurrentLogin.getUserId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            logger.severe("Error counting requests: " + e.getMessage());
        }
        return 0;
    }

    // Check request limit and show the limit warning at 3 active requests
    private void checkRequestLimit() {
        int count = getUserRequestCount();
        btnPostRequest.setDisable(count >= 3);
        lblRequestLimit.setVisible(count >= 3);
    }
    // Display a status message in green for success or red for failure
    private void showPostStatus(String message, boolean success) {
        lblPostStatus.setText(message);
        lblPostStatus.setStyle(success
                ? "-fx-font-size: 12px; -fx-text-fill: #2e7d32;"
                : "-fx-font-size: 12px; -fx-text-fill: #c62828;");
        lblPostStatus.setVisible(true);
    }
    // Reset the post request form fields.
    private void clearPostForm() {
        cmbCategory.setValue(null);
        cmbItemName.setValue(null);
        cmbUrgency.setValue(null);
    }
}