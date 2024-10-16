package com.wg.dabms.appointment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wg.dabms.enums.AppointmentStatus;
import com.wg.dabms.enums.AppointmentTimeSlot;
import com.wg.dabms.user.UserService;

import jakarta.validation.Valid;

@RestController
public class AppointmentController {

	
	private AppointmentService appointmentService;
	private UserService userService;
	
	public AppointmentController(AppointmentService appointmentService, UserService userService) {
		this.appointmentService = appointmentService;
		this.userService = userService;
	}
	
	@GetMapping("/appointments")
	public ResponseEntity<List<Appointment>> getAllAppointments() {
		List<Appointment> appointments = appointmentService.findAllAppointments();
		return ResponseEntity.ok(appointments);
	}
	
	// Find an appointment by appointment ID
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<Appointment> findAppointmentById(@PathVariable String appointmentId) {
        Optional<Appointment> appointment = appointmentService.findAppointmentById(appointmentId);
        return appointment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Find appointments of a specific user by user ID
    @GetMapping("/user/{userId}/appointments")
    public ResponseEntity<List<Appointment>> findAppointmentsByUserId(@PathVariable String userId) {
        List<Appointment> appointments = appointmentService.findAppointmentsByUserId(userId);
        return ResponseEntity.ok(appointments);
    }

    // Find a specific appointment of a specific user by user ID and appointment ID
    @GetMapping("/user/{userId}/appointment/{appointmentId}")
    public ResponseEntity<Appointment> findAppointmentByUserIdAndAppointmentId(
            @PathVariable String userId, 
            @PathVariable String appointmentId) {
        
        Optional<Appointment> appointment = appointmentService.findAppointmentByUserIdAndAppointmentId(userId, appointmentId);
        return appointment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
	
	// Create a new appointment
    @PostMapping("/appointments")
    public ResponseEntity<Appointment> createAppointment(@Valid @RequestBody CreationAppointmentDTO creationAppointmentDTO) {
        Appointment appointment = appointmentService.createAppointment(creationAppointmentDTO);
        return ResponseEntity.ok(appointment);
    }

    // not working
//    // Update an existing appointment (PUT)
//    @PutMapping("/appointment/{appointmentUuid}")
//    public ResponseEntity<Appointment> updateAppointment(
//            @PathVariable String appointmentUuid,
//            @Valid @RequestBody CreationAppointmentDTO updatedAppointmentDTO) {
//        Optional<Appointment> updatedAppointment = appointmentService.updateAppointment(appointmentUuid, updatedAppointmentDTO);
//        return updatedAppointment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
//    }
//
//    // Partially update the appointment status (PATCH)
//    @PatchMapping("/appointment/{appointmentUuid}/status")
//    public ResponseEntity<Appointment> updateAppointmentStatus(
//            @PathVariable String appointmentUuid,
//            @Valid @RequestBody UpdateAppointmentStatusDTO updateAppointmentStatusDTO) {
//        Optional<Appointment> updatedAppointment = appointmentService.updateAppointmentStatus(appointmentUuid, updateAppointmentStatusDTO);
//        return updatedAppointment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
//    }

    // Search appointments by query parameters
    @GetMapping("/appointments/search")
    public ResponseEntity<List<Appointment>> searchAppointments(
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) String doctorUuid,
            @RequestParam(required = false) String patientUuid,
            @RequestParam(required = false) LocalDate appointmentScheduledDate,
            @RequestParam(required = false) AppointmentTimeSlot appointmentSlot) {
        
        List<Appointment> appointments = appointmentService.searchAppointments(status, doctorUuid, patientUuid, appointmentScheduledDate, appointmentSlot);
        return ResponseEntity.ok(appointments);
    }
//	
//	@GetMapping("/user/{userId}/appointments")
//	public ResponseEntity<List<Appointment>> getAllAppointmentsOfUserByUserId(@PathVariable String userId) {
//		User user = userService.findById(userId);
//		List<Appointment> appointments = appointmentService.findAllAppointmentByUserId();
//		return ResponseEntity.ok(appointments);
//	}
//	
//	@GetMapping("/user/{userId}/appointment/{appointmentId}")
//	public ResponseEntity<Appointment> getAppointmentOfUserByUserIdANDAppointmentId(@PathVariable String userId, @PathVariable String appointmentId) {
//		User user = userService.findById(userId);
//		Appointment appointment = appointmentService.findAppointmentByUserIdANDAppointmentId(appointmentId);
//		return ResponseEntity.ok(appointment);
//	}
//	
//	@PostMapping("/user/{userId}/appointment")
//	public ResponseEntity<Object> createNewAppointmentToUser(@PathVariable String userId, @Valid @RequestBody Appointment appointment) {
//		User user = userService.findById(userId);
//		Appointment appointmentCreated = appointmentService.createAppointmentByUserId(appointment);
//		return ApiResponseHandler.buildResponse(ApiResponseStatus.SUCCESS, HttpStatus.CREATED,
//				"APPOINTMENT CREATED SUCCESSFULLY", appointmentCreated);
//	}
//	
//	@PutMapping("/user/{userId}/appointment/{appointmentId}")
//	public ResponseEntity<Object> updateAppointmentOfUserByUserIdANDAppointmentId(@PathVariable String userId, @PathVariable String appointmentId, @Valid @RequestBody Appointment appointment){
//		Appointment appointmentToBeUpdated = appointmentService.findByUserIdANDAppointmentId(userId, appointmentId);
//		Appointment appointmentUpdated = appointmentService.save(appointmentId, appointment);
//		return ApiResponseHandler.buildResponse(ApiResponseStatus.SUCCESS, HttpStatus.OK, "APPOINTMENT UPDATED SUCCESSFULLY", appointmentUpdated);
//	}
//	
//	@DeleteMapping("/user/{userId}/appointment/{appointmentId}")
//	public ResponseEntity<Object> deleteAppointmentOfUserByUserIdANDAppointmentId(@PathVariable String userId, @PathVariable String appointmentId){
//		User user = userService.findById(userId);
//		Appointment appointmentToBeDeleted = appointmentService.findByUserIdANDAppointmentId(userId, appointmentId);
//		appointmentService.deleteAppointmentByAppointmentId(appointmentId);
//		return ApiResponseHandler.buildResponse(ApiResponseStatus.SUCCESS, HttpStatus.OK, "APPOINTMENT DELETED SUCCESSFULLY", appointmentToBeDeleted);
//	}
}
