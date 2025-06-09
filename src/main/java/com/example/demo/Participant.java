package com.example.demo;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Participant {

    @Id
    private String participantId;
    private String name;
    private String assignedGifteeId;
    private String status;
    private String selectedGiftId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "participant", orphanRemoval = true)
    private List<GiftRequest> giftRequests;

    public Participant() {
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAssignedGifteeId() {
        return assignedGifteeId;
    }

    public void setAssignedGifteeId(String assignedGifteeId) {
        this.assignedGifteeId = assignedGifteeId;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public List<GiftRequest> getGiftRequests() {
        return giftRequests;
    }

    public void setGiftRequests(List<GiftRequest> giftRequests) {
        this.giftRequests = giftRequests;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSelectedGiftId() {
        return selectedGiftId;
    }

    public void setSelectedGiftId(String selectedGiftId) {
        this.selectedGiftId = selectedGiftId;
    }
}