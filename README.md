# Hostel Room Booking System

**Course:** SSW4353 — Component-Based Software Engineering
**Group:** Group 1 | Semester 2, 2025/2026
**University:** Universiti Putra Malaysia (UPM)
**Lecturer:** Prof. Madya Dr. Novia Indriaty Admodisastro

---

## Team Members

| No. | Name | Matric No. |
|-----|------|------------|
| 1 | Nur Amalina Binti Babah | 227670 |
| 2 | Nureen Husna Binti Sharil | 227750 |
| 3 | Nur Alya Nabilah Binti Muhammad Adzuan | 228131 |
| 4 | Zeti Ellyana Binti Norman Yusof | 227912 |
| 5 | Wan Alia Kautsar Binti Wan Mohd Hazim | 227900 |

---

## Project Overview

The **Hostel Room Booking System** is a web-based enterprise application that automates and streamlines the hostel room reservation process at UPM. Students can browse and book hostel rooms online, while hostel staff and administrators manage bookings, check-ins, maintenance, and reports — all without requiring physical visits to the hostel office.

The system is built using the **Jakarta EE 10 Framework** and applies **Component-Based Software Engineering (CBSE)** concepts including reusable components, layered architecture, and system integration. It is structured as a **Maven EAR (Enterprise Application)** multi-module project.

---

## Technology Stack

| Layer | Technology | Version |
|-------|------------|---------|
| Application Server | GlassFish | 7.0.25 |
| Java Platform | JDK | 18 |
| Enterprise Standard | Jakarta EE | 10 |
| Frontend (View) | Jakarta Faces (JSF) | 4.0 |
| UI Component Library | PrimeFaces | 14.0.0 (jakarta) |
| Business Logic | Enterprise JavaBeans (EJB) | 4.0 — Stateless Session Beans |
| Web Services | JAX-WS (Jakarta XML Web Services) | 4.0 |
| Persistence | Jakarta Persistence API (JPA) | 3.1 + EclipseLink |
| Dependency Injection | CDI (Jakarta Contexts & DI) | 4.0 |
| Bean Validation | Jakarta Bean Validation | 3.0 |
| Servlet | Jakarta Servlet | 6.0 |
| Database | H2 (embedded) | 2.2.224 |
| Password Hashing | BCrypt (jbcrypt) | 0.4 |
| Build Tool | Apache Maven | 3.9.6 |
| Package Namespace | Jakarta EE 10 | `jakarta.*` |

---

## Maven EAR Project Structure

```
HostelBookingSystem/                          ← Parent POM (packaging: pom)
│
├── pom.xml                                   ← Parent: manages versions & shared config
│
├── HostelBookingSystem-ejb/                  ← EJB Module (packaging: ejb)
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/hostel/
│       │   ├── entity/                       ← JPA Entities (9 entities)
│       │   │   └── enums/                    ← Enums (8 enums)
│       │   ├── dao/                          ← Data Access Objects (9 DAOs)
│       │   ├── service/                      ← EJB Business Services (10 services)
│       │   ├── ws/                           ← JAX-WS Payment Web Service
│       │   └── util/                         ← PasswordUtil, DataInitializer
│       └── resources/META-INF/
│           └── persistence.xml               ← JPA config (JPA 3.1, H2)
│
├── HostelBookingSystem-war/                  ← WAR Module (packaging: war)
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/hostel/
│       │   ├── bean/                         ← CDI JSF Managed Beans (14 beans)
│       │   └── util/                         ← AuthFilter (servlet filter)
│       └── webapp/
│           ├── WEB-INF/
│           │   ├── web.xml                   ← Servlet 6.0 config
│           │   ├── faces-config.xml          ← Jakarta Faces 4.0 navigation
│           │   ├── beans.xml                 ← CDI 4.0 discovery
│           │   └── glassfish-web.xml         ← GlassFish context root
│           ├── templates/layout.xhtml        ← Master Facelets template
│           ├── resources/css/styles.css      ← Custom CSS
│           ├── login.xhtml / register.xhtml
│           ├── student/                      ← 6 student pages
│           ├── staff/                        ← 4 staff pages
│           └── admin/                        ← 6 admin pages
│
└── HostelBookingSystem-ear/                  ← EAR Module (packaging: ear)
    ├── pom.xml
    └── src/main/application/META-INF/
        └── glassfish-application.xml         ← GlassFish 7 EAR descriptor
```

---

## Architecture

### Physical View — Three-Tier Architecture
```
[Browser / Client]
        |
        | HTTP / HTTPS
        v
[Application Server — GlassFish 7.0.25 (Jakarta EE 10)]
   ┌────────────────────────────────────────────────────────┐
   │               HostelBookingSystem.ear                  │
   │  ┌─────────────────────┐  ┌────────────────────────┐  │
   │  │ HostelBookingSystem │  │  HostelBookingSystem   │  │
   │  │      -war.war       │  │      -ejb.jar          │  │
   │  │ JSF 4.0 Pages       │  │ EJB 4.0 Services       │  │
   │  │ CDI Beans           │  │ JPA 3.1 Entities       │  │
   │  │ PrimeFaces 14       │  │ JAX-WS Web Service     │  │
   │  └─────────────────────┘  └────────────────────────┘  │
   └────────────────────────────────────────────────────────┘
        |
        | JDBC
        v
[Database — H2 (Dev) / MySQL (Production)]
```

### Development View — Layered Architecture
```
┌──────────────────────────────────────────────────────────┐
│  PRESENTATION LAYER  [HostelBookingSystem-war]           │
│  Jakarta Faces 4.0 (.xhtml) + PrimeFaces 14             │
│  Facelets templates + CDI @Named / @ViewScoped beans     │
├──────────────────────────────────────────────────────────┤
│  BUSINESS / SERVICE LAYER  [HostelBookingSystem-ejb]     │
│  @Stateless EJBs — enforce business rules               │
│  @Singleton DataInitializer — seed data on startup       │
│  JAX-WS PaymentWebService — SOAP web service             │
├──────────────────────────────────────────────────────────┤
│  DATA ACCESS LAYER  [HostelBookingSystem-ejb]            │
│  DAO Pattern — AbstractDAO<T,ID> base class              │
│  @Stateless EJB DAOs + JPQL Named Queries                │
├──────────────────────────────────────────────────────────┤
│  DATA LAYER  [HostelBookingSystem-ejb]                   │
│  JPA 3.1 Entities + EclipseLink + H2 / MySQL            │
└──────────────────────────────────────────────────────────┘
         ↕ packaged together in ↕
┌──────────────────────────────────────────────────────────┐
│  DEPLOYMENT UNIT  [HostelBookingSystem-ear]              │
│  HostelBookingSystem.ear                                 │
│  → HostelBookingSystem-ejb.jar                          │
│  → HostelBookingSystem-war.war                          │
│  → lib/ (H2, jBCrypt)                                   │
└──────────────────────────────────────────────────────────┘
```

### Design Patterns Applied
| Pattern | Where | Description |
|---------|-------|-------------|
| **Session Facade** | All EJB Services | Services hide DAO complexity from JSF beans |
| **DAO Pattern** | `AbstractDAO` + 9 DAO classes | Separates data access from business logic |
| **MVC** | Full system | JSF (View) + CDI Beans (Controller) + EJBs/Entities (Model) |
| **Singleton** | `DataInitializer` (`@Singleton @Startup`) | Runs once on startup to seed data and define datasource |
| **Stateless** | All EJB Services and DAOs | Scalable, no client state retained between calls |

---

## User Roles & Use Cases

| Role | Key Functions |
|------|--------------|
| **Student** | Register, browse/filter rooms, submit booking, make payment, cancel booking, submit complaint, view notifications |
| **Hostel Staff** | Approve/reject bookings, check-in/check-out, log maintenance requests, update room availability |
| **Admin** | Manage rooms (CRUD), manage all bookings, respond to complaints, generate reports, manage registration periods |
| **Payment Service** (External JAX-WS) | Verify payment transactions, send confirmation |

### Business Constraints Enforced in Code
- Student can only have **one active booking** at a time — validated in `BookingService`
- Bookings only during an **active registration period** — validated in `BookingService`
- **No duplicate room bookings** — validated before persisting
- **Role-based page access** enforced by `AuthFilter` on `/student/*`, `/staff/*`, `/admin/*`
- **Payment verification** via JAX-WS `PaymentWebService`

---

## Module Responsibilities

### HostelBookingSystem-ejb
| Package | Classes | Responsibility |
|---------|---------|----------------|
| `com.hostel.entity` | 9 entities | JPA-mapped database tables |
| `com.hostel.entity.enums` | 8 enums | Typed status constants |
| `com.hostel.dao` | 9 DAOs | Database CRUD via EntityManager |
| `com.hostel.service` | 10 services | Business rules, transactions |
| `com.hostel.ws` | 2 classes | JAX-WS SOAP Payment Web Service |
| `com.hostel.util` | 2 classes | BCrypt hashing, startup data seeding |

### HostelBookingSystem-war
| Package | Classes | Responsibility |
|---------|---------|----------------|
| `com.hostel.bean` | 14 CDI beans | JSF page controllers |
| `com.hostel.util` | 1 class | `AuthFilter` — role-based access control |
| `webapp/` | 20 XHTML pages | UI views |
| `webapp/resources/css/` | `styles.css` | Custom responsive design |

---

## Seed Data (Auto-loaded on First Startup)

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `Admin@123` |
| Staff | `staff1` | `Staff@123` |
| Student | `student1` | `Student@123` |
| Student | `student2` | `Student@123` |

**10 rooms** pre-seeded across blocks A and B.
**1 active registration period:** 1 Jan 2026 – 31 Dec 2026.

---

## Build & Deploy Instructions

### Prerequisites
| Tool | Version |
|------|---------|
| JDK | 18+ |
| Apache Maven | 3.6+ |
| GlassFish | 7.0.25 |

### 1. Build the EAR
```bash
cd HostelBookingSystem
mvn clean package -DskipTests
```

**Output artifacts:**
```
HostelBookingSystem-ejb/target/HostelBookingSystem-ejb.jar   (67 KB)
HostelBookingSystem-war/target/HostelBookingSystem-war.war   (4.2 MB)
HostelBookingSystem-ear/target/HostelBookingSystem-ear.ear   (6.7 MB)  ← deploy this
```

### 2. Start GlassFish 7
```bash
<glassfish-home>/bin/asadmin start-domain
```

### 3. Deploy the EAR
```bash
<glassfish-home>/bin/asadmin deploy \
  HostelBookingSystem-ear/target/HostelBookingSystem-ear-1.0-SNAPSHOT.ear
```

### 4. Access the Application
```
http://localhost:8080/hostel-booking/login.xhtml
```

### Switching to MySQL (Production)
1. Update `DataInitializer.java` `@DataSourceDefinition`:
```java
className = "com.mysql.cj.jdbc.MysqlDataSource",
url       = "jdbc:mysql://localhost:3306/hosteldb",
user      = "root",
password  = "yourpassword"
```
2. Update `persistence.xml` — remove H2Platform property.
3. Add MySQL JDBC driver JAR to `GlassFish/domain1/lib/`.

---

## JAX-WS Payment Web Service

The `PaymentWebService` is a **Stateless Session Bean published as a SOAP Web Service**, satisfying the assignment requirement for stateless session beans as web services.

- **WSDL:** `http://localhost:8080/hostel-booking/PaymentWebServiceImplService?wsdl`
- **Operations:**
  - `verifyPayment(transactionId, amount)` → `boolean`
  - `getTransactionStatus(transactionId)` → `String`

---

## What Changed from Previous Version

| Item | Previous (hostel-booking) | Current (EAR) |
|------|--------------------------|----------------|
| Structure | Single WAR module | EAR (ejb + war + ear modules) |
| App Server | GlassFish 5.1.0 | GlassFish 7.0.25 |
| Java | JDK 8 | JDK 18 |
| API Namespace | `javax.*` | `jakarta.*` |
| Jakarta EE | 8 | 10 |
| JSF Version | 2.3 | Faces 4.0 |
| Servlet Version | 4.0 | 6.0 |
| JPA Version | 2.2 | 3.1 |
| CDI Version | 2.0 | 4.0 |
| PrimeFaces | 10.0.0 | 14.0.0 (jakarta) |
| XHTML namespaces | `http://xmlns.jcp.org/jsf/*` | `jakarta.faces.*` |
| Java syntax | Java 8 only | Switch expressions, `isBlank()`, `toList()`, `isEmpty()` |
| Deployment | `.war` | `.ear` |

---

## Build Validation

```
[INFO] BUILD SUCCESS
HostelBookingSystem-ejb  →  67 KB  JAR
HostelBookingSystem-war  →  4.2 MB WAR
HostelBookingSystem-ear  →  6.7 MB EAR
Built with: JDK 18 + Maven 3.9.6
```
