# Malibu Lumina Hotel Reservation System - Final Documentation
**Course:** APD545  
**Project:** Hotel Reservation System  
**Date:** August 4, 2026  
**Team Members:** [Add team member names]

---

## 1. Project Overview

### ✅ Summary of System and Purpose
A JavaFX-based hotel reservation kiosk and administrative management system for Malibu Lumina Hotel. The system enables guests to self-book rooms via kiosks while providing comprehensive backend tools for hotel staff to manage reservations, process payments, handle waitlists, and generate reports.

### ✅ Key Features
- **Guest Kiosk:** Self-service booking with room selection, add-on services, and confirmation
- **Admin Dashboard:** Full CRUD operations for reservations, checkout processing, and billing
- **Payment Processing:** Multiple payment methods (Cash, Credit/Debit Cards, Apple Pay, Google Pay)
- **Loyalty Program:** Point earning (10 points per $1) and redemption system
- **Waitlist Management:** Observer pattern notifications when rooms become available
- **Reporting System:** Revenue and occupancy reports with CSV/PDF export
- **Role-Based Discounts:** Admin (15% cap) vs Manager (30% cap)
- **Feedback System:** Guest reviews and ratings after checkout

### ✅ Technologies Used
- **Java 21:** Core language with module system
- **JavaFX 21.0.6:** UI framework for kiosk and admin interfaces
- **Hibernate 5.6.15.Final + JPA:** ORM layer with entity annotations
- **SQLite + sqlite-dialect:** Lightweight embedded database
- **BCrypt (jbcrypt 0.4):** Password hashing for admin authentication
- **Google Guice 7.0.0:** Dependency injection framework
- **iText7 (7.2.5):** PDF generation for reports
- **SLF4J 2.0.12:** Logging facade with file rotation
- **Maven:** Build and dependency management

---

## 2. Architecture Summary

### ✅ 3-Tier Architecture
**Presentation Layer:**
- JavaFX controllers handling UI logic and user interactions
- FXML views defining layouts and components
- Scene navigation and session management

**Business Logic Layer:**
- Service classes encapsulating business rules (BookingService, PaymentService, LoyaltyService, DiscountService, ReportService, etc.)
- Design pattern implementations (Observer, Decorator, Strategy, Factory, Singleton)
- Validation utilities and business rule enforcement

**Data Access Layer:**
- Repository pattern with generic AbstractRepository base class
- Entity classes with JPA annotations
- EntityManagerFactoryProvider singleton for database connections

### ✅ Cross-Cutting Concerns
**Security:**
- BCrypt password hashing (12 rounds) for admin credentials
- Session-based authentication with SessionManager singleton
- Role-based access control (AdminRole enum: ADMIN, MANAGER)

**Logging:**
- File-based logging with rotation (1MB limit, 10 files)
- LoggerConfig utility configuring java.util.logging.FileHandler
- Activity log auditing for all admin actions

**Exception Handling:**
- Try-catch blocks in all database operations
- User-friendly error messages displayed via alerts
- Stack traces logged to rotating log files

### ✅ MVC, DI, ORM Usage
**MVC Pattern:**
- Models: Entity classes in `models` package (Guest, Reservation, Room, Bill, etc.)
- Views: FXML files in `resources/view/fxml`
- Controllers: JavaFX controllers in `controller` package

**Dependency Injection:**
- Google Guice modules configured in `config` package
- Constructor injection for repository and service dependencies
- Singleton bindings for EntityManagerFactory and managers

**ORM (JPA/Hibernate):**
- Entity annotations (@Entity, @Table, @Id, @GeneratedValue)
- Relationship mappings (@ManyToOne, @OneToMany, @JoinColumn)
- Named queries and JPQL for complex queries
- Cascade types and fetch strategies configured per relationship

---

## 3. Design Artifacts

### ✅ Class Diagram
- **Location:** `UML/apd545-project-ms1.vpp`
- **Key Diagrams:**
  - Booking domain class diagram (V4)
  - Administration class diagram (V2)
  - Billing class diagram (V4)

### ✅ Entity Relationship Diagram
- **Location:** `UML/hotel reservation system entity relationship diagram_V5.png`
- **Coverage:** All 15+ entities with relationships, cardinalities, and constraints

### ✅ UI Screenshots
- **Welcome Screen:** Kiosk entry point (guest vs admin)
- **Room Selection:** Available rooms with filtering by type
- **Admin Dashboard:** Central hub with navigation to all admin features
- **Reports View:** Tabular data with export options

---

## 4. Entity and Relationship Mapping

### ✅ List of Entities
1. **Guest** - Customer information
2. **Reservation** - Booking details (1 Guest : N Reservations)
3. **ReservationItem** - Junction table linking Reservation to Rooms
4. **Room** - Individual room instances (N Rooms : 1 RoomType)
5. **RoomType** - Room categories (Single, Double, Suite, Deluxe)
6. **AddOn** - Additional services (Breakfast, WiFi, Parking, Spa)
7. **AddOnSelection** - Junction table for Reservation-AddOn (M:N)
8. **Bill** - Invoice for reservation (1 Reservation : 1 Bill)
9. **Payment** - Payment transactions (1 Bill : N Payments)
10. **LoyaltyAccount** - Guest loyalty program (1 Guest : 1 Account)
11. **LoyaltyTransaction** - Point earning/redemption history
12. **AdminUser** - System administrators with roles
13. **Feedback** - Guest reviews (1 Reservation : 1 Feedback)
14. **WaitlistEntry** - Room availability waitlist
15. **ActivityLog** - Admin action audit trail

### ✅ Relationships and Annotations
**Key Relationships:**
- Guest ↔ Reservation: @OneToMany (cascade PERSIST, fetch LAZY)
- Reservation ↔ ReservationItem: @OneToMany (cascade ALL, orphanRemoval)
- Room ↔ RoomType: @ManyToOne (fetch EAGER for availability checks)
- Reservation ↔ Bill: @OneToOne (cascade PERSIST)
- Bill ↔ Payment: @OneToMany (cascade ALL)
- Guest ↔ LoyaltyAccount: @OneToOne (cascade ALL)
- AdminUser ↔ ActivityLog: @OneToMany (audit trail)

### ✅ Cascade/Fetch/Validation Notes
**Cascade Types:**
- `CascadeType.ALL` used for owned entities (ReservationItem, AddOnSelection)
- `CascadeType.PERSIST` for associated entities to avoid accidental deletes
- `orphanRemoval=true` for junction tables

**Fetch Strategies:**
- `FetchType.LAZY` for large collections to avoid N+1 queries
- `FetchType.EAGER` for frequently accessed relationships (Room → RoomType)

**Validation:**
- `@Column(nullable=false)` for required fields
- `@Column(unique=true)` for usernames and email addresses
- Custom validation in ValidationUtil for email, phone, dates, occupancy

---

## 5. Pattern Usage

### ✅ Strategy Pattern
**Usage:** Pricing calculations based on room type and season  
**Implementation:**
- `PricingModel` enum with dynamic price calculation methods
- `BookingService.calculateBill()` applies appropriate strategy
- **Location:** `models/enums/PricingModel.java`, `services/BookingService.java`

### ✅ Observer Pattern
**Usage:** Notify admin when rooms become available for waitlist guests  
**Implementation:**
- `RoomAvailabilitySubject` interface (attach/detach/notify)
- `AdminNotificationManager` singleton subject
- `RoomAvailabilityObserver` interface with `onRoomAvailable()` callback
- Triggered on checkout when rooms freed
- **Location:** `events/` package

### ✅ Factory Pattern
**Usage:** Create room instances and pricing models  
**Implementation:**
- `RoomFactory` creates Room objects with proper initialization
- `PricingModelFactory` selects pricing strategy by season
- **Location:** `factories/` package

### ✅ Decorator Pattern
**Usage:** Dynamically add services (Spa, WiFi, Breakfast, Parking) to bill  
**Implementation:**
- `BookingComponent` interface with `getCost()` and `getDescription()`
- `BaseBooking` concrete component (base room cost)
- `AddOnDecorator` abstract decorator wrapping component
- Concrete decorators: `BreakfastDecorator`, `WifiDecorator`, `ParkingDecorator`, `SpaDecorator`
- Each decorator adds incremental cost and updates description
- **Location:** `decorators/` package, integrated in `BookingService.buildDecoratedBooking()`

### ✅ Singleton Pattern
**Usage:** Shared resources requiring single instance  
**Implementation:**
- `EntityManagerFactoryProvider` - Single EMF for entire application
- `SessionManager` - Current admin user session state
- `AdminNotificationManager` - Central subject for observer notifications
- **Location:** `config/EntityManagerFactoryProvider.java`, `security/SessionManager.java`, `events/AdminNotificationManager.java`

---

## 6. Business Rules

### ✅ Occupancy Rules
- Single rooms: Max 1 guest
- Double rooms: Max 2 guests
- Suite rooms: Max 4 guests
- Deluxe rooms: Max 6 guests
- Validation enforced in `ValidationUtil.validateOccupancy()` before booking

### ✅ Pricing Rules
- Base pricing defined in `PricingModel` enum (STANDARD, PEAK, OFF_SEASON)
- Room type multipliers applied to base rates
- Add-on flat fees: Breakfast ($45), WiFi ($30), Parking ($60), Spa ($150)
- Decorator pattern chains costs for selected add-ons

### ✅ Discount Rules
- **Admin Role:** Maximum 15% discount on subtotal
- **Manager Role:** Maximum 30% discount on subtotal
- Enforced in `DiscountService.calculateDiscount()` with role validation
- Exceeding cap throws `IllegalArgumentException`

### ✅ Loyalty Rules
- Earning rate: 10 points per $1 spent (1000 points = $100 spent)
- Redemption rate: 1 point = $0.01 (100 points = $1 discount)
- Auto-enrollment offered at first checkout
- Points calculated in `LoyaltyService.earnPoints()` after payment

### ✅ Feedback Rules
- One feedback entry per reservation
- Rating scale: 1-5 stars (integer validation)
- Submission only after checkout (reservation status = CHECKED_OUT)
- Admin can view aggregated ratings and filter by threshold

### ✅ Enforcement Logic
- Validation layer in `util/ValidationUtil.java`
- Service layer methods throw exceptions for rule violations
- UI displays user-friendly error messages via alerts
- Database constraints enforce referential integrity

---

## 7. Security and Logging

### ✅ Authentication and Roles
**BCrypt Hashing:**
- Passwords hashed with BCrypt using 12 rounds (cost factor)
- Plain text passwords never stored or logged
- `PasswordHasher.hashPassword()` for registration
- `PasswordHasher.verifyPassword()` for login authentication

**Roles:**
- `AdminRole` enum: ADMIN, MANAGER
- Role-based discount caps enforced in `DiscountService`
- Session tracking via `SessionManager.setCurrentUser()`

**Login Flow:**
1. Admin enters username/password in Login.fxml
2. `AuthenticationService.authenticate()` verifies credentials
3. On success, `SessionManager` stores current AdminUser
4. Controllers check `SessionManager.isAuthenticated()` for access control

### ✅ Logging Configuration
**File Rotation:**
- Pattern: `system_logs.%g.log` (e.g., system_logs.0.log, system_logs.1.log)
- Max file size: 1MB per file
- Max files: 10 (rotates oldest when limit reached)
- Configured in `LoggerConfig.configure()` using `java.util.logging.FileHandler`
- Initialized in `Main.java` at application startup

**Log Samples:**
```
INFO: Admin user 'manager' logged in successfully
INFO: Discount of 12.5% applied to Bill #42 by Admin #1
INFO: Payment of $500.00 processed via CREDIT_CARD for Bill #42
INFO: Reservation #123 checked out. Room 201 now available.
INFO: Observer notified: Room 201 (DOUBLE) available for waitlist
```

### ✅ Exception Handling
- All database operations wrapped in try-catch blocks
- Stack traces logged to file with `Logger.log(Level.SEVERE, "Error", exception)`
- User sees generic error alerts: "An error occurred. Please try again."
- Sensitive information (passwords, credit card details) excluded from logs

---

## 8. Export and Reporting

### ✅ Report Types
1. **Revenue Report**
   - Date range selection
   - Aggregates: Total reservations, subtotal, tax, total revenue per day
   - Generated by `ReportService.generateRevenueReport()`

2. **Occupancy Report**
   - Date range selection
   - Metrics: Total rooms, occupied rooms, occupancy percentage per day
   - Generated by `ReportService.generateOccupancyReport()`

3. **Activity Log Report**
   - Date range selection
   - Admin action audit trail (logins, checkouts, discounts, reservations)
   - Generated by `ReportService.generateActivityLogReport()`

4. **Feedback Summary**
   - Average rating, rating distribution, recent comments
   - Generated by `ReportService.generateFeedbackSummary()`

### ✅ Export Formats
**CSV Export:**
- Utility: `CsvExporter.exportToCsv()`
- Tabular data with comma-separated values
- Opens in Excel or any spreadsheet software

**PDF Export:**
- Utility: `PdfExporter.exportToPdf()` using iText7
- Professional formatted tables with headers
- Title and date included

**TXT Export:**
- Utility: `TxtExporter.exportToTxt()`
- Plain text format for lightweight viewing

### ✅ Sample Exports
**Revenue Report (CSV):**
```
Date,Reservations,Subtotal,Tax,Total Revenue
2026-08-01,5,$2450.00,$318.50,$2768.50
2026-08-02,3,$1620.00,$210.60,$1830.60
2026-08-03,7,$3890.00,$505.70,$4395.70
```

**Occupancy Report (PDF):**
```
Date       | Total Rooms | Occupied | Occupancy %
-----------|-------------|----------|------------
2026-08-01 | 50          | 35       | 70.00%
2026-08-02 | 50          | 42       | 84.00%
2026-08-03 | 50          | 38       | 76.00%
```

---

## 9. Challenges and Learnings

### ✅ Overall Team Technical Challenges

**1. Pattern Integration with Existing Architecture**
- **Challenge:** Implementing Observer and Decorator patterns without breaking existing Milestone 1/2 functionality
- **Solution:** Added patterns as behavioral wrappers around existing entities rather than replacing core logic
- **Learning:** Design patterns should extend, not replace, working code

**2. JPA Relationship Mapping Complexity**
- **Challenge:** Circular references causing LazyInitializationException and N+1 query problems
- **Solution:** Strategic use of FetchType.LAZY, @JsonIgnore, and DTO patterns for data transfer
- **Learning:** Careful planning of cascade types and fetch strategies prevents performance issues

**3. File Logging with Rotation**
- **Challenge:** SLF4J doesn't natively support file rotation; needed java.util.logging.FileHandler
- **Solution:** Created LoggerConfig utility wrapping FileHandler with 1MB/10-file rotation
- **Learning:** Sometimes simpler tools (java.util.logging) are better than complex frameworks for basic needs

**4. Role-Based Business Logic**
- **Challenge:** Enforcing different discount caps based on admin role
- **Solution:** Created DiscountService with role validation throwing exceptions on cap violations
- **Learning:** Service layer is the right place for business rule enforcement

**5. UI State Management**
- **Challenge:** Passing booking session data across multiple FXML screens
- **Solution:** BookingSession singleton holding temporary state during kiosk flow
- **Learning:** Session objects work well for multi-screen workflows without database persistence

### ✅ Individual Reflections from Each Team Member

**Team Member 1: [Name] - Patterns & Security Lead**
- **Contribution:** Implemented Observer pattern, BCrypt authentication, SessionManager
- **Challenge:** Understanding how Observer pattern fits with JPA entities (shouldn't persist observers)
- **Learning:** Behavioral patterns operate in memory, not persisted; separation of concerns is key
- **Future Improvement:** Add email/SMS notifications when observers triggered

**Team Member 2: [Name] - Core Logic & Admin Features Lead**
- **Contribution:** Decorator pattern, payment processing, discount service, CRUD operations
- **Challenge:** Ensuring decorator chain correctly accumulates costs and descriptions
- **Learning:** Decorator pattern requires careful testing of each wrapper layer
- **Future Improvement:** Add unit tests for each decorator combination

**Team Member 3: [Name] - Entities & Reporting Lead**
- **Contribution:** Created 15+ entities, repositories, reporting system with CSV/PDF export
- **Challenge:** iText7 PDF API had steep learning curve for table formatting
- **Learning:** Reading library documentation thoroughly before coding saves debugging time
- **Future Improvement:** Add charts/graphs to PDF reports for visual insights

### ✅ Suggestions for Improvement

**Short-Term (Next Milestone):**
1. Add comprehensive unit test suite (JUnit 5 + Mockito)
2. Implement email notifications for waitlist and reservations
3. Add search/filter functionality to admin reservation table
4. Create admin user management UI (add/edit/delete admins)
5. Implement soft deletes for reservations (status = CANCELLED vs hard delete)

**Long-Term (Production Readiness):**
1. Migrate from SQLite to PostgreSQL/MySQL for multi-user concurrency
2. Add REST API layer for mobile app integration
3. Implement multi-language support (i18n)
4. Add two-factor authentication for admin logins
5. Create real-time dashboard with WebSocket updates
6. Implement automated backups and disaster recovery
7. Add performance monitoring and analytics (response times, peak usage)
8. Implement rate limiting for kiosk to prevent abuse

**Code Quality:**
1. Increase test coverage to 80%+ (currently no automated tests)
2. Add JavaDoc comments to all public APIs
3. Run SonarQube for code quality and security scanning
4. Implement CI/CD pipeline (GitHub Actions + Maven)
5. Add pre-commit hooks for code formatting (Checkstyle/PMD)

---

## Appendix

### ✅ Testing Checklist Completed
- [x] Kiosk: Complete booking flow (Welcome → Confirmation)
- [x] Kiosk: Add-ons using Decorator pattern
- [x] Admin: Login with BCrypt authentication
- [x] Admin: Search and view reservations
- [x] Admin: Create/Edit/Delete reservations (CRUD)
- [x] Admin: Process payment (cash, card, points)
- [x] Admin: Apply discount with role caps
- [x] Admin: Check out guest
- [x] Observer: Room availability notification
- [x] Admin: View waitlist entries
- [x] Admin: Add guest to waitlist
- [x] Guest: Submit feedback after checkout
- [x] Admin: View feedback
- [x] Admin: Generate revenue report
- [x] Admin: Export report to CSV
- [x] Admin: Export report to PDF
- [x] Logging: File rotation working (1MB, 10 files)
- [x] Loyalty: Enroll guest
- [x] Loyalty: Earn and redeem points
- [x] All validations working

### ✅ Build Information
- **Build Tool:** Maven 3.9.9
- **Java Version:** 21
- **Module System:** Enabled (`module-info.java`)
- **Compilation Status:** ✅ BUILD SUCCESS (August 4, 2026)
- **Total Source Files:** 111 Java files

### ✅ Repository Structure
```
MalibuLuminaHotel/
├── src/main/java/ca/senecacollege/malibuluminahotel/
│   ├── app/                    # Main entry point, BookingSession
│   ├── config/                 # EntityManagerFactory, DI modules
│   ├── controller/             # JavaFX controllers (15+ files)
│   ├── decorators/             # Decorator pattern (5 files)
│   ├── events/                 # Observer pattern (4 files)
│   ├── factories/              # Factory pattern (2 files)
│   ├── models/                 # JPA entities (15+ files)
│   ├── models/enums/           # Enums (10+ files)
│   ├── repositories/           # Data access layer (30+ files)
│   ├── security/               # Authentication, BCrypt (3 files)
│   ├── services/               # Business logic (10+ files)
│   ├── util/                   # Validation, export, logging (6 files)
│   └── tests/                  # Pattern tests (2 files)
├── src/main/resources/
│   ├── META-INF/persistence.xml
│   ├── application.properties
│   └── view/
│       ├── fxml/               # UI layouts (15+ files)
│       ├── css/style.css
│       └── images/
├── pom.xml                     # Maven dependencies
└── module-info.java            # Java module descriptor
```

---

**End of Documentation**  
**Submission Date:** August 4, 2026  
**Project Status:** ✅ Complete and Tested
