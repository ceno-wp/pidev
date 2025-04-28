package tn.esprit.models;

import java.time.LocalDateTime;

public class Claim {
    private int id;
    private int caseId;
    private int lawyerId;
    private int clientId;
    private String status;
    private LocalDateTime createdAt;

    public Claim(int caseId, int lawyerId, int clientId) {
        this.caseId = caseId;
        this.lawyerId = lawyerId;
        this.clientId = clientId;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    public int getLawyerId() {
        return lawyerId;
    }

    public void setLawyerId(int lawyerId) {
        this.lawyerId = lawyerId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCaseId() {
        return caseId;
    }

    public void setCaseId(int caseId) {
        this.caseId = caseId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}