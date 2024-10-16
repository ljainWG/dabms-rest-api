package com.wg.dabms.prescription;

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
public class CreatePrescriptionDTO {

	private String appointmentUuid;

	@Pattern(regexp = "^[a-zA-Z0-9 ,.]+$", message = "Patient examination should contain only alphanumeric characters, spaces, and punctuation.")
    @Size(min = 10, message = "Patient examination must be at least 10 characters long.")
    private String patientExamination; // problem or symptoms told by patient during examination

	@Pattern(regexp = "^[a-zA-Z0-9 ,.]+$", message = "Prescription description should contain only alphanumeric characters, spaces, and punctuation.")
    @Size(min = 10, message = "Prescription description must be at least 10 characters long.")
    private String prescriptionDescription;

}