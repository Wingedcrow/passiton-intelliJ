package com.cts.passiton;

public class TradeRequest {

    private int requestId;
    private String itemName;
    private String category;
    private String urgency;
    private String status;
    private String claimedBy;
    private String requestedBy;
    private String location;
    private String swapTime;
    private String expiresAt;

    public TradeRequest(int requestId, String itemName, String category,
                        String urgency, String status, String claimedBy,
                        String requestedBy, String location,
                        String swapTime, String expiresAt) {
        this.requestId   = requestId;
        this.itemName    = itemName;
        this.category    = category;
        this.urgency     = urgency;
        this.status      = status;
        this.claimedBy   = claimedBy;
        this.requestedBy = requestedBy;
        this.location    = location;
        this.swapTime    = swapTime;
        this.expiresAt   = expiresAt;
    }

    public int getRequestId()      { return requestId; }
    public String getItemName()    { return itemName; }
    public String getCategory()    { return category; }
    public String getUrgency()     { return urgency; }
    public String getStatus()      { return status; }
    public String getClaimedBy()   { return claimedBy; }
    public String getRequestedBy() { return requestedBy; }
    public String getLocation()    { return location; }
    public String getSwapTime()    { return swapTime; }
    public String getExpiresAt()   { return expiresAt; }
}