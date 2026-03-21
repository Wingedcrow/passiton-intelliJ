package com.cts.passiton;

public class MarketRequest {

    private int requestId;
    private String itemName;
    private String category;
    private String postedBy;
    private String datePosted;
    private String status;

    public MarketRequest(int requestId, String itemName, String category,
                         String postedBy, String datePosted, String status) {
        this.requestId = requestId;
        this.itemName = itemName;
        this.category = category;
        this.postedBy = postedBy;
        this.datePosted = datePosted;
        this.status = status;
    }

    // Getters
    public int getRequestId()     { return requestId; }
    public String getItemName()   { return itemName; }
    public String getCategory()   { return category; }
    public String getPostedBy()   { return postedBy; }
    public String getDatePosted() { return datePosted; }
    public String getStatus()     { return status; }
}