package com.example.demo;

import jakarta.persistence.*;

@Entity
public class GiftRequest {

    @Id
    private String requestId;
    private String giftName;
    private String photoUrl;
    private double approxBudget;
    private String notes;
    private String deliveryLocationTime;
    private boolean isClaimed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id")
    private Participant participant;

    public GiftRequest() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public double getApproxBudget() {
        return approxBudget;
    }

    public void setApproxBudget(double approxBudget) {
        this.approxBudget = approxBudget;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDeliveryLocationTime() {
        return deliveryLocationTime;
    }

    public void setDeliveryLocationTime(String deliveryLocationTime) {
        this.deliveryLocationTime = deliveryLocationTime;
    }

    public boolean isClaimed() {
        return isClaimed;
    }

    public void setClaimed(boolean claimed) {
        isClaimed = claimed;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }
}