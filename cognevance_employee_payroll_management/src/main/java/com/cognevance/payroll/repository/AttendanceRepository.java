package com.cognevance.payroll.repository;

import com.cognevance.payroll.dto.AttendenceResponse;
import com.cognevance.payroll.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    boolean existsByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate);

    List<Attendance> findByEmployeeIdAndWorkDateBetweenOrderByWorkDate(Long employeeId,
                                                                               LocalDate start,
                                                                               LocalDate end);

    List<Attendance> findByWorkDateBetweenOrderByWorkDate(LocalDate start, LocalDate end);
}
