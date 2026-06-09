# Hostel Management System

> **Semester Final Project — CSC-104L Database Systems Lab**  
> University of Engineering & Technology, Lahore | IST 2nd-A | Spring 2026

---

## Group Members

| Name | Roll Number |
|------|-------------|
| Raana Wasim | 2025IST24 |
| Amna Bibi | 2025IST2 |

**Section:** IST 2nd-A  
**Teacher:** Sir Rashad Mahmood Khan  
**Project ID:** DBS26IST2ndAF001

---

## Project Overview

A full-stack desktop application that digitalizes and streamlines university hostel operations. The system handles student accommodation, room allocation, fee collection, staff administration, visitor logging, complaint resolution, maintenance tracking, and automated PDF report generation — all backed by a relational MySQL database with stored procedures, triggers, and views.

---

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Java (JDK 17+) |
| GUI | Java Swing |
| Database | MySQL 8.0 |
| DB Access | JDBC + DAO Pattern |
| PDF Reports | iText Core 9.6.0 + slf4j 2.0 |
| IDE | IntelliJ IDEA |

---

## Features

- **Role-based login** — Admin, Warden, Accountant, Security, Student
- **Dashboard** — live stats (total students, occupied rooms, pending fees, open complaints) with recent activity feed
- **Student Management** — registration, profiles, program/semester tracking, status management
- **Room & Block Management** — real-time availability, capacity tracking, rent configuration
- **Room Allocation / Booking** — allocate and vacate rooms, full allocation history
- **Fee Management** — monthly bill generation, payment processing, defaulter tracking
- **Staff Management** — wardens, security, maintenance staff with block assignments
- **Visitor Log** — entry/exit recording with timestamps and purpose tracking
- **Complaint System** — filing, priority assignment, status tracking and resolution
- **Maintenance Requests** — issue logging with cost recording and status updates
- **Notices** — hostel notice board with active/expired management
- **Audit Logging** — automatic action logging to `audit_logs` table via triggers
- **PDF Reports** — 10 business reports with date/status/month filters, auto-saved to `reports/` folder

---

## PDF Reports

| ID | Report | Data Source |
|----|--------|-------------|
| R01 | Student Register | Students table |
| R02 | Room Occupancy Report | Rooms + Blocks |
| R03 | Booking History | Room Allocations |
| R04 | Fee Collection Report | Fee Bills by month |
| R05 | Outstanding Dues | Fee Defaulters |
| R06 | Complaint Summary | Complaints by status |
| R07 | Staff Directory | Staff + Blocks |
| R08 | Visitor Log Report | Visitors by date range |
| R09 | Monthly Income Summary | Fee Bills aggregated by month |
| R10 | Vacancy & Availability | Available rooms |

---

## Database Stats

| Item | Count |
|------|-------|
| Tables | 17 |
| Views | 6 |
| Stored Procedures | 4 |
| Triggers | 4 |
| Constraints | 61+ |

### Tables
`departments`, `blocks`, `rooms`, `staff`, `students`, `users`, `room_allocations`, `fee_structure`, `fee_bills`, `fee_payments`, `visitors`, `complaints`, `maintenance_requests`, `meal_plans`, `student_meal_plans`, `notices`, `audit_logs`

---

## Project Structure

```
HMS/
├── src/
│   ├── Main.java
│   ├── dao/                  # Data access layer
│   │   ├── ComplaintDAO.java
│   │   ├── FeeBillDAO.java
│   │   ├── RoomAllocationDAO.java
│   │   ├── RoomDAO.java
│   │   ├── StaffDAO.java
│   │   ├── StudentDAO.java
│   │   ├── UserDAO.java
│   │   └── VisitorDAO.java
│   ├── domain/               # Domain/model classes
│   │   ├── Block, Room, Staff, Student, User
│   │   ├── Complaint, FeeBill, FeePayment
│   │   ├── MaintenanceRequest, Notice, Visitor
│   ├── software/             # Core infrastructure
│   │   ├── DBConnection.java
│   │   ├── Logger.java
│   │   ├── SessionManager.java
│   │   └── Validator.java
│   └── ui/                   # Swing UI screens
│       ├── LoginFrame.java
│       ├── DashboardFrame.java
│       ├── StudentForm.java
│       ├── RoomForm.java
│       ├── BookingForm.java
│       ├── FeeForm.java
│       ├── ComplaintForm.java
│       ├── StaffForm.java
│       ├── VisitorForm.java
│       ├── MaintenanceForm.java
│       ├── NoticeForm.java
│       ├── ReportsPanel.java
│       └── util/             # Theme + UIFactory helpers
├── Database/
│   ├── schema.sql            # Full DB creation script
│   ├── views.sql             # All 6 views
│   ├── procedures.sql        # Stored procedures
│   ├── triggers.sql          # 4 triggers
│   └── Sample data.sql       # Test data
├── libs/                     # All required JARs
│   ├── iText Core 9.6.0 (commons, io, kernel, layout, ...)
│   ├── slf4j-api-2.0.x.jar
│   ├── slf4j-simple-2.0.x.jar
│   └── mysql-connector-j-9.7.0.jar
├── reports/                  # Generated PDFs saved here
├── logs/                     # Application error logs
└── README.md
```

---

## How to Run

### Prerequisites
- Java JDK 17+
- MySQL 8.0
- IntelliJ IDEA
- All JARs present in `/libs` (iText 9.6, slf4j 2.0, MySQL connector)

### Setup

1. **Clone the repo**
   ```bash
   git clone https://github.com/[your-username]/DBS26IST2ndAF001.git
   cd DBS26IST2ndAF001
   ```

2. **Set up the database** (run in order)
   ```sql
   source Database/schema.sql
   source Database/views.sql
   source Database/procedures.sql
   source Database/triggers.sql
   source Database/Sample\ data.sql
   ```

3. **Configure DB connection**  
   Edit `src/software/DBConnection.java`:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/hms_db";
   private static final String USER = "root";
   private static final String PASSWORD = "your_password";
   ```

4. **Create required folders** in the project root (if not present):
   ```
   logs/
   reports/
   ```

5. **Add all JARs to IntelliJ**  
   File → Project Structure → Modules → Dependencies → + → JARs  
   Add everything from the `libs/` folder.

6. **Run the project**  
   Run `src/Main.java`  
   Default login: `admin` / `admin123`

---

## Notes

- Generated PDF reports are saved to the `reports/` folder in the project root, named `R##_YYYYMMDD_HHMM.pdf`
- The `logs/` folder must exist for the Logger to write; create it manually if missing
- Password comparison is plain-text for demo purposes — update the DB directly if you change passwords in `Sample data.sql` (the SQL file only runs at setup, not on rebuild)

---

> CSC-104L | Database Systems Lab | UET Lahore | Spring 2026
