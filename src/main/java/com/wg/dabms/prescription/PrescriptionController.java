package com.wg.dabms.prescription;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wg.dabms.api_response.ApiResponseStatus;
import com.wg.dabms.envelope.ResponseEnvelopeWithPagination;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/prescription")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    // GET /prescription
    @GetMapping
    public ResponseEntity<ResponseEnvelopeWithPagination> getAllPrescriptions() {
        List<Prescription> prescriptions = prescriptionService.getAllPrescriptions();
        ResponseEnvelopeWithPagination response = ResponseEnvelopeWithPagination.builder()
                .status(ApiResponseStatus.SUCCESS)
                .message("Retrieved all prescriptions successfully.")
                .data(prescriptions)
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // POST /prescription
    @PostMapping
    public ResponseEntity<ResponseEnvelopeWithPagination> createPrescription(@RequestBody CreatePrescriptionDTO createPrescriptionDTO) {
        prescriptionService.createPrescription(createPrescriptionDTO);
        ResponseEnvelopeWithPagination response = ResponseEnvelopeWithPagination.builder()
                .status(ApiResponseStatus.SUCCESS)
                .message("Prescription created successfully.")
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseEnvelopeWithPagination> getPrescriptionsByUserId(@PathVariable String userId) {
        List<Prescription> prescriptions = prescriptionService.getPrescriptionsByUserId(userId);
        
        // Create response envelope
        ResponseEnvelopeWithPagination responseEnvelope;
        
        if (prescriptions.isEmpty()) {
            responseEnvelope = ResponseEnvelopeWithPagination.builder()
                    .status(ApiResponseStatus.SUCCESS)
                    .message("No prescriptions found for the provided user ID.")
                    .data(prescriptions)
                    .build();
        } else {
            responseEnvelope = ResponseEnvelopeWithPagination.builder()
                    .status(ApiResponseStatus.SUCCESS)
                    .message("Retrieved prescriptions successfully.")
                    .data(prescriptions)
                    .build();
        }

        return ResponseEntity.ok(responseEnvelope);
    }
    
 // PUT to update a prescription
    @PutMapping("/{prescriptionUuid}")
    public ResponseEntity<ResponseEnvelopeWithPagination> updatePrescription(@PathVariable String prescriptionUuid, 
                                                                          @Valid @RequestBody UpdatePrescriptionDTO updatePrescriptionDTO) {
        prescriptionService.updatePrescription(prescriptionUuid, updatePrescriptionDTO);
        
        ResponseEnvelopeWithPagination responseEnvelope = ResponseEnvelopeWithPagination.builder()
                .status(ApiResponseStatus.SUCCESS)
                .message("Prescription updated successfully.")
                .data(null) // or provide the updated prescription if needed
                .build();
        
        return ResponseEntity.ok(responseEnvelope);
    }

    // DELETE a prescription
    @DeleteMapping("/{prescriptionUuid}")
    public ResponseEntity<ResponseEnvelopeWithPagination> deletePrescription(@PathVariable String prescriptionUuid) {
        prescriptionService.deletePrescription(prescriptionUuid);
        
        ResponseEnvelopeWithPagination responseEnvelope = ResponseEnvelopeWithPagination.builder()
                .status(ApiResponseStatus.SUCCESS)
                .message("Prescription deleted successfully.")
                .data(null)
                .build();
        
        return ResponseEntity.ok(responseEnvelope);
    }
    
}
