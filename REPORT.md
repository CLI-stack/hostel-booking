# SSW4353: COMPONENT-BASED SOFTWARE ENGINEERING
## GROUP ASSIGNMENT REPORT

---

**System Title:** Hostel Room Booking System
**Group:** Group 1
**Semester:** Semester 2, Session 2025/2026
**Lecturer:** Prof. Madya Dr. Novia Indriaty Admodisastro

| No. | Name | Matric No. |
|-----|------|------------|
| 1 | Nur Amalina Binti Babah | 227670 |
| 2 | Nureen Husna Binti Sharil | 227750 |
| 3 | Nur Alya Nabilah Binti Muhammad Adzuan | 228131 |
| 4 | Zeti Ellyana Binti Norman Yusof | 227912 |
| 5 | Wan Alia Kautsar Binti Wan Mohd Hazim | 227900 |

---

## Table of Contents

1. Project Background
2. System Analysis — Use Cases
3. System Detailed Design & Architecture
4. Component Descriptions
5. API and Third-Party Components
6. User Interface Design

---

---

# 1.0 PROJECT BACKGROUND

The **Hostel Room Booking System** is a web-based enterprise application developed to assist students and hostel management at Universiti Putra Malaysia (UPM) in managing hostel room reservations more effectively. In educational institutions, hostel management plays a crucial role in ensuring students are provided with comfortable and organised accommodation services. However, the current hostel room booking process at UPM is still handled manually — students are required to visit the hostel management office physically to submit booking forms, check room availability, and receive confirmation, which is time-consuming and difficult to manage, especially when involving a large number of students each semester.

The proposed system addresses this problem by providing a centralised online platform where students can browse available rooms, filter by room type and price, submit booking applications, and make payment online — all without leaving their residence. On the other side, hostel staff can process booking approvals or rejections, record student check-ins and check-outs, and log maintenance requests, while administrators can manage room availability, generate occupancy reports, handle complaints, and control registration periods from a single dashboard.

The system is developed using the **Jakarta EE 10 Framework** as a **Maven EAR (Enterprise Application Archive) multi-module project** and applies **Component-Based Software Engineering (CBSE)** principles. The architecture is divided into three Maven modules: `HostelBookingSystem-ejb` containing all backend business logic and persistence components, `HostelBookingSystem-war` containing the presentation layer, and `HostelBookingSystem-ear` which packages both into a single deployable EAR file for GlassFish 7.

Reusable software components — including a user authentication module, booking management service, payment verification web service, notification service, and data access objects — are designed to be modular and integrated across the system. This component reuse approach reduces development time, improves system consistency, simplifies maintenance, and ensures reliability and scalability for future enhancements.

---

---

# 2.0 SYSTEM ANALYSIS — USE CASES

## Actors
- **Student/Guest** — primary user who browses and books rooms
- **Hostel Staff** — manages bookings, check-ins, and maintenance
- **Admin** — full system management and reporting
- **Payment Verification Service** — external SOAP web service that verifies payments

---

## UC-01: Browse and Book a Room

| Field | Description |
|-------|-------------|
| **Use Case ID** | UC-01 |
| **Use Case Name** | Browse and Book a Room |
| **Actor** | Student |
| **Precondition** | Student is logged in. A registration period is currently active. |
| **Postcondition** | A booking is created with status PENDING. The room status changes to RESERVED. A notification is sent to the student. |
| **Main Flow** | 1. Student navigates to the Browse Rooms page. |
| | 2. System displays all available rooms with type, price, capacity, and amenities. |
| | 3. Student optionally filters rooms by type (Single, Double, Triple, Quad). |
| | 4. Student selects a room and clicks "Book This Room". |
| | 5. `BookingService` validates: (a) registration period is open, (b) student has no existing active booking, (c) room has no active booking. |
| | 6. System creates a new booking with status PENDING and updates room to RESERVED. |
| | 7. `NotificationService` sends a push notification to the student. |
| | 8. System displays a success message. |
| **Alternative Flow** | 5a. If no active registration period exists → error: "Booking is not open." |
| | 5b. If student already has an active booking → error: "You already have an active booking." |
| | 5c. If room is already reserved/booked → error: "Room is not available." |

---

## UC-02: Approve or Reject a Booking

| Field | Description |
|-------|-------------|
| **Use Case ID** | UC-02 |
| **Use Case Name** | Approve or Reject a Booking |
| **Actor** | Hostel Staff / Admin |
| **Precondition** | Staff is logged in. At least one booking exists with status PENDING. |
| **Postcondition** | Booking status is updated to APPROVED or REJECTED. Room and student are notified. |
| **Main Flow** | 1. Staff navigates to the Manage Bookings page. |
| | 2. System displays all pending bookings with student and room details. |
| | 3. Staff clicks "Approve" on a booking. |
| | 4. `BookingService.approveBooking()` updates booking status to APPROVED and room to OCCUPIED. |
| | 5. `NotificationService` sends approval notification to the student. |
| **Alternative Flow** | 3a. Staff clicks "Reject" and enters a rejection reason. |
| | 3b. `BookingService.rejectBooking()` sets status to REJECTED and room back to AVAILABLE. |
| | 3c. Notification sent to student with the rejection reason. |

---

## UC-03: Record Student Check-In and Check-Out

| Field | Description |
|-------|-------------|
| **Use Case ID** | UC-03 |
| **Use Case Name** | Record Student Check-In and Check-Out |
| **Actor** | Hostel Staff |
| **Precondition** | Staff is logged in. Booking is APPROVED (check-in) or CHECKED_IN (check-out). |
| **Postcondition** | A `CheckInOut` record is created. Booking and room status are updated. |
| **Main Flow — Check In** | 1. Staff goes to the Check In/Out page. |
| | 2. System displays approved bookings pending check-in. |
| | 3. Staff selects a booking, enters room condition, clicks "Check In". |
| | 4. `CheckInOutService.checkIn()` creates a record with timestamp and updates booking to CHECKED_IN. |
| | 5. `NotificationService` notifies the student. |
| **Main Flow — Check Out** | 1. Staff finds student in "Currently Checked In" list. |
| | 2. Staff enters room condition notes and clicks "Check Out". |
| | 3. `CheckInOutService.checkOut()` records checkout time, updates booking to CHECKED_OUT, room to AVAILABLE. |
| **Alternative Flow** | If booking is not in the required status, the action is rejected with an error. |

---

## UC-04: Make Payment via JAX-WS Payment Web Service

| Field | Description |
|-------|-------------|
| **Use Case ID** | UC-04 |
| **Use Case Name** | Make Payment via Payment Web Service |
| **Actor** | Student, Payment Verification Service (External SOAP WS) |
| **Precondition** | Student has a booking with status APPROVED. |
| **Postcondition** | A payment record exists. Status is VERIFIED or FAILED. |
| **Main Flow** | 1. Student navigates to the Payment page. |
| | 2. System displays approved bookings pending payment. |
| | 3. Student selects a booking and chooses a payment method (FPX, Credit Card, Debit Card). |
| | 4. Student clicks "Initiate Payment". |
| | 5. `PaymentService.initiatePayment()` creates a payment record with a unique transaction ID and status PENDING. |
| | 6. Student clicks "Verify Payment (via Web Service)". |
| | 7. `PaymentService.processAndVerifyPayment()` calls **`PaymentWebServiceImpl.verifyPayment(transactionId, amount)`** — a Stateless EJB published as JAX-WS SOAP Web Service. |
| | 8. Web Service validates the transaction and returns `true`. |
| | 9. Payment status is set to VERIFIED. Notification sent to student. |
| **Alternative Flow** | 8a. Web Service returns `false` → payment status set to FAILED → failure notification sent. |
| **Security Note** | Payment information is transmitted via the JAX-WS endpoint secured with SSL/TLS in production. |

---

## UC-05: Submit and Manage a Complaint

| Field | Description |
|-------|-------------|
| **Use Case ID** | UC-05 |
| **Use Case Name** | Submit and Manage a Complaint |
| **Actor** | Student (submits), Admin (responds) |
| **Precondition** | Student or Admin is logged in. |
| **Postcondition** | Complaint record is created and/or updated. Student is notified of admin response. |
| **Main Flow — Student** | 1. Student goes to the Complaints page. |
| | 2. Student enters subject and description and clicks "Submit Complaint". |
| | 3. `ComplaintService.submitComplaint()` creates a complaint with status OPEN. |
| **Main Flow — Admin** | 1. Admin goes to Manage Complaints. |
| | 2. System shows all complaints with status. |
| | 3. Admin clicks "Respond", enters response text, selects new status. |
| | 4. `ComplaintService.respondToComplaint()` saves response and updates status. |
| | 5. `NotificationService` sends response notification to the student. |

---

---

# 3.0 SYSTEM DETAILED DESIGN & ARCHITECTURE

## 3.1 Application Architecture

### Physical View — Three-Tier Architecture

```
 ┌──────────────────────────────────────────────────────────────────────────┐
 │                            CLIENT TIER                                   │
 │                     Web Browser (HTTP Requests)                          │
 └────────────────────────────────┬─────────────────────────────────────────┘
                                  │ HTTP / HTTPS
 ┌────────────────────────────────▼─────────────────────────────────────────┐
 │                      APPLICATION SERVER TIER                             │
 │                    GlassFish 7.0.25 (Jakarta EE 10)                      │
 │  ┌───────────────────────────────────────────────────────────────────┐   │
 │  │                  HostelBookingSystem.ear                          │   │
 │  │  ┌──────────────────────────┐  ┌────────────────────────────┐    │   │
 │  │  │  HostelBookingSystem-war │  │  HostelBookingSystem-ejb   │    │   │
 │  │  │  Jakarta Faces 4.0       │  │  EJB 4.0 Services          │    │   │
 │  │  │  PrimeFaces 14           │  │  JPA 3.1 Entities          │    │   │
 │  │  │  CDI Beans               │  │  JAX-WS Web Service        │    │   │
 │  │  │  AuthFilter              │  │  DAOs + Utilities          │    │   │
 │  │  └──────────────────────────┘  └────────────────────────────┘    │   │
 │  └───────────────────────────────────────────────────────────────────┘   │
 └────────────────────────────────┬─────────────────────────────────────────┘
                                  │ JDBC
 ┌────────────────────────────────▼─────────────────────────────────────────┐
 │                           DATABASE TIER                                  │
 │                          MySQL 8.x                                        │
 └──────────────────────────────────────────────────────────────────────────┘
```

### Development View — Layered Architecture

```
 ┌─────────────────────────────────────────────────────────────────────┐
 │  PRESENTATION LAYER                  [HostelBookingSystem-war]      │
 │  • Jakarta Faces 4.0 XHTML pages (.xhtml)                           │
 │  • PrimeFaces 14 UI components                                      │
 │  • Facelets templating (layout.xhtml master template)               │
 ├─────────────────────────────────────────────────────────────────────┤
 │  CONTROLLER LAYER                    [HostelBookingSystem-war]      │
 │  • CDI Managed Beans (@Named, @ViewScoped, @SessionScoped)          │
 │  • Handle UI events, invoke services, return navigation outcomes    │
 │  • AuthFilter — role-based page access control                      │
 ├─────────────────────────────────────────────────────────────────────┤
 │  BUSINESS / SERVICE LAYER            [HostelBookingSystem-ejb]      │
 │  • Stateless EJBs (@Stateless) — business rules, transactions       │
 │  • Singleton EJB (@Singleton @Startup) — startup & datasource       │
 │  • JAX-WS PaymentWebService — published as SOAP Web Service         │
 ├─────────────────────────────────────────────────────────────────────┤
 │  DATA ACCESS LAYER                   [HostelBookingSystem-ejb]      │
 │  • DAO pattern — AbstractDAO<T,ID> generic base class               │
 │  • Stateless EJBs with @PersistenceContext EntityManager            │
 │  • Named JPQL queries per entity                                    │
 ├─────────────────────────────────────────────────────────────────────┤
 │  DATA LAYER                          [HostelBookingSystem-ejb]      │
 │  • JPA 3.1 Entities — mapped to database tables                     │
 │  • Enumerations — typed constants for all status fields             │
 │  • EclipseLink — auto DDL generation (create-or-extend-tables)      │
 └─────────────────────────────────────────────────────────────────────┘
                          ↕ packaged into ↕
 ┌─────────────────────────────────────────────────────────────────────┐
 │  DEPLOYMENT UNIT                     [HostelBookingSystem-ear]      │
 │  • HostelBookingSystem-ear-1.0.ear                                  │
 │    ├── HostelBookingSystem-ejb.jar   (67 KB)                        │
 │    ├── HostelBookingSystem-war.war   (4.2 MB)                       │
 │    └── lib/ (jBCrypt)               (2.6 MB)                       │
 └─────────────────────────────────────────────────────────────────────┘
```

---

## 3.2 Class Diagram (Key Entities & Relationships)

```
 ┌──────────────────┐         ┌──────────────────────┐
 │      User        │         │   RegistrationPeriod │
 │──────────────────│         │──────────────────────│
 │ id: Long         │         │ id: Long             │
 │ username: String │         │ name: String         │
 │ password: String │         │ startDate: LocalDate │
 │ fullName: String │         │ endDate: LocalDate   │
 │ email: String    │         │ semester: String     │
 │ matricNumber:Str │         │ academicYear: String │
 │ role: UserRole   │         │ active: boolean      │
 │ active: boolean  │         └──────────┬───────────┘
 └────────┬─────────┘                    │ 1
          │ 1                            │
          │ *                            │ *
          └──────────┐       ┌───────────▼─────────────┐
                     │       │         Booking          │
                     └──────▶│─────────────────────────│
                             │ id: Long                 │
 ┌──────────────────┐   1    │ student: User            │
 │      Room        │◀───────│ room: Room               │
 │──────────────────│        │ registrationPeriod: Reg  │
 │ id: Long         │        │ bookingDate: LDT         │
 │ roomNumber:Str   │        │ status: BookingStatus    │
 │ type: RoomType   │        │ rejectionReason: String  │
 │ capacity: int    │        └────────┬────────────────┘
 │ pricePerSemester │                 │ 1
 │ status:RoomStatus│       ┌─────────┴──────────────────────┐
 │ block: String    │       │           │                    │
 │ hasWifi: boolean │       ▼           ▼                    ▼
 │ hasAc: boolean   │  ┌──────────┐ ┌───────────┐ ┌─────────────┐
 └──────────────────┘  │ Payment  │ │ CheckInOut│ │  Complaint  │
                       │──────────│ │───────────│ │─────────────│
                       │amount:BD │ │checkIn:LDT│ │subject:Str  │
                       │status:PS │ │checkOut:  │ │description: │
                       │txnId:Str │ │  LDT      │ │  String     │
                       │method:Str│ │checkInBy: │ │status: CS   │
                       └──────────┘ │  User     │ │adminResp:Str│
                                    └───────────┘ └─────────────┘

 ┌────────────────────────────────┐   ┌─────────────────────────┐
 │      MaintenanceRequest        │   │      Notification       │
 │────────────────────────────────│   │─────────────────────────│
 │ room: Room                     │   │ user: User              │
 │ reportedBy: User               │   │ title: String           │
 │ issueDescription: String       │   │ message: String         │
 │ category: String               │   │ type: NotificationType  │
 │ status: MaintenanceStatus      │   │ read: boolean           │
 │ assignedTo: User               │   │ sentAt: LocalDateTime   │
 └────────────────────────────────┘   └─────────────────────────┘
```

---

## 3.3 Sequence Diagram — Book a Room

```
 Student      RoomBrowseBean    BookingService      BookingDAO     RoomDAO  NotificationService
    │               │                │                   │              │           │
    │─ browse ─────▶│                │                   │              │           │
    │               │─ getAvail() ──▶│                   │              │           │
    │               │                │─ findAvailable() ─▶│             │           │
    │               │                │◀── List<Room> ─────│             │           │
    │◀── rooms ─────│                │                   │              │           │
    │               │                │                   │              │           │
    │─ click Book ─▶│                │                   │              │           │
    │               │─ submitBooking()▶                  │              │           │
    │               │                │─ findActivePeriod()              │           │
    │               │                │─ studentHasActiveBooking()       │           │
    │               │                │─ roomHasActiveBooking()          │           │
    │               │                │─ save(booking) ───▶│             │           │
    │               │                │─ updateStatus(RESERVED) ─────────▶           │
    │               │                │─ sendToUser() ────────────────────────────────▶
    │◀── success ───│                │                   │              │           │
```

---

## 3.4 Sequence Diagram — Payment Verification (JAX-WS)

```
 Student     PaymentBean      PaymentService    PaymentWebServiceImpl   PaymentDAO
    │              │                │                    │                   │
    │─ initiate ──▶│                │                    │                   │
    │              │─ initiatePayment()▶                 │                   │
    │              │                │─ save(payment) ────────────────────────▶
    │◀── txn ID ───│                │                    │                   │
    │              │                │                    │                   │
    │─ verify ────▶│                │                    │                   │
    │              │─ processAndVerify()▶                │                   │
    │              │                │─ verifyPayment(txnId, amount) ─────────▶
    │              │                │                    │ [validate]        │
    │              │                │◀─ true/false ───────│                   │
    │              │                │─ update status ────────────────────────▶
    │              │                │─ sendNotification()│                   │
    │◀── result ───│                │                    │                   │
```

---

## 3.5 Component Diagram

```
 ┌───────────────────────────────────────────────────────────────────────┐
 │                    HostelBookingSystem.ear                            │
 │                                                                       │
 │  ┌──────────────────────────────┐  ┌──────────────────────────────┐  │
 │  │  HostelBookingSystem-war     │  │  HostelBookingSystem-ejb     │  │
 │  │──────────────────────────────│  │──────────────────────────────│  │
 │  │ Presentation Component       │  │ Business Layer Component      │  │
 │  │  • Jakarta Faces Pages       │──▶  • AuthService               │  │
 │  │  • CDI Managed Beans         │  │  • BookingService             │  │
 │  │  • Facelets Templates        │  │  • RoomService                │  │
 │  │  • PrimeFaces 14 UI          │  │  • PaymentService             │  │
 │  │                              │  │  • ComplaintService           │  │
 │  │ Filter Component             │  │  • MaintenanceService         │  │
 │  │  • AuthFilter (Servlet)      │  │  • NotificationService        │  │
 │  │                              │  │  • CheckInOutService          │  │
 │  └──────────────────────────────┘  │  • ReportService              │  │
 │                                    │  • RegistrationPeriodService  │  │
 │                                    │──────────────────────────────│  │
 │                                    │ Web Service Component         │  │
 │                                    │  • PaymentWebService (JAX-WS) │  │
 │                                    │──────────────────────────────│  │
 │                                    │ Data Access Component         │  │
 │                                    │  • AbstractDAO<T,ID>          │  │
 │                                    │  • UserDAO, RoomDAO           │  │
 │                                    │  • BookingDAO, PaymentDAO     │  │
 │                                    │  • ComplaintDAO               │  │
 │                                    │  • MaintenanceDAO             │  │
 │                                    │  • NotificationDAO            │  │
 │                                    │  • RegistrationPeriodDAO      │  │
 │                                    │──────────────────────────────│  │
 │                                    │ Entity Component              │  │
 │                                    │  • 9 JPA Entities + 8 Enums  │  │
 │                                    │──────────────────────────────│  │
 │                                    │ Utility Component             │  │
 │                                    │  • DataInitializer (@Singleton│  │
 │                                    │  • PasswordUtil (BCrypt)      │  │
 │                                    └──────────────┬───────────────┘  │
 └────────────────────────────────────────────────────┼──────────────────┘
                                                      │ JPA 3.1 / JDBC
                                             ┌────────▼──────────┐
                                             │   MySQL 8.x DB    │
                                             └───────────────────┘
```

---

## 3.6 Deployment Diagram

```
 ┌──────────────────────────────────────────────────────────────────┐
 │              Student / Staff / Admin Workstation                 │
 │  ┌──────────────────────────────────────────────────────────┐   │
 │  │               Web Browser                               │   │
 │  │   http://localhost:8080/hostel-booking/login.xhtml       │   │
 │  └──────────────────────────────────────────────────────────┘   │
 └─────────────────────────────┬────────────────────────────────────┘
                               │ HTTP / HTTPS
 ┌─────────────────────────────▼────────────────────────────────────┐
 │              Application Server Node                             │
 │              GlassFish 7.0.25 — Jakarta EE 10 — JDK 18          │
 │  ┌────────────────────────────────────────────────────────────┐  │
 │  │             HostelBookingSystem.ear                        │  │
 │  │  ┌────────────────────┐  ┌──────────────────────────────┐ │  │
 │  │  │ -war.war           │  │ -ejb.jar                     │ │  │
 │  │  │ Jakarta Faces 4.0  │  │ EJB 4.0 (@Stateless)         │ │  │
 │  │  │ PrimeFaces 14      │  │ JPA 3.1 (EclipseLink)        │ │  │
 │  │  │ CDI 4.0 Beans      │  │ JAX-WS PaymentWebService     │ │  │
 │  │  │ Servlet 6.0 Filter │  │ @Singleton DataInitializer   │ │  │
 │  │  └────────────────────┘  └──────────────────────────────┘ │  │
 │  │  lib/ jbcrypt-0.4.jar                                      │  │
 │  └────────────────────────────────────────────────────────────┘  │
 └─────────────────────────────┬────────────────────────────────────┘
                               │ JDBC
 ┌─────────────────────────────▼────────────────────────────────────┐
 │              Database Server Node                                │
 │                    MySQL 8.x                                    │
 │   Tables: users, rooms, bookings, payments,                     │
 │           complaints, maintenance_requests,                     │
 │           check_in_out, notifications,                          │
 │           registration_periods                                  │
 └──────────────────────────────────────────────────────────────────┘
```

---

## 3.7 Design Patterns Applied

| Pattern | Where Applied | Description |
|---------|--------------|-------------|
| **Session Facade** | All 10 EJB Services | Each service acts as a facade hiding DAO complexity from JSF beans |
| **DAO (Data Access Object)** | `AbstractDAO<T,ID>` + 9 DAO classes | Generic reusable base DAO; subclasses add entity-specific queries |
| **MVC (Model-View-Controller)** | Full system | Jakarta Faces (View) + CDI Beans (Controller) + EJBs/Entities (Model) |
| **Singleton** | `DataInitializer` (`@Singleton @Startup`) | Single instance that runs once on startup to seed data and define the datasource |
| **Stateless** | All EJB Services and DAOs (`@Stateless`) | No state retained between client calls — scalable and pool-able |

---

---

# 4.0 COMPONENT DESCRIPTIONS

## Component 1: AuthService

| Field | Description |
|-------|-------------|
| **Component Name** | AuthService |
| **Type** | Stateless EJB (`@Stateless`) |
| **Module** | HostelBookingSystem-ejb |
| **Package** | `com.hostel.service` |
| **Purpose** | Handles user authentication using BCrypt password verification, student self-registration, and role-based navigation redirection using Java 18 switch expressions. |
| **Key Methods** | `authenticate(username, password)` → `Optional<User>` |
| | `register(username, password, fullName, email, phone, matric)` → `User` |
| | `changePassword(userId, current, newPassword)` → `void` |
| | `getRedirectForRole(role)` → `String` |
| **Dependencies** | `UserDAO`, `PasswordUtil` |

---

## Component 2: BookingService

| Field | Description |
|-------|-------------|
| **Component Name** | BookingService |
| **Type** | Stateless EJB (`@Stateless`) |
| **Module** | HostelBookingSystem-ejb |
| **Package** | `com.hostel.service` |
| **Purpose** | Core booking business logic. Enforces all constraints: active registration period check, one-active-booking-per-student rule, duplicate room booking prevention. Manages the full booking lifecycle: PENDING → APPROVED/REJECTED → CHECKED_IN → CHECKED_OUT. |
| **Key Methods** | `submitBooking(studentId, roomId)` → `Booking` |
| | `approveBooking(bookingId, staffId)` → `Booking` |
| | `rejectBooking(bookingId, staffId, reason)` → `Booking` |
| | `cancelBooking(bookingId, studentId)` → `Booking` |
| | `getAllBookings()` → `List<Booking>` |
| **Dependencies** | `BookingDAO`, `RoomDAO`, `UserDAO`, `RegistrationPeriodDAO`, `NotificationService` |

---

## Component 3: PaymentService

| Field | Description |
|-------|-------------|
| **Component Name** | PaymentService |
| **Type** | Stateless EJB (`@Stateless`) |
| **Module** | HostelBookingSystem-ejb |
| **Package** | `com.hostel.service` |
| **Purpose** | Manages the full payment lifecycle — initiation, external SOAP verification via `PaymentWebServiceImpl`, and refund processing. Generates unique transaction IDs. |
| **Key Methods** | `initiatePayment(bookingId, method)` → `Payment` |
| | `processAndVerifyPayment(paymentId)` → `Payment` |
| | `refundPayment(paymentId, reason)` → `Payment` |
| | `getTotalRevenue()` → `BigDecimal` |
| **Dependencies** | `PaymentDAO`, `BookingDAO`, `NotificationService`, `PaymentWebServiceImpl` |

---

## Component 4: PaymentWebService (JAX-WS)

| Field | Description |
|-------|-------------|
| **Component Name** | PaymentWebService / PaymentWebServiceImpl |
| **Type** | Stateless EJB + JAX-WS Web Service (`@Stateless @WebService`) |
| **Module** | HostelBookingSystem-ejb |
| **Package** | `com.hostel.ws` |
| **Purpose** | A Stateless Session Bean published as a SOAP Web Service. Represents the external Payment Verification Service. Validates transaction IDs and amounts. In production this would connect to a real payment gateway (FPX, Stripe) over HTTPS. |
| **Key Methods** | `verifyPayment(transactionId, amount)` → `boolean` |
| | `getTransactionStatus(transactionId)` → `String` |
| **WSDL URL** | `http://localhost:8080/hostel-booking/PaymentWebServiceImplService?wsdl` |
| **Dependencies** | None (standalone service) |

---

## Component 5: AbstractDAO + DAO Classes

| Field | Description |
|-------|-------------|
| **Component Name** | `AbstractDAO<T, ID>` |
| **Type** | Abstract Stateless EJB base class |
| **Module** | HostelBookingSystem-ejb |
| **Package** | `com.hostel.dao` |
| **Purpose** | Generic reusable DAO base providing CRUD operations via JPA `EntityManager`. All 9 DAO classes extend this. Demonstrates the DAO design pattern and component reuse. |
| **Key Methods** | `save(entity)` → T |
| | `update(entity)` → T |
| | `delete(entity)` → void |
| | `findById(id)` → `Optional<T>` |
| | `findAll()` → `List<T>` |
| | `count()` → `long` |
| **Subclasses** | `UserDAO`, `RoomDAO`, `BookingDAO`, `PaymentDAO`, `ComplaintDAO`, `MaintenanceDAO`, `NotificationDAO`, `RegistrationPeriodDAO`, `CheckInOutDAO` |
| **Dependencies** | JPA `EntityManager` (`@PersistenceContext`) |

---

## Component 6: NotificationService

| Field | Description |
|-------|-------------|
| **Component Name** | NotificationService |
| **Type** | Stateless EJB (`@Stateless`) |
| **Module** | HostelBookingSystem-ejb |
| **Package** | `com.hostel.service` |
| **Purpose** | Reusable notification component injected by all other services. Sends push, email, and SMS notifications to users. Provides read/unread management. Demonstrates component reuse across `BookingService`, `PaymentService`, `ComplaintService`, and `CheckInOutService`. |
| **Key Methods** | `sendToUser(user, title, message)` → `void` |
| | `getUserNotifications(userId)` → `List<Notification>` |
| | `countUnread(userId)` → `long` |
| | `markAsRead(notificationId)` → `void` |
| **Dependencies** | `NotificationDAO` |

---

## Component 7: DataInitializer

| Field | Description |
|-------|-------------|
| **Component Name** | DataInitializer |
| **Type** | Singleton EJB (`@Singleton @Startup`) |
| **Module** | HostelBookingSystem-ejb |
| **Package** | `com.hostel.util` |
| **Purpose** | Implements the **Singleton pattern**. Runs exactly once when GlassFish deploys the EAR. Seeds demo users, 10 rooms across two blocks, and an active registration period. |
| **Key Methods** | `init()` → `void` (via `@PostConstruct`) |
| **Dependencies** | `UserDAO`, `RoomDAO`, `RegistrationPeriodDAO`, `PasswordUtil` |

---

## Component 8: AuthFilter

| Field | Description |
|-------|-------------|
| **Component Name** | AuthFilter |
| **Type** | Jakarta Servlet Filter (`jakarta.servlet.Filter`) |
| **Module** | HostelBookingSystem-war |
| **Package** | `com.hostel.util` |
| **Purpose** | Enforces role-based access control on all protected URL patterns. Redirects unauthenticated users to login. Validates that the user's role matches the page area (`/student/*`, `/staff/*`, `/admin/*`). |
| **Key Methods** | `doFilter(request, response, chain)` → `void` |
| **URL Patterns** | `/student/*`, `/staff/*`, `/admin/*` |
| **Dependencies** | `HttpSession`, `UserRole` enum |

---

## Component 9: SessionBean

| Field | Description |
|-------|-------------|
| **Component Name** | SessionBean |
| **Type** | CDI Session-Scoped Bean (`@SessionScoped @Named`) |
| **Module** | HostelBookingSystem-war |
| **Package** | `com.hostel.bean` |
| **Purpose** | Session-scoped CDI bean available to every XHTML page via EL expressions. Provides the currently logged-in user, role-check helpers (`isStudent`, `isStaff`, `isAdmin`), and unread notification count to the layout template. |
| **Key Methods** | `getLoggedInUser()` → `User` |
| | `getUnreadNotificationCount()` → `long` |
| | `isStudent()` / `isStaff()` / `isAdmin()` → `boolean` |
| **Dependencies** | `HttpSession`, `NotificationService` |

---

---

# 5.0 API AND THIRD-PARTY COMPONENTS

## 5.1 Jakarta EE 10 Platform APIs

| API | Version | Purpose | Source |
|-----|---------|---------|--------|
| **Jakarta Faces (JSF)** | 4.0 | Frontend MVC framework — XHTML pages with new `jakarta.faces.*` namespace | Bundled with GlassFish 7 |
| **Enterprise JavaBeans (EJB)** | 4.0 | Server-side business components with container-managed transactions | Bundled with GlassFish 7 |
| **Jakarta Persistence (JPA)** | 3.1 | ORM framework for mapping Java entities to database tables | Bundled with GlassFish 7 |
| **Jakarta XML Web Services (JAX-WS)** | 4.0 | SOAP web services — used for Payment Verification Web Service | Bundled with GlassFish 7 / Metro |
| **CDI (Contexts and Dependency Injection)** | 4.0 | Dependency injection, scoped beans, interceptors | Bundled with GlassFish 7 |
| **Bean Validation** | 3.0 | Annotations: `@NotBlank`, `@Email`, `@NotNull`, `@Min`, `@DecimalMin` | Bundled with GlassFish 7 |
| **Jakarta Servlet** | 6.0 | `AuthFilter` for role-based page protection | Bundled with GlassFish 7 |

---

## 5.2 Third-Party Libraries

| Library | Version | Purpose | Source / Link | Alternative |
|---------|---------|---------|---------------|-------------|
| **PrimeFaces** | 14.0.0 (jakarta) | Rich UI component library for Jakarta Faces 4.0: `p:dataTable`, `p:dialog`, `p:tag`, `p:commandButton`, `p:panelMenu`, `p:datePicker`, etc. | [https://www.primefaces.org](https://www.primefaces.org) | OmniFaces, RichFaces |
| **MySQL Connector/J** | 9.7.0 | JDBC driver for MySQL 8+ database connectivity | [https://dev.mysql.com/downloads/connector/j](https://dev.mysql.com/downloads/connector/j/) | MariaDB Connector |
| **jBCrypt** | 0.4 | BCrypt password hashing — `PasswordUtil.hash()` and `PasswordUtil.verify()` | [https://www.mindrot.org/projects/jBCrypt](https://www.mindrot.org/projects/jBCrypt) | Spring Security Crypto |

---

## 5.3 Application Server & Build Tools

| Component | Version | Purpose | Source |
|-----------|---------|---------|--------|
| **GlassFish** | 7.0.25 | Jakarta EE 10 reference implementation application server | [https://glassfish.org](https://glassfish.org) |
| **EclipseLink** | (bundled with GF7) | JPA 3.1 persistence provider — DDL auto-generation, JPQL execution | Bundled with GlassFish 7 |
| **Mojarra** | (bundled with GF7) | Jakarta Faces 4.0 reference implementation | Bundled with GlassFish 7 |
| **Metro (JAX-WS RI)** | (bundled with GF7) | JAX-WS 4.0 SOAP web service runtime | Bundled with GlassFish 7 |
| **Apache Maven** | 3.9.6 | Multi-module EAR build and dependency management | [https://maven.apache.org](https://maven.apache.org) |
| **JDK** | 18 | Java platform — enables switch expressions, `isBlank()`, `toList()`, `isEmpty()` | [https://openjdk.org](https://openjdk.org) |

---

---

# 6.0 USER INTERFACE DESIGN

## 6.1 Login Page

```
 ┌─────────────────────────────────────────────────────┐
 │                                                     │
 │           🏢 Hostel Room Booking System             │
 │              Universiti Putra Malaysia              │
 │                                                     │
 │   ┌─────────────────────────────────────────────┐   │
 │   │  Username: [_____________________________]  │   │
 │   │  Password: [_____________________________]  │   │
 │   │         [        LOGIN        ]             │   │
 │   │   Don't have an account? Register here      │   │
 │   │   Demo: admin/Admin@123 | staff1/Staff@123  │   │
 │   └─────────────────────────────────────────────┘   │
 └─────────────────────────────────────────────────────┘
```
**Description:** Centred card on a blue gradient background. PrimeFaces `p:inputText`, `p:password`, and `p:commandButton` components.

---

## 6.2 Student Dashboard

```
 ┌──────────────┬──────────────────────────────────────────────────────────┐
 │  Student Menu│  Student Dashboard                                       │
 │  ──────────  │  ┌──────────────────────────────────────────────────┐   │
 │  Dashboard   │  │ Welcome, Nur Amalina! | Matric: 227670           │   │
 │  Browse Rooms│  └──────────────────────────────────────────────────┘   │
 │  My Bookings │  ┌───────────┐  ┌───────────┐  ┌───────────┐           │
 │  Payment     │  │📋 2       │  │🔔 3 Unread│  │💬 1       │           │
 │  Complaints  │  │ Bookings  │  │           │  │ Complaints│           │
 │  Notifications│ └───────────┘  └───────────┘  └───────────┘           │
 │              │  Recent Bookings (p:dataTable)                          │
 │              │  ID | Room  | Status    | Date     | Action             │
 │              │   1 | A101  | APPROVED  | 01-06-26 | [Pay]              │
 │              │   2 | A201  | PENDING   | 05-06-26 | [Cancel]           │
 └──────────────┴──────────────────────────────────────────────────────────┘
```
**Description:** Role-aware sidebar navigation, KPI stat cards, and recent bookings table using `p:dataTable` with status badges via `p:tag`.

---

## 6.3 Browse Rooms Page (Student)

```
 ┌──────────────┬──────────────────────────────────────────────────────────┐
 │  Student Menu│  Browse Available Rooms                                  │
 │              │  Filter: [All Types ▼]  [Filter]                        │
 │              │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐    │
 │              │  │🚪 Room A101  │ │🚪 Room A102  │ │🚪 Room B101  │    │
 │              │  │ Block A | Fl1│ │ Block A | Fl1│ │ Block B | Fl1│    │
 │              │  │ SINGLE       │ │ SINGLE       │ │ SINGLE       │    │
 │              │  │ RM 550/sem   │ │ RM 550/sem   │ │ RM 600/sem   │    │
 │              │  │ 📶WiFi       │ │ 📶WiFi       │ │ 📶WiFi ❄AC   │    │
 │              │  │[AVAILABLE]   │ │[AVAILABLE]   │ │[AVAILABLE]   │    │
 │              │  │[Book Room]   │ │[Book Room]   │ │[Book Room]   │    │
 │              │  └──────────────┘ └──────────────┘ └──────────────┘    │
 └──────────────┴──────────────────────────────────────────────────────────┘
```
**Description:** Rooms shown as a 3-column `p:dataGrid` with cards. Filter by type using `p:selectOneMenu`.

---

## 6.4 Staff Booking Management

```
 ┌──────────────┬──────────────────────────────────────────────────────────┐
 │  Staff Menu  │  Manage Bookings                                         │
 │              │  Pending Bookings                                        │
 │              │  Student       | Room   | Date      | Actions           │
 │              │  Amalina(227670)| A101  | 01-06-26  |[✓Approve][✗Reject]│
 │              │  Nureen (227750)| A201  | 02-06-26  |[✓Approve][✗Reject]│
 │              │                                                          │
 │              │  ┌─────────────────────────────────────────┐            │
 │              │  │ Reject Booking — p:dialog               │            │
 │              │  │ Reason: [p:inputTextarea______________]  │            │
 │              │  │         [    Confirm Reject    ]         │            │
 │              │  └─────────────────────────────────────────┘            │
 └──────────────┴──────────────────────────────────────────────────────────┘
```
**Description:** Pending bookings table with approve/reject buttons. Rejection opens a `p:dialog` modal for entering the reason.

---

## 6.5 Admin Dashboard

```
 ┌──────────────┬──────────────────────────────────────────────────────────┐
 │  Admin Menu  │  Admin Dashboard                                         │
 │              │  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐│
 │              │  │ 10   │ │  7   │ │  3   │ │  15  │ │  4   │ │RM   ││
 │              │  │Rooms │ │Avail │ │Occup │ │Books │ │Pend  │ │3,450││
 │              │  └──────┘ └──────┘ └──────┘ └──────┘ └──────┘ └──────┘│
 │              │  Occupancy Rate:                                         │
 │              │  [████████████░░░░░░░░░░░░░░░░░░░░░░░░] 30%            │
 │              │  [Rooms] [Bookings] [Reports] [Complaints] [Reg.Periods]│
 └──────────────┴──────────────────────────────────────────────────────────┘
```
**Description:** Six KPI stat cards, occupancy rate `p:progressBar`, and quick navigation buttons.

---

## 6.6 Payment Page (Student)

```
 ┌──────────────┬──────────────────────────────────────────────────────────┐
 │  Student Menu│  Payment                                                 │
 │              │  Step 1: Select Approved Booking                         │
 │              │  Room A101 (SINGLE) | RM 550.00 | [Select]              │
 │              │                                                          │
 │              │  Step 2: Choose Payment Method                           │
 │              │  [FPX (Online Banking) ▼]  [Initiate Payment]           │
 │              │                                                          │
 │              │  Step 3: Verify via JAX-WS Web Service                  │
 │              │  Transaction ID : ABC123DEF456                           │
 │              │  Amount         : RM 550.00                              │
 │              │  Status         : [PENDING]                              │
 │              │  [Verify Payment (via Web Service)]                     │
 └──────────────┴──────────────────────────────────────────────────────────┘
```
**Description:** A 3-step payment flow. Step 3 triggers `PaymentService → PaymentWebServiceImpl.verifyPayment()` — a Stateless EJB published as a SOAP Web Service.

---

*End of Report*
