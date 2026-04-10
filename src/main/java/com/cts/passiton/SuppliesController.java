package com.cts.passiton;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;

/**
 * SuppliesController.java
 * This controller manages the supplies dashboard screen, which displays all active items available
 * in the school supplies catalogue.
 * Each item shows whether the logged-in student currently owns it .
 * Students can filter the list by category,status, or by typing in the search box.
 * Ownership can be toggled from a button on each row, which updates the record in the database immediately.
 * Note for partner : please read up on factory method pattern, lambda expressions,
 * SQL Server coalesce and SQL leftjoin
 *
 * @author Joshua Howard & Bradley Balram
 * @version 1.0
 * @date (08/04/2026)
 */

public class SuppliesController implements Initializable {

    //Create a new database connection
    static DatabaseConnection dc = new DatabaseConnection();

    @FXML
    private TextField txtSearch;

    @FXML
    private RadioButton rbAllCategories;

    @FXML
    private RadioButton rbBooks;

    @FXML
    private RadioButton rbTools;

    @FXML
    private RadioButton rbComponents;

    @FXML
    private RadioButton rbAllOwnership;

    @FXML
    private RadioButton rbOwned;

    @FXML
    private RadioButton rbUnowned;

    @FXML
    private Button btnFilter;

    @FXML
    private Button btnClearFilters;

    @FXML
    private Button btnBack;

    @FXML
    private TableView<SupplyItem> tblSupplies;

    @FXML
    private TableColumn<SupplyItem, String> colName;

    @FXML
    private TableColumn<SupplyItem, String> colCategory;

    @FXML
    private TableColumn<SupplyItem, String> colOwned;

    @FXML
    private TableColumn<SupplyItem, String> colAction;

    @FXML
    private Label lblStatus;

    @FXML
    private Label lblItemCount;

    //Holds the full list of supplies loaded from the database.
    ObservableList<SupplyItem> listS;

    //Populate table columns and load supply data
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Group the category radio button so only one can be selected at a time
        ToggleGroup categoryGroup = new ToggleGroup();
        rbAllCategories.setToggleGroup(categoryGroup);
        rbBooks.setToggleGroup(categoryGroup);
        rbTools.setToggleGroup(categoryGroup);
        rbComponents.setToggleGroup(categoryGroup);

        //Group the ownership radio button so only one can be selected at a time
        ToggleGroup ownershipGroup = new ToggleGroup();
        rbAllOwnership.setToggleGroup(ownershipGroup);
        rbOwned.setToggleGroup(ownershipGroup);
        rbUnowned.setToggleGroup(ownershipGroup);

        //Wire the item name and category columns using the matching field names in SupplyItem
        colName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));

        //Owned column converts the boolean owned value into a readable string for display
        colOwned.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().isOwned() ? "Owned" : "Not Owned"));
        colOwned.setCellFactory(col -> new TableCell<SupplyItem, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle(item.equals("Owned")
                        ? "-fx-text-fill: #2e7d32; -fx-font-weight: bold;"
                        : "-fx-text-fill: #c62828;");
            }
        });

        //Action column shows Own/Remove button depending on current status
        colAction.setCellValueFactory(data -> new SimpleStringProperty(""));
        colAction.setCellFactory(col -> new TableCell<SupplyItem, String>() {
            private final Button btn = new Button();

            {
                //  Wire the button click once when the cell is created rather than each time it is rendered
                btn.setOnAction(e -> {
                    int idx = getIndex();
                    if (idx < 0 || idx >= getTableView().getItems().size()) return;
                    SupplyItem supply = getTableView().getItems().get(idx);
                    toggleOwnership(supply);
                    styleButton(supply.isOwned());
                    getTableView().refresh();
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                    return;
                }
                // These are ensuring the button colour matches the current status.
                styleButton(getTableView().getItems().get(getIndex()).isOwned());
                setGraphic(btn);
            }

            private void styleButton(boolean owned) {
                if (owned) {
                    btn.setText("Remove");
                    btn.setStyle("-fx-background-color: #ef9a9a; -fx-text-fill: #c62828; " +
                            "-fx-font-weight: bold; -fx-background-radius: 12;");
                } else {
                    btn.setText("Own");
                    btn.setStyle("-fx-background-color: #c5e1a5; -fx-text-fill: #33691e; " +
                            "-fx-font-weight: bold; -fx-background-radius: 12;");
                }
            }
        });

        //Load all supplies from the database
        listS = getDataSupplies();
        tblSupplies.setItems(listS);
        updateItemCount(listS.size());

        //Filter list as user types in the search box
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    //Query the database for all active supply items and the ownership records of current student
    public static ObservableList<SupplyItem> getDataSupplies() {

        ObservableList<SupplyItem> list = FXCollections.observableArrayList();

        // LEFT JOIN ensures all items are displayed on the screen , even if the student has
        // never interacted with it or owned it before.
        // COALESCE returns false for owned and manual for acquired_via when no matching row exists in tblusersupplies.
        // I am using this to ensure UI consistency as without it, the application would display
        // null values or blank spaces for unowned items.
        try {
            String query = "SELECT i.itemid, i.item_name, i.category, " +
                    "COALESCE(s.owned, FALSE) AS owned, " +
                    "COALESCE(s.acquired_via, 'MANUAL') AS acquired_via " +
                    "FROM tblitems i " +
                    "LEFT JOIN tblusersupplies s " +
                    "ON i.itemid = s.itemid AND s.user_id = ? " +
                    "WHERE i.is_active = TRUE " +
                    "ORDER BY i.category, i.item_name";

            dc.ps = dc.con.prepareStatement(query);
            dc.ps.setInt(1, CurrentLogin.getUserId());
            dc.rst = dc.ps.executeQuery();

            while (dc.rst.next()) {
                list.add(new SupplyItem(
                        dc.rst.getInt("itemid"),
                        dc.rst.getString("item_name"),
                        dc.rst.getString("category"),
                        dc.rst.getBoolean("owned"),
                        dc.rst.getString("acquired_via")
                ));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    //Apply category, ownership and search filters to the supplies list
    @FXML
    public void applyFilters() {
        String searchText = txtSearch.getText().trim().toLowerCase();

        String category = null;
        if (rbBooks.isSelected())      category = "Books";
        if (rbTools.isSelected())      category = "Tools";
        if (rbComponents.isSelected()) category = "Computer Components";

        Boolean ownedFilter = null;
        if (rbOwned.isSelected())   ownedFilter = true;
        if (rbUnowned.isSelected()) ownedFilter = false;

        final String finalCategory = category;
        final Boolean finalOwned   = ownedFilter;

        ObservableList<SupplyItem> filtered = FXCollections.observableArrayList();

        // Checks each item in the list against all active filters
        for (SupplyItem item : listS) {
            boolean matchesSearch   = searchText.isEmpty() ||
                    item.getItemName().toLowerCase().contains(searchText);
            boolean matchesCategory = finalCategory == null ||
                    item.getCategory().equals(finalCategory);
            boolean matchesOwned    = finalOwned == null ||
                    item.isOwned() == finalOwned;

            if (matchesSearch && matchesCategory && matchesOwned) {
                filtered.add(item);
            }
        }

        tblSupplies.setItems(filtered);
        updateItemCount(filtered.size());
    }

    //Clear all filters and restore the full list
    @FXML
    public void clearFilters() {
        txtSearch.clear();
        rbAllCategories.setSelected(true);
        rbAllOwnership.setSelected(true);
        tblSupplies.setItems(listS);
        updateItemCount(listS.size());
    }

    //Toggle the owned status of a supply item and update the database
    private void toggleOwnership(SupplyItem supply) {
        boolean newOwned = !supply.isOwned();

        try {
            // Check if an ownership record already exists for the student and item
            dc.ps = dc.con.prepareStatement(
                    "SELECT supplyid FROM tblusersupplies WHERE user_id = ? AND itemid = ?");
            dc.ps.setInt(1, CurrentLogin.getUserId());
            dc.ps.setInt(2, supply.getItemId());
            ResultSet rs = dc.ps.executeQuery();

            if (rs.next()) {
                // Record exists, update the owned flag
                dc.ps = dc.con.prepareStatement(
                        "UPDATE tblusersupplies SET owned = ? WHERE user_id = ? AND itemid = ?");
                dc.ps.setBoolean(1, newOwned);
                dc.ps.setInt(2, CurrentLogin.getUserId());
                dc.ps.setInt(3, supply.getItemId());
                dc.ps.executeUpdate();
            } else {
                //No record found, so insert a new one
                dc.ps = dc.con.prepareStatement(
                        "INSERT INTO tblusersupplies (user_id, itemid, owned, acquired_via) VALUES (?, ?, ?, 'MANUAL')");
                dc.ps.setInt(1, CurrentLogin.getUserId());
                dc.ps.setInt(2, supply.getItemId());
                dc.ps.setBoolean(3, newOwned);
                dc.ps.executeUpdate();
            }
            // Updates the item in memory so the table reflects the change without reloading
            //from the database
            supply.setOwned(newOwned);
            showStatus(newOwned
                    ? supply.getItemName() + " marked as owned!"
                    : supply.getItemName() + " marked as not owned.", newOwned);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //Return to the Student Dashboard
    @FXML
    public void actionBack() throws IOException {
        PassItOnApp app = new PassItOnApp();
        app.changeScene("student-dashboard-view.fxml", 1100, 750);
    }

    //Show a status message below the table
    private void showStatus(String message, boolean success) {
        lblStatus.setText(message);
        lblStatus.setStyle(success
                ? "-fx-font-size: 12px; -fx-text-fill: #2e7d32;"
                : "-fx-font-size: 12px; -fx-text-fill: #c62828;");
        lblStatus.setVisible(true);
    }

    //Update the item count label to reflect how many items are currently visible in the table
    private void updateItemCount(int count) {
        lblItemCount.setText("Showing " + count + " item" + (count != 1 ? "s" : ""));
    }
}