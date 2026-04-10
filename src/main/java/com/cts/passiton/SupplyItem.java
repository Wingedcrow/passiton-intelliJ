package com.cts.passiton;

/**
 * SupplyItem.java
 * This class handles the data for items shown on the supplies dashboard.
 * dashboard. Each object holds the item's details as retrieved from tblitems,
 * joined with the logged-in student's ownership record from tblusersupplies.
 * The owned field was made changeable so the SuppliesController can update the status
 * in the app's memory as the student toggles it. This results in the screen updating instantly
 * without needing to wait for the database to reload.
 *
 * @author Joshua Howard & Bradley Balram
 * @version 1.0
 * @date (08/04/2026)
 */

public class SupplyItem {

    private int itemId;
    private String itemName;
    private String category;
    private boolean owned;
    private String acquiredVia;

    public SupplyItem(int itemId, String itemName, String category, boolean owned, String acquiredVia) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.category = category;
        this.owned = owned;
        this.acquiredVia = acquiredVia;
    }

    public int getItemId() { return itemId; }

    public String getItemName() { return itemName; }

    public String getCategory() { return category; }

    public boolean isOwned() { return owned; }

    // This is a setter, and makes the status changeable
    public void setOwned(boolean owned) { this.owned = owned; }

    // currently not used but was considered to support a trade log on the item.
    public String getAcquiredVia() { return acquiredVia; }
}