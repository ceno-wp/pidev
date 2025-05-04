package tn.esprit.models;

import javafx.beans.property.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {
    private IntegerProperty paymentId;
    private IntegerProperty appointmentId;
    private StringProperty paymentStatus;
    private StringProperty paymentMethod;
    private ObjectProperty<LocalDateTime> paymentDate;
    private DoubleProperty amount;

    // Constructor to initialize properties
    public Payment() {
        this.paymentId = new SimpleIntegerProperty();
        this.appointmentId = new SimpleIntegerProperty();
        this.paymentStatus = new SimpleStringProperty();
        this.paymentMethod = new SimpleStringProperty();
        this.paymentDate = new SimpleObjectProperty<>();
        this.amount = new SimpleDoubleProperty();
    }

    // Getters and setters
    public int getPaymentId() {
        return paymentId.get();
    }

    public void setPaymentId(int paymentId) {
        this.paymentId.set(paymentId);
    }

    public IntegerProperty paymentIdProperty() {
        return paymentId;
    }

    public int getAppointmentId() {
        return appointmentId.get();
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId.set(appointmentId);
    }

    public IntegerProperty appointmentIdProperty() {
        return appointmentId;
    }

    public String getPaymentStatus() {
        return paymentStatus.get();
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus.set(paymentStatus);
    }

    public StringProperty paymentStatusProperty() {
        return paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod.get();
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod.set(paymentMethod);
    }

    public StringProperty paymentMethodProperty() {
        return paymentMethod;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate.get();
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate.set(paymentDate);
    }

    public ObjectProperty<LocalDateTime> paymentDateProperty() {
        return paymentDate;
    }

    public BigDecimal getAmount() {
        return BigDecimal.valueOf(amount.get());  // Convert double to BigDecimal
    }

    public void setAmount(BigDecimal amount) {
        this.amount.set(amount.doubleValue());  // Convert BigDecimal to double
    }

    public DoubleProperty amountProperty() {
        return amount;
    }
}
