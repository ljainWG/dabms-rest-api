package com.wg.dabms.prescription;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Prescriptions")
public class Prescription {

	@Id
	@Column(name = "prescription_uuid", nullable = false, unique = true)
	private String prescriptionUuid;

	@Column(name = "appointment_uuid", nullable = false)
	private String appointmentUuid;

	@Column(name = "patient_examination", columnDefinition = "TEXT")
	@Pattern(regexp = "^[a-zA-Z0-9 ,.]+$", message = "Patient examination should contain only alphanumeric characters, spaces, and punctuation.")
    @Size(min = 10, message = "Patient examination must be at least 10 characters long.")
    private String patientExamination; // problem or symptoms told by patient during examination

	@Column(name = "prescription_description", columnDefinition = "TEXT")
	@Pattern(regexp = "^[a-zA-Z0-9 ,.]+$", message = "Prescription description should contain only alphanumeric characters, spaces, and punctuation.")
    @Size(min = 10, message = "Prescription description must be at least 10 characters long.")
    private String prescriptionDescription;

	@Column(name = "prescription_created_at", nullable = false)
	private LocalDateTime prescriptionCreatedAt;

	@Column(name = "prescription_updated_at", nullable = false)
	private LocalDateTime prescriptionUpdatedAt;
	
}