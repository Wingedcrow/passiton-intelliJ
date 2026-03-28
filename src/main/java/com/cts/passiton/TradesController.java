package com.cts.passiton;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class TradesController {

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
    @FXML private Button btnDeleteRequest;


    @FXML private TableView<TradeRequest> tblClaimedTrades;
    @FXML private TableColumn<TradeRequest, String> colClaimedItemName;
    @FXML private TableColumn<TradeRequest, String> colClaimedCategory;
    @FXML private TableColumn<TradeRequest, String> colClaimedRequestedBy;
    @FXML private TableColumn<TradeRequest, String> colClaimedStatus;
    @FXML private TableColumn<TradeRequest, String> colClaimedLocation;
    @FXML private TableColumn<TradeRequest, String> colClaimedTime;
    @FXML private TableColumn<TradeRequest, String> colClaimedExpires;

    @FXML private Button btnBack;

    DatabaseConnection dc = new DatabaseConnection();

    @FXML
    public void initialize() {
        cmbCategory.setItems(FXCollections.observableArrayList(
                "Books", "Tools", "Computer Components"
        ));
        cmbUrgency.setItems(FXCollections.observableArrayList(
                "LOW", "MEDIUM", "HIGH"
        ));

        cmbCategory.setOnAction(e -> loadItemsForCategory());
        deleteExpiredRequests();
        loadMyRequestsData();
        loadClaimedTradesData();
        checkRequestLimit();
    }

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

            showPostStatus("✅ Request posted successfully!", true);
            clearPostForm();
            loadMyRequestsData();
            checkRequestLimit();

        } catch (SQLException e) {
            showPostStatus("Database error. Could not post request.", false);
            logger.severe("Error posting request: " + e.getMessage());
        }
    }


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

    @FXML
    protected void actionBack() {
        try {
            JavaFxDemoApp app = new JavaFxDemoApp();
            app.changeScene("student-dashboard-view.fxml", 1100, 750);
        } catch (IOException e) {
            logger.severe("Error loading dashboard: " + e.getMessage());
        }
    }


    private void loadMyRequestsData() {
        ObservableList<TradeRequest> trades = FXCollections.observableArrayList();

        try {
            String query = "SELECT r.requestid, r.item_name, r.category, r.urgency, " +
                    "r.status, r.location, r.swap_time, r.expires_at, " +
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
                        rs.getString("expires_at")
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
                        rs.getString("expires_at")
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
                    comboBox.setValue(item.equals("Not set") ? null : item);
                    comboBox.setPromptText("Select location");
                    comboBox.setOnAction(e -> updateLocation(request, comboBox.getValue()));
                    setGraphic(comboBox);
                }
            });

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
                    if (empty) { setGraphic(null); return; }
                    TradeRequest request = getTableView().getItems().get(getIndex());
                    comboBox.setValue(item.equals("Not set") ? null : item);
                    comboBox.setPromptText("Select time");
                    comboBox.setOnAction(e -> updateSwapTime(request, comboBox.getValue()));
                    setGraphic(comboBox);
                }
            });

            tblClaimedTrades.setItems(claimed);

        } catch (SQLException e) {
            showPostStatus("Could not load claimed trades.", false);
            logger.severe("Error loading claimed trades: " + e.getMessage());
        }
    }

    private void updateLocation(TradeRequest request, String location) {
        if (location == null) return;
        try {
            String query = "UPDATE tblrequest SET location = ? WHERE requestid = ?";
            PreparedStatement ps = dc.con.prepareStatement(query);
            ps.setString(1, location);
            ps.setInt(2, request.getRequestId());
            ps.executeUpdate();
            showPostStatus("✅ Location updated to: " + location, true);
        } catch (SQLException e) {
            showPostStatus("Could not update location.", false);
            logger.severe("Error updating location: " + e.getMessage());
        }
    }


    private void updateSwapTime(TradeRequest request, String swapTime) {
        if (swapTime == null) return;
        try {
            String query = "UPDATE tblrequest SET swap_time = ? WHERE requestid = ?";
            PreparedStatement ps = dc.con.prepareStatement(query);
            ps.setString(1, swapTime);
            ps.setInt(2, request.getRequestId());
            ps.executeUpdate();
            showPostStatus("✅ Swap time updated to: " + swapTime, true);
        } catch (SQLException e) {
            showPostStatus("Could not update swap time.", false);
            logger.severe("Error updating swap time: " + e.getMessage());
        }
    }


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


    private void checkRequestLimit() {
        int count = getUserRequestCount();
        btnPostRequest.setDisable(count >= 3);
        lblRequestLimit.setVisible(count >= 3);
    }

    private void showPostStatus(String message, boolean success) {
        lblPostStatus.setText(message);
        lblPostStatus.setStyle(success
                ? "-fx-font-size: 12px; -fx-text-fill: #2e7d32;"
                : "-fx-font-size: 12px; -fx-text-fill: #c62828;");
        lblPostStatus.setVisible(true);
    }

    private void clearPostForm() {
        cmbCategory.setValue(null);
        cmbItemName.setValue(null);
        cmbUrgency.setValue(null);
    }
}