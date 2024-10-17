package com.wg.dabms.appointment2;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.wg.dabms.enums.AppointmentStatus;
import com.wg.dabms.enums.AppointmentTimeSlot;
import com.wg.dabms.enums.UserRole;
import com.wg.dabms.exceptions.UserNotFoundException;
import com.wg.dabms.newexceptions.UnauthorizedException;
import com.wg.dabms.user2.User2DTO;
import com.wg.dabms.user2.User2Repository;
import com.wg.dabms.user2.User2Service;

import jakarta.transaction.Transactional;

@Service
public class AppointmentService2 {

    @Autowired
    private AppointmentRepository2 appointmentRepository;
    @Autowired
	private User2Repository user2Repository;
    @Autowired
	private User2Service user2Service;

    public Page<AppointmentDTO2> findAllAppointments(
            String doctorId,
            String patientId,
            AppointmentStatus status,
            LocalDate scheduledDate,
            AppointmentTimeSlot slot,
            Pageable pageable) {
        
    	User2DTO currentUser = user2Service.getCurrentUser();
    	user2Service.checkAdminOrReceptionistAccess(currentUser);
		
        return appointmentRepository.findByCriteria(doctorId, patientId, status, scheduledDate, slot, pageable);
    }
    
    @Transactional
    public AppointmentDTO2 createAppointment(CreationAppointmentDTO2 creationAppointmentDTO) {
        User2DTO currentUser = user2Service.getCurrentUser();

        // Check if the current user is a patient
        if (currentUser.getUserRole() == UserRole.PATIENT) {
            // If current user is a patient, they can only book appointments for themselves
            if (!currentUser.getUserUuid().equals(creationAppointmentDTO.getPatientUuid())) {
                throw new UnauthorizedException("You cannot book an appointment for another user.");
            }
        }
        
        // Validate that the userId in the request exists
        User2DTO doctor = user2Repository.findById(creationAppointmentDTO.getDoctorUuid())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + creationAppointmentDTO.getDoctorUuid()));
        User2DTO patient = user2Repository.findById(creationAppointmentDTO.getPatientUuid())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + creationAppointmentDTO.getPatientUuid()));

     // Check roles for doctor and patient
        if (user2Service.isDoctor(doctor)) {
            throw new UnauthorizedException("The user with ID " + doctor.getUserUuid() + " is not a valid doctor.");
        }
        if (user2Service.isPatient(patient)) {
            throw new UnauthorizedException("The user with ID " + patient.getUserUuid() + " is not a valid patient.");
        }
        
        String newAppointmentUuid = generateUniqueAppointmentUuid();
        
        // Create the appointment
        AppointmentDTO2 appointment = AppointmentDTO2.builder()
                .appointmentUuid(newAppointmentUuid) // Assuming this method exists
                .doctorUuid(doctor.getUserUuid())
                .patientUuid(patient.getUserUuid())
                .appointmentScheduledDate(creationAppointmentDTO.getAppointmentScheduledDate())
                .appointmentSlot(creationAppointmentDTO.getAppointmentSlot())
                .appointmentStatus(AppointmentStatus.SCHEDULED) // Default status
                .dateTimeOfBookingOfAppointment(LocalDateTime.now())
                .dateTimeOfUpdationOfAppointmentStatus(LocalDateTime.now())
                .build();

        return appointmentRepository.save(appointment);
    }
    
////////////////////////////////////////////////////////////////////////////////////////////
//                                                 helper methods
    private String generateUniqueAppointmentUuid() {
		String newAppointmentUuid;
		do {
			newAppointmentUuid = user2Service.generateRandomString(16);
		} while (appointmentRepository.existsById(newAppointmentUuid));
		return newAppointmentUuid;
	}
}
