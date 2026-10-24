# Employee Payroll Management System

A **Spring Boot monolith** for employee registration, attendance tracking, monthly payroll generation, payroll status, and salary reports. Built for the Level 2 project brief in `Java_Developer.pdf`.

## Technology stack

- Java / JDK 21
- Spring Boot 4.0.3
- Spring MVC REST APIs, Spring Data JPA / Hibernate
- MySQL
- Spring Security HTTP Basic (admin-only demo account)
- Bean Validation, centralized exception handling
- `@Async` + `CompletableFuture` for batch payroll generation
- Maven

## Architecture

```text
REST Client / Postman
        |
        v
Controllers (employees, attendance, payroll)
        |
        v
Services (business rules, payroll calculations, reports)
        |
        v
Spring Data JPA repositories
        |
        v
MySQL: employees, attendance, payrolls
```

This is a single deployable application, not microservices. Modules share one application and one database.

## Requirements

- JDK 21 (set IntelliJ Project SDK and Maven runner JDK to 21)
- Maven 3.9+
- MySQL 8+

## Setup

1. Create the database (or allow the JDBC URL to create it):

   ```sql
   CREATE DATABASE employee_payroll;
   ```

2. Copy `.env.example` to `.env` if desired. Spring does not automatically load `.env`; either set the variables in your IDE run configuration or edit `src/main/resources/application.properties`. Defaults are `root` / `root`, so configure your own database credentials.
3. Open the project folder in IntelliJ IDEA, choose **JDK 21** as Project SDK and Maven runner JDK, then reload Maven.
4. Run `EmployeePayrollApplication.java`, or run from a terminal where Maven is installed:

   ```bash
   mvn spring-boot:run
   ```

5. Base URL: `http://localhost:8080`. All endpoints require HTTP Basic authentication. Demo defaults: username `admin`, password `ChangeMe123!`. Override with `APP_USERNAME` and `APP_PASSWORD`. Change the demo password before sharing/deploying.

## API authentication

Use Postman Authorization → Basic Auth, or curl:

```bash
curl -u admin:admin123! http://localhost:8080/api/employees
```

## REST API

All JSON dates use ISO format (`YYYY-MM-DD`); timestamps use ISO date-time. Send `Content-Type: application/json`.

### Employees

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/employees` | Register employee |
| GET | `/api/employees` | List all |
| GET | `/api/employees?department=Engineering` | Filter by department |
| GET | `/api/employees?active=true` | Filter by active state |
| GET | `/api/employees/{id}` | Employee details |
| PUT | `/api/employees/{id}` | Update employee |
| DELETE | `/api/employees/{id}` | Soft-deactivate employee; preserves payroll/attendance history |

Example employee body:

```json
{
  "employeeCode": "EMP-1001",
  "fullName": "Aarav Sharma",
  "email": "aarav@example.com",
  "phone": "9876543210",
  "department": "Engineering",
  "designation": "Java Developer",
  "baseSalary": 45000.00,
  "joiningDate": "2025-06-01",
  "active": true
}
```

### Attendance

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/attendance` | Record one employee's attendance for a date |
| GET | `/api/attendance?start=2026-10-01&end=2026-10-31` | Attendance report for date range |
| GET | `/api/attendance/employee/{employeeId}?year=2026&month=10` | Employee's monthly attendance |

Example attendance body:

```json
{
  "employeeId": 1,
  "workDate": "2026-10-19",
  "status": "PRESENT",
  "checkIn": "2026-10-19T09:00:00",
  "checkOut": "2026-10-19T17:30:00",
  "notes": "On time"
}
```

Allowed attendance statuses: `PRESENT`, `ABSENT`, `LEAVE`, `HALF_DAY`. Only one attendance record per employee per date is allowed.

### Payroll

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/payroll/employee/{employeeId}` | Generate payroll for one employee |
| POST | `/api/payroll/generate-all` | Asynchronously process all active employees |
| GET | `/api/payroll?period=2026-10` | List payroll records for month |
| GET | `/api/payroll/employee/{employeeId}` | Payroll history for employee |
| PATCH | `/api/payroll/{payrollId}/paid` | Mark payroll as paid |
| GET | `/api/payroll/summary?period=2026-10` | Payroll analytics summary |

Example payroll request:

```json
{
  "period": "2026-10",
  "allowance": 3500.00,
  "deduction": 1200.00
}
```

### Payroll calculation policy

- `grossSalary = baseSalary + allowance`
- `netSalary = max(grossSalary - deduction, 0)`
- Attendance counts (`paidDays`, `absentDays`) are included in the payroll record.
- For this demo, monthly salary is **not prorated** based on attendance; payroll policy varies by employer, so update the formula if your assignment requires proration.
- A payroll record is unique per employee and month. The batch endpoint skips already-generated records.
- Marking a payroll paid changes its status from `GENERATED` to `PAID`.

### Reports

`GET /api/payroll/summary?period=2026-10` returns payroll count, paid/pending count, total gross, and total net. Attendance date-range endpoints support attendance reporting.

## Database schema

JPA creates/updates these tables using `spring.jpa.hibernate.ddl-auto=update`:

- `employees`: unique employee code and email, salary, department, designation, joining date, active flag
- `attendance`: employee FK, date, status, check-in/out; unique employee/date pair
- `payrolls`: employee FK, period, salary breakdown, attendance counts, generation date and payment status; unique employee/period pair

For a production deployment, use Flyway/Liquibase migrations and set `ddl-auto=validate`.

## Security and limitations

- Demo HTTP Basic account is in-memory and intended for local evaluation, not production identity management.
- Use HTTPS, secrets from environment/secret manager, role-specific access, audit logs, and a proper identity provider before production use.
- The async batch operation runs in-process; it is not a durable job queue.
- Configure your database credentials securely. Never commit real passwords.
- For simplicity, this project uses entity responses directly; a production API should use response DTOs to avoid exposing persistence models.

## Testing

Run:

```bash
mvn test
```

The context test requires a reachable MySQL database with the configured credentials. Use IntelliJ's Maven panel if Maven is not installed on your terminal.

## Suggested submission checklist

- [ ] Run and test the APIs in Postman
- [ ] Capture screenshots for employee CRUD, attendance, payroll, and summary report
- [ ] Add screenshots to `docs/screenshots/`
- [ ] Commit and push to a repository named `cognevance_employee-payroll-management`
- [ ] Submit the GitHub URL and project review to `support@cognevance.online`
