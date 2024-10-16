package com.wg.dabms.prescription;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, String> {
    List<Prescription> findByAppointmentUuid(String appointmentUuid);
}
