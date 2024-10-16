package com.wg.dabms.appointment;

import java.time.LocalDate;

import com.wg.dabms.enums.AppointmentTimeSlot;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreationAppointmentDTO {	
	
	@NotBlank(message = "Doctor UUID cannot be blank")
    @Size(min = 16, max = 16, message = "Doctor UUID must be 16 characters")
	String doctorUuid;
	
	@NotBlank(message = "Patient UUID cannot be blank")
    @Size(min = 16, max = 16, message = "Patient UUID must be 16 characters")
	String patientUuid;    
	
	@NotNull(message = "Appointment scheduled date cannot be null")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Appointment date must be in yyyy-MM-dd format")
	LocalDate appointmentScheduledDate;
	
	@NotNull(message = "Appointment slot cannot be null")
    @Enumerated(EnumType.STRING)
    AppointmentTimeSlot appointmentSlot;
    
}
