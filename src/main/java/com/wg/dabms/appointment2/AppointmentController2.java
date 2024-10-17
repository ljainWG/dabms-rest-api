package com.wg.dabms.appointment2;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wg.dabms.api_response.ApiResponseStatus;
import com.wg.dabms.enums.AppointmentStatus;
import com.wg.dabms.enums.AppointmentTimeSlot;
import com.wg.dabms.envelope.ResponseEnvelopeWithPagination2;

@RestController
public class AppointmentController2 {

    private final AppointmentService2 appointmentService;

    public AppointmentController2(AppointmentService2 appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/v2/appointments")
    public ResponseEntity<ResponseEnvelopeWithPagination2> getAllAppointments(
            @RequestParam(required = false) String doctorId,
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) LocalDate scheduledDate,
            @RequestParam(required = false) AppointmentTimeSlot slot,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AppointmentDTO2> appointmentsPage = appointmentService.findAllAppointments(doctorId, patientId, status, scheduledDate, slot, pageable);
        
        ResponseEnvelopeWithPagination2 response = ResponseEnvelopeWithPagination2.builder()
                .status(ApiResponseStatus.SUCCESS)
                .message("Appointments retrieved successfully")
                .data(appointmentsPage.getContent())
                .currentPageNo(appointmentsPage.getNumber())
                .totalNoOfRecords((int) appointmentsPage.getTotalElements())
                .totalNoOfPages(appointmentsPage.getTotalPages())
                .recordsPerPage(appointmentsPage.getSize())
                .timeStamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/v2/appointments")
    public ResponseEntity<AppointmentDTO2> createAppointment(@RequestBody CreationAppointmentDTO2 creationAppointmentDTO) {
        AppointmentDTO2 createdAppointment = appointmentService.createAppointment(creationAppointmentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
    }
    
    @GetMapping("/v2/user/{userId}/appointments")
    public ResponseEntity<ResponseEnvelopeWithPagination2> getUserAppointments(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) { 

        ResponseEnvelopeWithPagination2 response = appointmentService.getUserAppointments(userId, page, size);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/v2/appointment/{appointmentId}")
    public ResponseEntity<ResponseEnvelopeWithPagination2> updateAppointmentStatus(
            @PathVariable String appointmentId,
            @RequestBody UpdateAppointmentStatusDTO2 statusUpdateDTO) { // Assuming you have a DTO for the status update

        ResponseEnvelopeWithPagination2 response = appointmentService.updateAppointmentStatus(appointmentId, statusUpdateDTO);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/v2/appointment/{appointmentId}")
    public ResponseEntity<String> deleteAppointment(@PathVariable String appointmentId) {
        appointmentService.deleteAppointment(appointmentId);
        return ResponseEntity.ok("Appointment deleted successfully.");
    }
}
