package com.wg.dabms.appointment2;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wg.dabms.enums.AppointmentStatus;
import com.wg.dabms.enums.AppointmentTimeSlot;

public interface AppointmentRepository2 extends JpaRepository<AppointmentDTO2, String> {

	@Query("SELECT a FROM AppointmentDTO2 a " + "WHERE (:doctorId IS NULL OR a.doctorUuid = :doctorId) "
			+ "AND (:patientId IS NULL OR a.patientUuid = :patientId) "
			+ "AND (:status IS NULL OR a.appointmentStatus = :status) "
			+ "AND (:scheduledDate IS NULL OR a.appointmentScheduledDate = :scheduledDate) "
			+ "AND (:slot IS NULL OR a.appointmentSlot = :slot)")
	Page<AppointmentDTO2> findByCriteria(@Param("doctorId") String doctorId, @Param("patientId") String patientId,
			@Param("status") AppointmentStatus status, @Param("scheduledDate") LocalDate scheduledDate,
			@Param("slot") AppointmentTimeSlot slot, Pageable pageable);

	Page<AppointmentDTO2> findByDoctorUuid(String userId, Pageable pageable);

	Page<AppointmentDTO2> findByPatientUuid(String userId, Pageable pageable);
}
