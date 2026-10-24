package com.cognevance.payroll.model;

import com.cognevance.payroll.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "attendance", uniqueConstraints =
        @UniqueConstraint(name = "uk_attendance_employee_date", columnNames = {"employee_id", "work_date"}))
public class Attendance {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceStatus status;

    private LocalDateTime checkIn;

    private LocalDateTime checkOut;

    @Column(length = 500)
    private String notes;

//    protected Attendance() {}
//
    public Attendance(Employee employee, LocalDate workDate, AttendanceStatus status, LocalDateTime checkIn,
                      LocalDateTime checkOut, String notes) {
        this.employee = employee; this.workDate = workDate; this.status = status;
        this.checkIn = checkIn; this.checkOut = checkOut; this.notes = notes;
    }
//    public Long getId() { return id; }
//    public Employee getEmployee() { return employee; }
//    public LocalDate getWorkDate() { return workDate; }
//    public LocalDateTime getCheckIn() { return checkIn; }
//    public LocalDateTime getCheckOut() { return checkOut; }
//    public String getNotes() { return notes; }
}
