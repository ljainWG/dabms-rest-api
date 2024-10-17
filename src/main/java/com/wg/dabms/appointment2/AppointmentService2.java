package com.wg.dabms.appointment2;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.wg.dabms.api_response.ApiResponseStatus;
import com.wg.dabms.enums.AppointmentStatus;
import com.wg.dabms.enums.AppointmentTimeSlot;
import com.wg.dabms.enums.UserRole;
import com.wg.dabms.envelope.ResponseEnvelopeWithPagination2;
import com.wg.dabms.exceptions.AppointmentNotFoundException;
import com.wg.dabms.exceptions.UserNotFoundException;
import com.wg.dabms.newexceptions.UnauthorizedAccessException;
import com.wg.dabms.newexceptions.UserNotFoundException2;
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

	public Page<AppointmentDTO2> findAllAppointments(String doctorId, String patientId, AppointmentStatus status,
			LocalDate scheduledDate, AppointmentTimeSlot slot, Pageable pageable) {

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
				throw new UnauthorizedAccessException("You cannot book an appointment for another user.");
			}
		}

		// Validate that the userId in the request exists
		User2DTO doctor = user2Repository.findById(creationAppointmentDTO.getDoctorUuid()).orElseThrow(
				() -> new UserNotFoundException("User not found with ID: " + creationAppointmentDTO.getDoctorUuid()));
		User2DTO patient = user2Repository.findById(creationAppointmentDTO.getPatientUuid()).orElseThrow(
				() -> new UserNotFoundException("User not found with ID: " + creationAppointmentDTO.getPatientUuid()));

		// Check roles for doctor and patient
		if (!user2Service.isDoctor(doctor)) {
			throw new UnauthorizedAccessException("The user with ID " + doctor.getUserUuid() + " is not a valid doctor.");
		}
		if (!user2Service.isPatient(patient)) {
			throw new UnauthorizedAccessException("The user with ID " + patient.getUserUuid() + " is not a valid patient.");
		}

		String newAppointmentUuid = generateUniqueAppointmentUuid();

		// Create the appointment
		AppointmentDTO2 appointment = AppointmentDTO2.builder().appointmentUuid(newAppointmentUuid) // Assuming this
																									// method exists
				.doctorUuid(doctor.getUserUuid()).patientUuid(patient.getUserUuid())
				.appointmentScheduledDate(creationAppointmentDTO.getAppointmentScheduledDate())
				.appointmentSlot(creationAppointmentDTO.getAppointmentSlot())
				.appointmentStatus(AppointmentStatus.SCHEDULED) // Default status
				.dateTimeOfBookingOfAppointment(LocalDateTime.now())
				.dateTimeOfUpdationOfAppointmentStatus(LocalDateTime.now()).build();

		return appointmentRepository.save(appointment);
	}

	public ResponseEnvelopeWithPagination2 getUserAppointments(String userId, int page, int size) {
		// Create a Pageable instance
		Pageable pageable = PageRequest.of(page, size);

		// Check if the requested user exists
		User2DTO existingUser = user2Repository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException2("User not found with ID: " + userId));

		User2DTO currentUser = user2Service.getCurrentUser();

		// Check if the current user is authorized to view the requested user's
		// appointments
		boolean isCurrentUser = user2Service.isCurrentUser(existingUser, currentUser);
		boolean isAdminOrReceptionist = user2Service.isAdmin(currentUser) || user2Service.isReceptionist(currentUser);

		if (!isCurrentUser && !isAdminOrReceptionist) {
			throw new UnauthorizedAccessException("You are not authorized to see appointments of this user.");
		}

		// Fetch appointments based on the current user's role
		Page<AppointmentDTO2> appointmentsPage;
		if (user2Service.isDoctor(currentUser)) {
			appointmentsPage = appointmentRepository.findByDoctorUuid(userId, pageable);
		} else {
			// For admins and receptionists, we assume they can view the patient's
			// appointments
			appointmentsPage = appointmentRepository.findByPatientUuid(userId, pageable);
		}

		// Build and return the response envelope
		return ResponseEnvelopeWithPagination2.builder().status(ApiResponseStatus.SUCCESS)
				.message("Appointments retrieved successfully").data(appointmentsPage.getContent())
				.currentPageNo(appointmentsPage.getNumber()).totalNoOfRecords((int) appointmentsPage.getTotalElements())
				.totalNoOfPages(appointmentsPage.getTotalPages()).recordsPerPage(appointmentsPage.getSize())
				.timeStamp(LocalDateTime.now()).build();
	}

	@Transactional
	public ResponseEnvelopeWithPagination2 updateAppointmentStatus(String appointmentId,
			UpdateAppointmentStatusDTO2 statusUpdateDTO) {
		// Fetch the appointment by ID
		AppointmentDTO2 appointment = appointmentRepository.findById(appointmentId)
				.orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with ID: " + appointmentId));

		// Get the current logged-in user
		User2DTO currentUser = user2Service.getCurrentUser();

		// Handle the doctor case (doctors can cancel their own scheduled appointments)
		if (user2Service.isDoctor(currentUser)) {
			// Ensure the doctor is assigned to the appointment
			if (!currentUser.getUserUuid().equals(appointment.getDoctorUuid())) {
				throw new UnauthorizedAccessException(
						"You are not authorized to cancel appointments where you are not the assigned doctor.");
			}

			// Doctors can only cancel scheduled appointments
			if (!appointment.getAppointmentStatus().equals(AppointmentStatus.SCHEDULED)
					|| !statusUpdateDTO.getAppointmentStatus().equals(AppointmentStatus.CANCELLED)) {
				throw new UnauthorizedAccessException("You can only cancel your scheduled appointments.");
			}
		}

		// Handle the patient case (patients can only cancel their own scheduled appointments)
		else if (currentUser.getUserRole() == UserRole.PATIENT) {
			// Ensure the patient is the one who booked the appointment
			if (!currentUser.getUserUuid().equals(appointment.getPatientUuid())) {
				throw new UnauthorizedAccessException("You are not authorized to cancel someone else's appointment.");
			}

			// Patients can only cancel scheduled appointments
			if (!appointment.getAppointmentStatus().equals(AppointmentStatus.SCHEDULED)
					|| !statusUpdateDTO.getAppointmentStatus().equals(AppointmentStatus.CANCELLED)) {
				throw new UnauthorizedAccessException("You can only cancel your own scheduled appointments.");
			}
		}


		// Handle the receptionist case (can update scheduled appointments to any other status)
		else if (user2Service.isReceptionist(currentUser)) {
			if (!appointment.getAppointmentStatus().equals(AppointmentStatus.SCHEDULED)) {
				throw new UnauthorizedAccessException("Receptionists can only update scheduled appointments.");
			}
		}

		// Handle the admin case (can update to any status without restrictions)
		else if (user2Service.isAdmin(currentUser)) {
			// Admins can update to any status, so no restrictions apply here
		}

		// If the current user does not have permission
		else {
			throw new UnauthorizedAccessException("You do not have permission to update this appointment.");
		}

		// Update the appointment status and the update timestamp
		appointment.setAppointmentStatus(statusUpdateDTO.getAppointmentStatus());
		appointment.setDateTimeOfUpdationOfAppointmentStatus(LocalDateTime.now());

		// Save the updated appointment in the repository
		appointmentRepository.save(appointment);

		// Build and return the response envelope
		return ResponseEnvelopeWithPagination2.builder().status(ApiResponseStatus.SUCCESS)
				.message("Appointment status updated successfully.").data(appointment).timeStamp(LocalDateTime.now())
				.build();
	}
	
	@Transactional
    public void deleteAppointment(String appointmentId) {
		
        User2DTO currentUser = user2Service.getCurrentUser();

        // Check if the current user is an admin
        if (!user2Service.isAdmin(currentUser)) {
            throw new UnauthorizedAccessException("You do not have permission to delete this appointment.");
        }

        // Check if the appointment exists before trying to delete it
        if (!appointmentRepository.existsById(appointmentId)) {
            throw new AppointmentNotFoundException("Appointment not found with ID: " + appointmentId);
        }

        // Delete the appointment
        appointmentRepository.deleteById(appointmentId);
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
