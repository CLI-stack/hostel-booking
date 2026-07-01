# Hostel Booking System — Project Structure

## Overall Project Layout

This is a **Jakarta EE 10** multi-module Maven project with 3 sub-modules packaged together into an **EAR** (Enterprise Archive) deployed to **GlassFish 7**.

```
hostel-booking/                    ← Parent POM (aggregator)
├── HostelBookingSystem-ejb/       ← Business logic + database
├── HostelBookingSystem-war/       ← Web UI + managed beans
└── HostelBookingSystem-ear/       ← Packages EJB + WAR into one deployable
```

---

## 1. EAR — `HostelBookingSystem-ear`

**Packaging:** `.ear`  
**Purpose:** Bundles the EJB jar and WAR together into a single deployable unit for GlassFish.

| File | Purpose |
|------|---------|
| `pom.xml` | Declares both ejb and war as dependencies; uses `maven-ear-plugin` to assemble them |
| `src/main/application/META-INF/` | (Empty — EAR descriptor is auto-generated) |
| `target/META-INF/application.xml` | Auto-generated deployment descriptor listing the EJB and WAR modules |

The EAR itself contains **no Java code** — it is purely a packaging/deployment wrapper.

---

## 2. EJB — `HostelBookingSystem-ejb`

**Packaging:** `.jar` (EJB module)  
**Purpose:** The entire **backend** — JPA entities, DAOs, business services, and a web service.

### `entity/` — JPA Entities (database tables)

| File | Represents |
|------|-----------|
| `User.java` | Student/staff/admin accounts |
| `Room.java` | Hostel rooms |
| `Booking.java` | Room booking records |
| `Payment.java` | Payment transactions |
| `Complaint.java` | Student complaints |
| `MaintenanceRequest.java` | Room maintenance requests |
| `Notification.java` | In-app notifications |
| `CheckInOut.java` | Check-in/check-out records |
| `RegistrationPeriod.java` | Booking registration windows |

### `entity/enums/` — Enum Types Used by Entities

`UserRole`, `RoomType`, `RoomStatus`, `BookingStatus`, `PaymentStatus`, `ComplaintStatus`, `MaintenanceStatus`, `NotificationType`

### `dao/` — Data Access Objects (database queries)

| File | Manages |
|------|---------|
| `AbstractDAO.java` | Base DAO with generic CRUD |
| `UserDAO.java` | User queries |
| `RoomDAO.java` | Room queries |
| `BookingDAO.java` | Booking queries |
| `PaymentDAO.java` | Payment queries |
| `ComplaintDAO.java` | Complaint queries |
| `MaintenanceDAO.java` | Maintenance queries |
| `NotificationDAO.java` | Notification queries |
| `RegistrationPeriodDAO.java` | Registration period queries |

### `service/` — EJB Session Beans (business logic)

| File | Responsibility |
|------|---------------|
| `AuthService.java` | Login, logout, password reset |
| `BookingService.java` | Create/cancel bookings |
| `RoomService.java` | Room management |
| `PaymentService.java` | Payment processing |
| `ComplaintService.java` | Complaint handling |
| `MaintenanceService.java` | Maintenance workflow |
| `NotificationService.java` | Notification dispatch |
| `CheckInOutService.java` | Check-in/out logic |
| `RegistrationPeriodService.java` | Manage booking windows |
| `ReportService.java` | Admin reporting/statistics |

### `util/` — Utilities

| File | Purpose |
|------|---------|
| `PasswordUtil.java` | BCrypt password hashing |
| `DataInitializer.java` | Seeds initial data on startup |
| `SeedService.java` | Helper for seeding |

### `ws/` — Web Service (SOAP)

| File | Purpose |
|------|---------|
| `PaymentWebService.java` | SOAP interface definition |
| `PaymentWebServiceImpl.java` | SOAP implementation |

### Key Config

- `src/main/resources/META-INF/persistence.xml` — JPA config pointing to MySQL via `java:app/jdbc/hostelDB` using EclipseLink as the JPA provider

---

## 3. WAR — `HostelBookingSystem-war`

**Packaging:** `.war`  
**Purpose:** The entire **frontend** — JSF pages (`.xhtml`) and JSF managed beans.

### `bean/` — JSF Managed Beans (controllers)

| File | Handles |
|------|---------|
| `LoginBean.java` | Login form |
| `RegisterBean.java` | Student registration |
| `ForgotPasswordBean.java` | Password reset flow |
| `SessionBean.java` | Session/user state |
| `ProfileBean.java` | User profile editing |
| `BookingBean.java` | Student room booking |
| `RoomBrowseBean.java` | Student room browsing |
| `PaymentBean.java` | Student payment |
| `ComplaintBean.java` | Student complaints |
| `NotificationBean.java` | Student notifications |
| `StaffBookingBean.java` | Staff booking management |
| `MaintenanceBean.java` | Staff maintenance requests |
| `AdminUserListBean.java` | Admin user management |
| `AdminCreateUserBean.java` | Admin create user |
| `AdminRoomBean.java` | Admin room management |
| `AdminComplaintBean.java` | Admin complaint view |
| `AdminRegistrationPeriodBean.java` | Admin registration periods |
| `AdminReportBean.java` | Admin reports/statistics |

### `util/` — Web Utilities

| File | Purpose |
|------|---------|
| `AuthFilter.java` | Servlet filter — protects `/student/*`, `/staff/*`, `/admin/*` routes by role |

### `webapp/` — XHTML Pages (JSF Views)

**Public pages:**

| Page | Purpose |
|------|---------|
| `index.xhtml` | Landing page |
| `login.xhtml` | Login |
| `register.xhtml` | Student self-registration |
| `forgot-password.xhtml` | Password reset |
| `profile.xhtml` | User profile |

**`student/`** — Student portal:

| Page | Purpose |
|------|---------|
| `dashboard.xhtml` | Student home |
| `rooms.xhtml` | Browse available rooms |
| `my-bookings.xhtml` | View own bookings |
| `payment.xhtml` | Make payments |
| `complaints.xhtml` | Submit/view complaints |
| `notifications.xhtml` | View notifications |

**`staff/`** — Staff portal:

| Page | Purpose |
|------|---------|
| `dashboard.xhtml` | Staff home |
| `bookings.xhtml` | Manage bookings |
| `checkinout.xhtml` | Process check-in/out |
| `maintenance.xhtml` | Manage maintenance requests |

**`admin/`** — Admin portal:

| Page | Purpose |
|------|---------|
| `dashboard.xhtml` | Admin home |
| `users.xhtml` | Manage all users |
| `rooms.xhtml` | Manage all rooms |
| `bookings.xhtml` | View all bookings |
| `complaints.xhtml` | View all complaints |
| `registration-periods.xhtml` | Manage booking windows |
| `reports.xhtml` | View statistics/reports |

**`templates/`** — Shared layout:

- `layout.xhtml` — Master Facelets template (header/nav/footer used by all pages)

**`resources/css/`** — Stylesheets

### Key Config (`WEB-INF/`)

| File | Purpose |
|------|---------|
| `web.xml` | JSF FacesServlet mapping, `AuthFilter` configuration |
| `faces-config.xml` | JSF navigation rules for role-based post-login redirects |
| `beans.xml` | Enables CDI bean discovery (`mode="all"`) |
| `glassfish-web.xml` | Sets context root to `/hostel-booking` |

---

## Technology Stack

| Technology | Version | Role |
|-----------|---------|------|
| Java | 17 | Language |
| Jakarta EE | 10 | Platform |
| GlassFish | 7 | Application Server |
| JSF (Jakarta Faces) | 4.0 | UI Framework |
| PrimeFaces | 14 | UI Component Library |
| JPA (EclipseLink) | — | ORM / Persistence |
| MySQL | 8.x | Database |
| BCrypt (jbcrypt) | 0.4 | Password Hashing |
| Maven | — | Build Tool |

---

## How It All Fits Together

```
Browser → /hostel-booking (WAR)
              ↓
         JSF Pages (.xhtml)
              ↓
         Managed Beans (bean/)
              ↓  @EJB injection
         EJB Services (service/)
              ↓
         DAOs (dao/)
              ↓
         JPA Entities → MySQL DB
```

The **AuthFilter** guards role-specific routes based on the logged-in user's role (`STUDENT`, `STAFF`, `ADMIN`). The **EAR** deploys both modules under one application context so the WAR can inject EJBs from the EJB jar seamlessly via `@EJB` or `@Inject`.
