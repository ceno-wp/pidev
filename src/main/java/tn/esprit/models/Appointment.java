package tn.esprit.models;

import tn.esprit.utils.MailService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Appointment {
    private int id;
    private LocalDateTime date;
    private String time_period;
    private String description;
    private int clientId;
    private int lawyerId;
    private LocalDateTime createdAt;

    // Constructors
    public Appointment() {}

    public Appointment(int id, LocalDateTime date, String time_period, String description,
                       int clientId, int lawyerId, LocalDateTime createdAt) {
        this.id = id;
        this.date = date;
        this.time_period = time_period;
        this.description = description;
        this.clientId = clientId;
        this.lawyerId = lawyerId;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public String getTime_period() { return time_period; }
    public void setTime_period(String time_period) { this.time_period = time_period; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }

    public int getLawyerId() { return lawyerId; }
    public void setLawyerId(int lawyerId) { this.lawyerId = lawyerId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // For displaying in ListView or console
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "🔖 Appointment ID: " + id
                + " | 📅 " + date.format(formatter)
                + " | Time_period: " + time_period
                + "\n📝 " + description;
    }

}