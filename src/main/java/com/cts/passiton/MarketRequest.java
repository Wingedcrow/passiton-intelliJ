package com.cts.passiton;

/**
 * MarketRequest.java
 * This represents a single trade request as it appears on the marketboard
 * Each object holds the details of a request posted by a student
 * and functions with MarketboardController to prevent students from claiming their own request
 *
 * @author Joshua Howard & Bradley Balram
 * @version 1.0
 * @date (08/04/2026)
 */

public class MarketRequest {

    private int requestId;
    private int requesterId;
    private String itemName;
    private String category;
    private String postedBy;
    private String datePosted;
    private String status;

    public MarketRequest(int requestId, int requesterId, String itemName, String category,
                         String postedBy, String datePosted, String status) {
        this.requestId   = requestId;
        this.requesterId = requesterId;
        this.itemName    = itemName;
        this.category    = category;
        this.postedBy    = postedBy;
        this.datePosted  = datePosted;
        this.status      = status;
    }

    public int getRequestId()     { return requestId; }
    public int getRequesterId()   { return requesterId; }
    public String getItemName()   { return itemName; }
    public String getCategory()   { return category; }
    public String getPostedBy()   { return postedBy; }
    public String getDatePosted() { return datePosted; }
    public String getStatus()     { return status; }
}