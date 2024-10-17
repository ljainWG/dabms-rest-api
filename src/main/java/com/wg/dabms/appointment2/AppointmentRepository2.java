package com.wg.dabms.appointment2;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.wg.dabms.enums.AppointmentStatus;
import com.wg.dabms.enums.AppointmentTimeSlot;

public interface AppointmentRepository2 extends JpaRepository<AppointmentDTO2, String> {

    Page<AppointmentDTO2> findByCriteria(String doctorId, String patientId, AppointmentStatus status,
                                           LocalDate scheduledDate, AppointmentTimeSlot slot, Pageable pageable);
}
