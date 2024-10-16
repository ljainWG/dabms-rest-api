package com.wg.dabms.appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wg.dabms.enums.AppointmentStatus;
import com.wg.dabms.enums.AppointmentTimeSlot;
import com.wg.dabms.user.UserService;

import jakarta.transaction.Transactional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

	public List<Appointment> findAllAppointments() {
		List<Appointment> appointments = appointmentRepository.findAll();
//		if (appointments == null || appointments.isEmpty())
			return appointments;
	}
	
	// Find an appointment by ID
    public Optional<Appointment> findAppointmentById(String appointmentId) {
        return appointmentRepository.findById(appointmentId);
    }

    // Find appointments by user ID
    public List<Appointment> findAppointmentsByUserId(String userId) {
        return appointmentRepository.findByUserId(userId);
    }

    // Find a specific appointment by user ID and appointment ID
    public Optional<Appointment> findAppointmentByUserIdAndAppointmentId(String userId, String appointmentId) {
        return appointmentRepository.findByUserIdAndAppointmentId(userId, appointmentId);
    }
	
	@Transactional
    public Appointment createAppointment(CreationAppointmentDTO creationAppointmentDTO) {
        Appointment appointment = Appointment.builder()
                .appointmentUuid(UserService.generateRandomString(16))
                .doctorUuid(creationAppointmentDTO.getDoctorUuid())
                .patientUuid(creationAppointmentDTO.getPatientUuid())
                .appointmentScheduledDate(creationAppointmentDTO.getAppointmentScheduledDate())
                .appointmentSlot(creationAppointmentDTO.getAppointmentSlot())
                .appointmentStatus(AppointmentStatus.SCHEDULED) // Default status
                .dateTimeOfBookingOfAppointment(LocalDateTime.now())
                .dateTimeOfUpdationOfAppointmentStatus(LocalDateTime.now())
                .build();
        return appointmentRepository.save(appointment);
    }

    // Update an existing appointment
    @Transactional
    public Optional<Appointment> updateAppointment(String appointmentUuid, CreationAppointmentDTO updatedAppointmentDTO) {
        return appointmentRepository.findById(appointmentUuid).map(appointment -> {
            appointment.setDoctorUuid(updatedAppointmentDTO.getDoctorUuid());
            appointment.setPatientUuid(updatedAppointmentDTO.getPatientUuid());
            appointment.setAppointmentScheduledDate(updatedAppointmentDTO.getAppointmentScheduledDate());
            appointment.setAppointmentSlot(updatedAppointmentDTO.getAppointmentSlot());
            appointment.setDateTimeOfUpdationOfAppointmentStatus(LocalDateTime.now());
            return appointmentRepository.save(appointment);
        });
    }

    // Partially update the appointment status
    @Transactional
    public Optional<Appointment> updateAppointmentStatus(String appointmentUuid, UpdateAppointmentStatusDTO updateAppointmentStatusDTO) {
        return appointmentRepository.findById(appointmentUuid).map(appointment -> {
            if (appointment.getAppointmentStatus() == AppointmentStatus.SCHEDULED) {
                appointment.setAppointmentStatus(updateAppointmentStatusDTO.getAppointmentStatus());
                appointment.setDateTimeOfUpdationOfAppointmentStatus(LocalDateTime.now());
                return appointmentRepository.save(appointment);
            } else {
                throw new IllegalArgumentException("Appointment status can only be updated if it is SCHEDULED.");
            }
        });
    }

    // Search appointments
    public List<Appointment> searchAppointments(AppointmentStatus status, String doctorUuid, String patientUuid,
                                                LocalDate appointmentScheduledDate, AppointmentTimeSlot appointmentSlot) {
        return appointmentRepository.findByCriteria(status, doctorUuid, patientUuid, appointmentScheduledDate, appointmentSlot);
    }
    
}
