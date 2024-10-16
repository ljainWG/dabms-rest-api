package com.wg.dabms.prescription;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wg.dabms.appointment.Appointment;
import com.wg.dabms.appointment.AppointmentRepository;
import com.wg.dabms.exceptions.AppointmentNotFoundException;
import com.wg.dabms.exceptions.PrescriptionNotFoundException;
import com.wg.dabms.exceptions.UserNotFoundException;
import com.wg.dabms.user.UserRepository;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private AppointmentRepository appointmentRepository; // to check doctorId and patientId
    
    @Autowired
    private UserRepository userRepository;

    // Method to get all prescriptions
    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepository.findAll();
    }

    // Method to create a prescription
    public void createPrescription(CreatePrescriptionDTO createPrescriptionDTO) {
        // Check if the doctor or patient exists
        String appointmentUuid = createPrescriptionDTO.getAppointmentUuid();
        Appointment appointment = appointmentRepository.findById(appointmentUuid)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment does not exist."));

        // Create a new Prescription entity
        Prescription prescription = Prescription.builder()
                .prescriptionUuid(generateRandomString(16)) // use your UUID generation method
                .appointmentUuid(appointmentUuid)
                .patientExamination(createPrescriptionDTO.getPatientExamination())
                .prescriptionDescription(createPrescriptionDTO.getPrescriptionDescription())
                .prescriptionCreatedAt(LocalDateTime.now())
                .prescriptionUpdatedAt(LocalDateTime.now())
                .build();

        // Save the prescription to the database
        prescriptionRepository.save(prescription);
    }
    
    public List<Prescription> getPrescriptionsByUserId(String userId) {
        // Check if the user exists as a doctor or patient
        boolean userExists = userRepository.existsById(userId);
        boolean doctorExists = appointmentRepository.existsByDoctorUuid(userId);
        boolean patientExists = appointmentRepository.existsByPatientUuid(userId);

        // If the user doesn't exist, throw an exception
        if (!userExists && !doctorExists && !patientExists) {
            throw new UserNotFoundException("User does not exist.");
        }

        // Retrieve all appointments for the user
        List<Appointment> appointments = appointmentRepository.findByDoctorUuidOrPatientUuid(userId, userId);

        // If no appointments are found, return an empty list and a message
        if (appointments.isEmpty()) {
            return new ArrayList<>(); // Return an empty response (you can customize this further if needed)
        }

        // Retrieve prescriptions for each appointment
        List<Prescription> prescriptions = new ArrayList<>();
        for (Appointment appointment : appointments) {
            List<Prescription> userPrescriptions = prescriptionRepository.findByAppointmentUuid(appointment.getAppointmentUuid());
            prescriptions.addAll(userPrescriptions);
        }

        return prescriptions;
    }

 // Update a prescription
    public void updatePrescription(String prescriptionUuid, UpdatePrescriptionDTO updatePrescriptionDTO) {
        Prescription prescription = prescriptionRepository.findById(prescriptionUuid)
                .orElseThrow(() -> new PrescriptionNotFoundException("Prescription not found with ID: " + prescriptionUuid));

        prescription.setPatientExamination(updatePrescriptionDTO.getPatientExamination());
        prescription.setPrescriptionDescription(updatePrescriptionDTO.getPrescriptionDescription());
        prescription.setPrescriptionUpdatedAt(LocalDateTime.now());

        prescriptionRepository.save(prescription);
    }

    // Delete a prescription
    public void deletePrescription(String prescriptionUuid) {
        if (!prescriptionRepository.existsById(prescriptionUuid)) {
            throw new PrescriptionNotFoundException("Prescription not found with ID: " + prescriptionUuid);
        }
        
        prescriptionRepository.deleteById(prescriptionUuid);
    }
    
	private static final SecureRandom random = new SecureRandom();
    private static String generateRandomString(int length) {
        byte[] randomBytes = new byte[length];
        random.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes).substring(0, length);
    }
    

}