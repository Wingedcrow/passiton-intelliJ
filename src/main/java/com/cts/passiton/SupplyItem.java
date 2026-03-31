package com.cts.passiton;

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

    public void setOwned(boolean owned) { this.owned = owned; }

    public String getAcquiredVia() { return acquiredVia; }
}