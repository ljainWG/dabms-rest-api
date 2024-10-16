package com.wg.dabms.appointment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wg.dabms.enums.AppointmentStatus;
import com.wg.dabms.enums.AppointmentTimeSlot;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, String> {

	// Custom query for searching appointments
    @Query("SELECT a FROM Appointment a WHERE " +
            "(:status IS NULL OR a.appointmentStatus = :status) AND " +
            "(:doctorUuid IS NULL OR a.doctorUuid = :doctorUuid) AND " +
            "(:patientUuid IS NULL OR a.patientUuid = :patientUuid) AND " +
            "(:appointmentScheduledDate IS NULL OR a.appointmentScheduledDate = :appointmentScheduledDate) AND " +
            "(:appointmentSlot IS NULL OR a.appointmentSlot = :appointmentSlot)")
    List<Appointment> findByCriteria(
            @Param("status") AppointmentStatus status,
            @Param("doctorUuid") String doctorUuid,
            @Param("patientUuid") String patientUuid,
            @Param("appointmentScheduledDate") LocalDate appointmentScheduledDate,
            @Param("appointmentSlot") AppointmentTimeSlot appointmentSlot);
    
    @Query("SELECT a FROM Appointment a WHERE a.patientUuid = :userId OR a.doctorUuid = :userId")
    List<Appointment> findByUserId(@Param("userId") String userId);

    // Find an appointment by user ID and appointment ID
    @Query("SELECT a FROM Appointment a WHERE (a.patientUuid = :userId OR a.doctorUuid = :userId) AND a.appointmentUuid = :appointmentId")
    Optional<Appointment> findByUserIdAndAppointmentId(@Param("userId") String userId, @Param("appointmentId") String appointmentId);

    List<Appointment> findByDoctorUuidOrPatientUuid(String doctorUuid, String patientUuid);
    boolean existsByDoctorUuid(String doctorUuid);
    boolean existsByPatientUuid(String patientUuid);
   
}
