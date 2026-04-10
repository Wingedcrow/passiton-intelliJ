package com.cts.passiton;

/**
 * TradeRequest.java
 * This class represents a single trade request as used in the TradesController.
 * It holds the details of a request , urgency,current status and swap arrangement info.
 * The claimBy field stores the name of the student who has offered to fulfil the request
 * The requestedBy stores the name of the original poster, used when viewing trades the logged
 * in student has claimed.
 * The requesterConfirmed and benefactorConfirmed fields are there to support the feature 2 person
 * request completion system.
 * Both parties must mark a trade as satisfied before ownership is automatically updated
 * in the student's supplies
 *
 * @author Joshua Howard & Bradley Balram
 * @version 1.0
 * @date (09/04/2026)
 */
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
    private boolean requesterConfirmed;
    private boolean benefactorConfirmed;

    public TradeRequest(int requestId, String itemName, String category,
                        String urgency, String status, String claimedBy,
                        String requestedBy, String location,
                        String swapTime, String expiresAt,
                        boolean requesterConfirmed, boolean benefactorConfirmed) {
        this.requestId            = requestId;
        this.itemName             = itemName;
        this.category             = category;
        this.urgency              = urgency;
        this.status               = status;
        this.claimedBy            = claimedBy;
        this.requestedBy          = requestedBy;
        this.location             = location;
        this.swapTime             = swapTime;
        this.expiresAt            = expiresAt;
        this.requesterConfirmed   = requesterConfirmed;
        this.benefactorConfirmed  = benefactorConfirmed;
    }

    public int getRequestId()             { return requestId; }
    public String getItemName()           { return itemName; }
    public String getCategory()           { return category; }
    public String getUrgency()            { return urgency; }
    public String getStatus()             { return status; }
    public String getClaimedBy()          { return claimedBy; }
    public String getRequestedBy()        { return requestedBy; }
    public String getLocation()           { return location; }
    public String getSwapTime()           { return swapTime; }
    public String getExpiresAt()          { return expiresAt; }
    public boolean isRequesterConfirmed() { return requesterConfirmed; }
    public boolean isBenefactorConfirmed(){ return benefactorConfirmed; }
}