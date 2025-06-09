package com.example.demo;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList; // Ensure this is imported
import java.util.Map;
import java.util.HashMap;

@Entity
@Table(name = "secret_santa_group") // Use a different table name to avoid conflicts with SQL keyword 'group'
public class Group {

    @Id
    private String groupId;
    private String groupName;
    private int maxParticipants;
    private double minBudget;
    private double maxBudget;
    private LocalDateTime exchangeDate;
    private LocalDateTime revealDate;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "group", orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "group_assignments", joinColumns = @JoinColumn(name = "group_id"))
    @MapKeyColumn(name = "gifter_id") // This must match your table column
    @Column(name = "giftee_id")
    private Map<String, String> assignments = new HashMap<>(); // Use String IDs for map keys/values

    private boolean assignmentsDone = false;

    private String adminPassword;
    private java.time.LocalDateTime assignGiftDate;

    // Constructors
    public Group() {
    }

    // Getters and Setters
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public double getMinBudget() {
        return minBudget;
    }

    public void setMinBudget(double minBudget) {
        this.minBudget = minBudget;
    }

    public double getMaxBudget() {
        return maxBudget;
    }

    public void setMaxBudget(double maxBudget) {
        this.maxBudget = maxBudget;
    }

    public LocalDateTime getExchangeDate() {
        return exchangeDate;
    }

    public void setExchangeDate(LocalDateTime exchangeDate) {
        this.exchangeDate = exchangeDate;
    }

    public LocalDateTime getRevealDate() {
        return revealDate;
    }

    public void setRevealDate(LocalDateTime revealDate) {
        this.revealDate = revealDate;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public Map<String, String> getAssignments() {
        return assignments;
    }

    public void setAssignments(Map<String, String> assignments) {
        this.assignments = assignments;
    }

    public boolean isAssignmentsDone() {
        return assignmentsDone;
    }

    public void setAssignmentsDone(boolean assignmentsDone) {
        this.assignmentsDone = assignmentsDone;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public java.time.LocalDateTime getAssignGiftDate() {
        return assignGiftDate;
    }

    public void setAssignGiftDate(java.time.LocalDateTime assignGiftDate) {
        this.assignGiftDate = assignGiftDate;
    }

    // Helper method to add a participant
    public void addParticipant(Participant participant) {
        if (this.participants == null) {
            this.participants = new ArrayList<>();
        }
        this.participants.add(participant);
        participant.setGroup(this); // Set the many-to-one relationship
    }
}
