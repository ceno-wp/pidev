package tn.esprit.test;

import tn.esprit.models.Appointment;
import tn.esprit.services.ServiceAppointment;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {

        ServiceAppointment serviceAppointment = new ServiceAppointment();

        // Create a new Appointment object
        Appointment newAppointment = new Appointment();
        newAppointment.setDate(LocalDateTime.of(2025, 4, 15, 10, 30)); // Set a sample date
        newAppointment.setStatus("Scheduled"); // Appointment status
        newAppointment.setDescription("First consultation with the client."); // Description
        newAppointment.setClientId(1); // Assuming client ID 1 exists in the database
        newAppointment.setLawyerId(1); // Assuming lawyer ID 1 exists in the database
        newAppointment.setCreatedAt(LocalDateTime.now()); // Set current time for creation


        serviceAppointment.add(newAppointment);
        System.out.println("✅ Appointment added.");


        System.out.println("📋 All Appointments:");
        serviceAppointment.getAll().forEach(appointment -> {
            System.out.println("ID: " + appointment.getId());
            System.out.println("Date: " + appointment.getDate());
            System.out.println("Status: " + appointment.getStatus());
            System.out.println("Description: " + appointment.getDescription());
            System.out.println("Client ID: " + appointment.getClientId());
            System.out.println("Lawyer ID: " + appointment.getLawyerId());
            System.out.println("Created At: " + appointment.getCreatedAt());
            System.out.println("-----------------------------");
        });
    }
}
