# Quick Action Plan - Phase-Based Implementation Strategy
**Project:** Malibu Lumina Hotel Reservation System  
**Deadline:** August 7, 2026 (3 days)  
**Status:** 55% Complete → Target 85%+

---
## ⚠️ CRITICAL: TOKENS IS MONEY
Do not produce documentation and code which is not mandatory for this project to work. Think twice before generating unneccessary code or documentation as more you generate, more expensive it is, and we want to save money. 

## ⚠️ CRITICAL: COMPLIANCE WITH MILESTONE 1 & 2

**MANDATORY REQUIREMENTS - READ BEFORE ANY IMPLEMENTATION:**

### 1. UML/ERD Adherence (NON-NEGOTIABLE)
- **MUST FOLLOW:** All entity structures, relationships, and cardinalities defined in:
  - `UML/hotel reservation system entity relationship diagram_V5.png` (ERD)
  - `UML/booking class diagramV4.png` (Booking domain)
  - `UML/administration class diagramV2.png` (Admin domain)
  - `UML/billing class diagramV4.png` (Billing domain)
- **ENTITY NAMES:** Use exact names from ERD (e.g., "AdminUser" not "User", "WaitlistEntry" not "Waitlist")
- **ATTRIBUTES:** Match data types, nullability, and constraints from diagrams
- **RELATIONSHIPS:** Preserve all foreign keys, junction tables, and cardinalities

### 2. Existing Features Protection
- **DO NOT MODIFY:** Any working Milestone 1 or Milestone 2 functionality
- **DO NOT ALTER:** Existing entity classes (Guest, Reservation, Room, RoomType, AddOn, Bill, Payment, LoyaltyAccount, etc.)
- **DO NOT CHANGE:** Existing repository interfaces or service method signatures that are in use
- **DO NOT BREAK:** Current kiosk booking flow (Welcome → Stay Info → Room Selection → Guest Info → Add-ons → Confirmation)

### 3. Pattern Integration Rules
- **ADD, DON'T REPLACE:** New patterns must extend existing code, not replace working logic
- **BACKWARD COMPATIBLE:** New services must work with existing repositories and entities
- **NO SCHEMA CHANGES:** Behavioral patterns (Decorator, Observer, Strategy) should not alter database structure
- **VERIFY BEFORE COMMIT:** Test existing features after adding new ones

### 4. Verification Checklist (Before Each Phase)
- [ ] Does this change appear in the ERD/UML diagrams?
- [ ] Will this break any existing Milestone 1/2 feature?
- [ ] Are we using the exact entity/field names from the diagrams?
- [ ] Have we tested the kiosk flow end-to-end after changes?

**If unsure about compliance, STOP and consult the UML/ERD diagrams first.**

---

## Implementation Phases

Work through these phases sequentially. Each phase builds on the previous one and delivers working features.

---

## 🔴 PHASE 1: Critical Patterns (PRIORITY 1)
**Goal:** Implement Observer and Decorator patterns (required for passing)  
**Estimated Time:** 4-5 hours  
**Impact:** +15% to grade  
**Status:** ✅ COMPLETED - August 4, 2026

### Tasks:

#### 1.1 Observer Pattern for Waitlist Notifications (2-3 hours)
```java
// Create these files:
// 1. events/RoomAvailabilityObserver.java (interface)
//    - void onRoomAvailable(Room room, RoomType roomType)
// 2. events/RoomAvailabilitySubject.java (interface)
//    - void attach(RoomAvailabilityObserver observer)
//    - void detach(RoomAvailabilityObserver observer)
//    - void notifyObservers(Room room, RoomType roomType)
// 3. events/AdminNotificationManager.java (subject implementation)
//    - Manages list of observers
//    - Notifies all when room becomes available
// 4. events/WaitlistNotificationObserver.java (concrete observer)
//    - Implements notification logic for waitlist
// 5. Integrate into AdminCheckoutController (notify on checkout)
```

**Acceptance Criteria:**
- When admin checks out a guest, observers are notified
- Waitlist entries for that room type are flagged
- Admin sees notification in dashboard

#### 1.2 Decorator Pattern for Add-On Pricing (2 hours)
```java
// Create these files:
// 1. decorators/BookingComponent.java (interface)
//    - BigDecimal getCost()
//    - String getDescription()
// 2. decorators/BaseBooking.java (concrete component)
//    - Base room cost calculation
// 3. decorators/AddOnDecorator.java (abstract decorator)
//    - Wraps BookingComponent
// 4. decorators/BreakfastDecorator.java (concrete decorator)
// 5. decorators/WifiDecorator.java (concrete decorator)
// 6. decorators/ParkingDecorator.java (concrete decorator)
// 7. decorators/SpaDecorator.java (concrete decorator)
// 8. Update BookingService.calculateBill() to use decorator chain
```

**Acceptance Criteria:**
- Bill calculation uses decorator pattern
- Each add-on wraps the previous cost
- Description shows all selected add-ons

**Test Results (August 4, 2026):**
✅ **Observer Pattern Tests PASSED:**
- Multiple observers successfully receive notifications
- Attach/detach operations work correctly
- Notification counts tracked accurately
- AdminNotificationManager singleton pattern validated

✅ **Decorator Pattern Tests PASSED:**
- Base booking ($360.00) calculated correctly
- Breakfast decorator ($45.00) adds correctly → $405.00
- Wi-Fi decorator ($30.00) adds correctly → $435.00
- Parking decorator ($60.00) adds correctly → $495.00
- Spa decorator ($150.00) adds correctly → $645.00
- Final total matches expected calculation
- Description chain correctly concatenates all add-ons
- Individual decorators validated independently

**Files Created:**
- events/RoomAvailabilityObserver.java (interface)
- events/RoomAvailabilitySubject.java (interface)
- events/AdminNotificationManager.java (Singleton subject)
- events/WaitlistNotificationObserver.java (concrete observer)
- decorators/BookingComponent.java (interface)
- decorators/BaseBooking.java (concrete component)
- decorators/AddOnDecorator.java (abstract decorator)
- decorators/BreakfastDecorator.java (concrete decorator)
- decorators/WifiDecorator.java (concrete decorator)
- decorators/ParkingDecorator.java (concrete decorator)
- decorators/SpaDecorator.java (concrete decorator)
- tests/Phase1PatternTest.java (test class)

**Files Modified:**
- services/BookingService.java (integrated Decorator pattern)
- module-info.java (exported events and decorators packages)

**✅ Compliance Verification (Milestone 1 & 2):**
- ✅ **No Existing Entities Modified:** Room, RoomType, Reservation, AddOn, Guest entities remain unchanged
- ✅ **No Schema Changes:** Observer and Decorator are behavioral patterns that don't alter database structure
- ✅ **ERD Alignment:** WaitlistEntry entity (from ERD) is ready for Observer pattern integration in Phase 3
- ✅ **Existing Flow Preserved:** Kiosk booking flow (Welcome → Stay Info → Room Selection → Add-ons → Checkout) still works
- ✅ **Backward Compatible:** BookingService.calculateBill() still returns correct totals, now using Decorator pattern internally
- ✅ **Pattern Integration:** Patterns added on top of existing architecture, not replacing working code
- ✅ **Repository Layer Untouched:** All existing repositories (RoomRepository, AddOnRepository, etc.) unchanged
- ✅ **Service Contracts Preserved:** Public methods in BookingService maintain same signatures

**Why First:** These patterns are explicitly required and carry significant grade weight. Without them, maximum grade is ~60%.

---

## 🟠 PHASE 2: Security & Authentication (PRIORITY 2)
**Goal:** Implement admin authentication with BCrypt  
**Estimated Time:** 3-4 hours  
**Impact:** +10% to grade  
**Status:** Required for admin functionality  
**ERD Reference:** See `UML/administration class diagramV2.png` - AdminUser entity

### Tasks:

#### 2.1 AdminUser Entity & Repository (1.5 hours)
**⚠️ COMPLIANCE:** Must match ERD exactly - entity name is "AdminUser" not "User"

```java
// Create these files:
// 1. models/AdminUser.java (EXACT NAME FROM ERD)
//    - adminID (Long, PK) - matches ERD
//    - username (String) - matches ERD
//    - password (String) - matches ERD (will store BCrypt hash)
//    - role (Role enum: ADMIN, MANAGER) - matches ERD
//    - Methods: +login(), +applyDiscount()
// 2. models/enums/Role.java
//    - ADMIN, MANAGER
// 3. repositories/IAdminUserRepository.java
//    - Optional<AdminUser> findByUsername(String username)
// 4. repositories/AdminUserRepositoryImpl.java
// 5. Update persistence.xml: <class>...models.AdminUser</class>
```

#### 2.2 Authentication Service (1 hour)
```java
// Create these files:
// 1. security/PasswordHasher.java
//    - String hashPassword(String plaintext) using BCrypt
//    - boolean checkPassword(String plaintext, String hashed)
// 2. security/AuthenticationService.java
//    - Optional<AdminUser> authenticate(String username, String password)
//    - AdminUser currentUser (session state)
// 3. security/SessionManager.java (singleton)
//    - Tracks logged-in AdminUser
```

#### 2.3 Login UI & Controller (1 hour)
```java
// Create these files:
// 1. view/fxml/Login.fxml
//    - Username field, password field, login button, error label
// 2. controller/LoginController.java
//    - Uses AuthenticationService
//    - Navigates to AdminDashboard on success
// 3. Update WelcomeController.handleAdminLogin() to open Login.fxml
```

#### 2.4 Seed Admin Users (0.5 hours)
```java
// Update DataSeeder.java:
// - Create default admin (username: admin, password: admin123, role: ADMIN)
// - Create default manager (username: manager, password: manager123, role: MANAGER)
```

**Acceptance Criteria:**
- Admin can log in with username/password
- Passwords stored as BCrypt hashes (NOT plain text)
- Current AdminUser tracked in session
- Login required to access admin features
- Entity name matches ERD: "AdminUser" (not "User")

---

## 🟡 PHASE 3: Core Entities (PRIORITY 3)
**Goal:** Create missing entities for Feedback, Waitlist, and ActivityLog  
**ERD Reference:** See `UML/hotel reservation system entity relationship diagram_V5.png`
**Estimated Time:** 3-4 hours  
**Impact:** +15% to grade  
**Status:** Required for full functionality

### Tasks:

#### 3.1 Feedback Entity (1.5 hours)
**⚠️ COMPLIANCE:** Must match ERD structure in `hotel reservation system entity relationship diagram_V5.png`

```java
// Create these files:
// 1. models/Feedback.java (matches ERD)
//    - feedback_id (PK, Long)
//    - Reservation (FK to Reservation)
//    - Guest (FK to Guest - from reservation)
//    - rating (Integer 1-5)
//    - comment (String)
//    - submitted_date (LocalDate)
// 2. repositories/IFeedbackRepository.java
//    - List<Feedback> findByRatingGreaterThan(int rating)
//    - List<Feedback> findByReservation(Reservation reservation)
// 3. repositories/FeedbackRepositoryImpl.java
// 4. services/FeedbackService.java
//    - void submitFeedback(Reservation res, int rating, String comment)
//    - List<Feedback> getAllFeedback()
// 5. Update persistence.xml: <class>...models.Feedback</class>
```

#### 3.2 WaitlistEntry Entity (1 hour)
**⚠️ COMPLIANCE:** Entity name is "WaitlistEntry" (not "Waitlist") - matches ERD

```java
// Create these files:
// 1. models/WaitlistEntry.java (EXACT NAME FROM ERD)
//    - waitlist_id (PK, Long)
//    - RoomType (FK to RoomType)
//    - Guest (FK to Guest)
//    - requested_checkin (date)
//    - requested_checkout (date)
//    - date_added (date)
//    - status (WaitlistStatusType enum: InQueue, SpotAvailable, Withdrawn)
//    - Method: +notifyGuest()
// 2. models/enums/WaitlistStatusType.java
//    - InQueue, SpotAvailable, Withdrawn (matches ERD)
// 3. repositories/IWaitlistEntryRepository.java
//    - List<WaitlistEntry> findByRoomTypeAndStatus(RoomType type, WaitlistStatusType status)
// 4. repositories/WaitlistEntryRepositoryImpl.java
// 5. services/WaitlistService.java
//    - void addToWaitlist(Guest guest, RoomType type, dates...)
//    - void convertToReservation(WaitlistEntry entry)
// 6. Integrate WaitlistNotificationObserver with WaitlistService
// 7. Update persistence.xml: <class>...models.WaitlistEntry</class>
```

#### 3.3 ActivityLog Entity (1 hour)
**⚠️ COMPLIANCE:** Must reference AdminUser (not User) - matches administration class diagram

```java
// Create these files:
// 1. models/ActivityLog.java (matches ERD)
//    - log_id (PK, Long)
//    - AdminUser (FK to AdminUser - who performed action)
//    - timestamp (LocalDateTime)
//    - action (String - e.g., "checkout", "discount_applied")
//    - entity (String - e.g., "Reservation", "Guest")
//    - entity_id (Long - ID of affected entity)
//    - message (String - description of action)
//    - Method: +record() (from diagram)
// 2. repositories/IActivityLogRepository.java
//    - List<ActivityLog> findByTimestampBetween(LocalDateTime start, end)
//    - List<ActivityLog> findByAdminUser(AdminUser user)
// 3. repositories/ActivityLogRepositoryImpl.java
// 4. services/ActivityLogService.java
//    - void log(AdminUser user, String action, String entityType, Long entityId, String message)
//    - List<ActivityLog> getActivityLogs(filters...)
// 5. Update persistence.xml: <class>...models.ActivityLog</class>
// 6. Integrate logging into all admin actions (checkout, discounts, cancellations)
```

**Acceptance Criteria:**
- All entities properly annotated with JPA
- Repositories implement CRUD operations
- Services provide business logic
- Entities added to persistence.xml

---

## 🟢 PHASE 4: Admin Functionality (PRIORITY 4)
**Goal:** Complete admin payment, discount, feedback, and waitlist features  
**Estimated Time:** 5-6 hours  
**Impact:** +15% to grade  
**Status:** Required for admin workflow

### Tasks:

#### 4.1 Payment Processing (2.5 hours)
```java
// Create/Update these files:
// 1. services/PaymentService.java
//    - void processPayment(Bill bill, PaymentMethod method, BigDecimal amount)
//    - void processRefund(Payment payment, BigDecimal amount)
//    - void processDeposit(Reservation res, BigDecimal amount)
// 2. Update AdminCheckoutController.java
//    - Add payment form (cash/card/points)
//    - Show balance due
//    - Process payment and update bill
//    - Prevent checkout if balance remains
// 3. Update AdminCheckout.fxml
//    - Payment method dropdown
//    - Amount field
//    - Process payment button
```

#### 4.2 Discount Management (1.5 hours)
```java
// Create/Update these files:
// 1. services/DiscountService.java
//    - BigDecimal calculateDiscount(BigDecimal subtotal, double percentage, User user)
//    - void applyDiscount(Bill bill, double percentage, User user)
//    - Enforce role caps: Admin max 15%, Manager max 30%
// 2. Update AdminCheckoutController.java
//    - Add discount input field
//    - Validate against role caps
//    - Log discount application
// 3. Update AdminCheckout.fxml
//    - Discount percentage field
//    - Apply discount button
```

#### 4.3 Feedback Management (1.5 hours)
```java
// Create/Update these files:
// 1. view/fxml/FeedbackSubmission.fxml
//    - Star rating selector (1-5)
//    - Comment text area
//    - Submit button
// 2. controller/FeedbackSubmissionController.java
//    - Load reservation details
//    - Submit feedback via FeedbackService
//    - Show confirmation
// 3. Update AdminFeedbackController.java
//    - Display all feedback in table
//    - Filter by rating, date
//    - Show average rating
//    - Export button (later phase)
// 4. Update AdminFeedback.fxml
//    - Table view for feedback
//    - Filter controls
```

#### 4.4 Waitlist Management (1 hour)
```java
// Update these files:
// 1. Update AdminWaitlistController.java
//    - Display waitlist entries in table
//    - Add to waitlist form
//    - Convert to reservation button
//    - Integration with observer pattern
// 2. Update AdminWaitlist.fxml
//    - Waitlist table
//    - Add waitlist form
//    - Action buttons
```

**Acceptance Criteria:**
- Admin can process payments with all methods
- Role-based discount caps enforced
- Guests can submit feedback after checkout
- Admin can view and manage feedback
- Admin can add guests to waitlist
- Waitlist integrates with observer notifications

---

## 🔵 PHASE 5: Reporting & Export (PRIORITY 5)
**Goal:** Implement report generation and export to CSV/PDF  
**Estimated Time:** 4-5 hours  
**Impact:** +10% to grade  
**Status:** Required for reporting requirement

### Tasks:

#### 5.1 Add Export Dependencies (0.5 hours)
```xml
// Update pom.xml:
<!-- CSV Export -->
<dependency>
    <groupId>com.opencsv</groupId>
    <artifactId>opencsv</artifactId>
    <version>5.7.1</version>
</dependency>
<!-- PDF Export -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext7-core</artifactId>
    <version>7.2.5</version>
    <type>pom</type>
</dependency>
```

#### 5.2 Export Utilities (2 hours)
```java
// Create these files:
// 1. util/CsvExporter.java
//    - void exportToCsv(List<Map<String, String>> data, String filename)
// 2. util/PdfExporter.java
//    - void exportToPdf(List<Map<String, String>> data, String filename, String title)
// 3. util/TxtExporter.java
//    - void exportToTxt(String content, String filename)
```

#### 5.3 Report Service (1.5 hours)
```java
// Create this file:
// 1. services/ReportService.java
//    - List<Map<String, String>> generateRevenueReport(LocalDate start, end)
//    - List<Map<String, String>> generateOccupancyReport(LocalDate start, end)
//    - List<Map<String, String>> generateActivityLogReport(LocalDate start, end)
//    - Map<String, Object> generateFeedbackSummary()
```

#### 5.4 Admin Reports Controller (1 hour)
```java
// Update these files:
// 1. Update AdminReportsController.java
//    - Load report data
//    - Display in TableView
//    - Export to CSV button
//    - Export to PDF button
// 2. Update AdminReports.fxml
//    - Report type selector (Revenue/Occupancy/Activity)
//    - Date range pickers
//    - Generate report button
//    - Export buttons
//    - TableView for results
```

**Acceptance Criteria:**
- Revenue reports show period, count, subtotal, tax, total
- Occupancy reports show rooms available, occupied, percentage
- Activity logs exportable to CSV/TXT
- Feedback summary shows average rating and counts
- Reports exportable to CSV and PDF

---

## 🟣 PHASE 6: Polish & Enhancement (PRIORITY 6)
**Goal:** Add validation, loyalty features, and logging configuration  
**Estimated Time:** 3-4 hours  
**Impact:** +10% to grade  
**Status:** Nice to have, improves quality

### Tasks:

#### 6.1 File Logging Configuration (1 hour)
```java
// Create this file:
// 1. util/LoggerConfig.java
//    - Configure FileHandler with 1MB rotation, 10 files
//    - Pattern: system_logs.%g.log
// Example:
FileHandler fileHandler = new FileHandler("system_logs.%g.log", 1024 * 1024, 10, true);
fileHandler.setFormatter(new SimpleFormatter());
Logger.getGlobal().addHandler(fileHandler);
```

#### 6.2 Validation Utilities (1 hour)
```java
// Create this file:
// 1. util/ValidationUtil.java
//    - boolean isValidEmail(String email) - regex check
//    - boolean isValidPhone(String phone) - format check
//    - boolean isValidDateRange(LocalDate start, end) - check future dates
//    - boolean isValidOccupancy(List<Room> rooms, int adults, int children)
// 2. Apply to all controllers (ReservationDetailsController, etc.)
```

#### 6.3 Loyalty Service (1.5 hours)
```java
// Create this file:
// 1. services/LoyaltyService.java
//    - void enrollGuest(Guest guest)
//    - void earnPoints(LoyaltyAccount account, BigDecimal paymentAmount)
//    - BigDecimal redeemPoints(LoyaltyAccount account, int points)
// 2. Update GuestCheckoutController - offer loyalty enrollment
// 3. Update PaymentService - call earnPoints on payment
// 4. Update AdminLoyaltyController - show loyalty dashboard
// 5. Update AdminLoyalty.fxml - display loyalty accounts and transactions
```

#### 6.4 Enhanced Error Handling (0.5 hours)
```java
// Update all controllers:
// - Wrap database operations in try-catch
// - Show user-friendly error messages
// - Log exceptions with stack traces
```

**Acceptance Criteria:**
- File logging with rotation configured
- All forms validate input
- Guests can enroll in loyalty program
- Points earned on payment
- Admin can view loyalty accounts

---

## 🏁 PHASE 7: Testing, Documentation & Submission (PRIORITY 7)
**Goal:** Test everything, create documentation, record video, submit  
**Estimated Time:** 6-7 hours  
**Impact:** Required for submission  
**Status:** Final phase before deadline

### Tasks:

#### 7.1 End-to-End Testing (2 hours)
**Test Checklist:**
- [ ] Kiosk: Complete booking flow (Welcome → Confirmation)
- [ ] Kiosk: Add-ons using Decorator pattern
- [ ] Admin: Login with BCrypt authentication
- [ ] Admin: Search and view reservations
- [ ] Admin: Process payment (cash, card, points)
- [ ] Admin: Apply discount with role caps
- [ ] Admin: Check out guest
- [ ] Observer: Room availability notification
- [ ] Admin: View waitlist entries
- [ ] Admin: Add guest to waitlist
- [ ] Guest: Submit feedback after checkout
- [ ] Admin: View feedback
- [ ] Admin: Generate revenue report
- [ ] Admin: Export report to CSV
- [ ] Admin: Export report to PDF
- [ ] Logging: File rotation working
- [ ] Loyalty: Enroll guest
- [ ] Loyalty: Earn and redeem points
- [ ] All validations working

#### 7.2 Documentation (2-3 hours)
```markdown
// Create ProjectDocumentation.pdf with:
// 1. Project Overview (system purpose, features, technologies)
// 2. Architecture Summary (3-tier, MVC, DI, ORM)
// 3. Design Artifacts (include UML diagrams from UML folder)
// 4. Entity & Relationship Mapping (list entities, relationships, annotations)
// 5. Pattern Usage:
//    - Singleton: EntityManagerFactory
//    - Factory: RoomFactory for room types
//    - Strategy: PricingStrategy for weekend/weekday pricing
//    - Observer: Waitlist notifications
//    - Decorator: Add-on pricing
// 6. Business Rules (occupancy, pricing, discounts, loyalty, feedback)
// 7. Security & Logging (BCrypt, session management, file rotation)
// 8. Export & Reporting (CSV, PDF, report types)
// 9. Team Challenges & Learnings (overall challenges, solutions)
// 10. Individual Reflections (each member: their work, challenges, learnings)
```

#### 7.3 Video Recording (1.5 hours)
**Video Structure (minimum 3 minutes):**
1. Introduction (team members, project overview)
2. Kiosk Demonstration (complete booking flow)
3. Admin Login (show authentication)
4. Admin Features (payment, discount, checkout)
5. Observer Pattern Demo (show notification after checkout)
6. Decorator Pattern Demo (explain add-on pricing calculation)
7. Reporting Demo (generate and export report)
8. Each member explains their contribution
9. Conclusion (challenges overcome)

#### 7.4 Final Submission (1 hour)
**Submit:**
- [ ] Zipped project (entire MalibuLuminaHotel folder)
- [ ] ProjectDocumentation.pdf
- [ ] Team video file
- [ ] Database design diagrams (from UML folder)
- [ ] README.md updated with setup instructions
- [ ] Verify all files in submission portal

**Acceptance Criteria:**
- All tests passing
- Documentation complete with all required sections
- Video demonstrates all features and patterns
- All files submitted before deadline

---

## Recommended Phase Progression

### ⏱️ Time Allocation by Phase
- **Phase 1 (Critical Patterns):** 4-5 hours → Complete ASAP
- **Phase 2 (Security):** 3-4 hours → Complete by end of Day 1
- **Phase 3 (Core Entities):** 3-4 hours → Complete by middle of Day 2
- **Phase 4 (Admin Functionality):** 5-6 hours → Complete by end of Day 2
- **Phase 5 (Reporting):** 4-5 hours → Complete by middle of Day 3
- **Phase 6 (Polish):** 3-4 hours → Complete by end of Day 3
- **Phase 7 (Testing & Docs):** 6-7 hours → Complete by deadline

**Total Estimated Time:** 28-35 hours over 3 days (9-12 hours/day with 3 people)

---

## Team Work Distribution by Phase

### 👤 Person 1: Patterns & Security Lead
**Primary Phases:** 1, 2
- Phase 1: Observer Pattern implementation
- Phase 2: User entity, Authentication, BCrypt, Login UI
- Phase 6: File logging configuration
- Phase 7: Testing support

**Secondary:** Assist with Phase 4 (Payment processing)

### 👤 Person 2: Core Logic & Admin Features Lead
**Primary Phases:** 1, 4, 6
- Phase 1: Decorator Pattern implementation
- Phase 4: Payment service, Discount service
- Phase 6: Validation utilities, Loyalty service
- Phase 7: Documentation

**Secondary:** Assist with Phase 3 (Entity creation)

### 👤 Person 3: Entities & Reporting Lead
**Primary Phases:** 3, 5
- Phase 3: Feedback, Waitlist, ActivityLog entities
- Phase 5: Export utilities, Report service, Admin Reports
- Phase 4: Feedback UI, Waitlist UI
- Phase 7: Video recording lead

---

## Phase Completion Checklist

### Phase 1: Critical Patterns ⚠️ BLOCKING
- [ ] events/RoomAvailabilityObserver.java
- [ ] events/RoomAvailabilitySubject.java
- [ ] events/AdminNotificationManager.java
- [ ] events/WaitlistNotificationObserver.java
- [ ] decorators/BookingComponent.java
- [ ] decorators/BaseBooking.java
- [ ] decorators/AddOnDecorator.java
- [ ] decorators/BreakfastDecorator.java
- [ ] decorators/WifiDecorator.java
- [ ] decorators/ParkingDecorator.java
- [ ] decorators/SpaDecorator.java
- [ ] Update BookingService.java to use Decorator

**Total:** 12 tasks (4 Observer + 7 Decorator + 1 integration)

### Phase 2: Security & Authentication
- [ ] models/User.java
- [ ] repositories/IUserRepository.java
- [ ] repositories/UserRepositoryImpl.java
- [ ] security/PasswordHasher.java
- [ ] security/AuthenticationService.java
- [ ] security/SessionManager.java
- [ ] view/fxml/Login.fxml
- [ ] controller/LoginController.java
- [ ] Update WelcomeController.java
- [ ] Update DataSeeder.java (add admin users)
- [ ] Update persistence.xml (add User)

**Total:** 11 tasks

### Phase 3: Core Entities
- [ ] models/Feedback.java
- [ ] repositories/IFeedbackRepository.java
- [ ] repositories/FeedbackRepositoryImpl.java
- [ ] services/FeedbackService.java
- [ ] models/Waitlist.java
- [ ] repositories/IWaitlistRepository.java
- [ ] repositories/WaitlistRepositoryImpl.java
- [ ] services/WaitlistService.java
- [ ] models/ActivityLog.java
- [ ] repositories/IActivityLogRepository.java
- [ ] repositories/ActivityLogRepositoryImpl.java
- [ ] services/ActivityLogService.java
- [ ] Update persistence.xml (add 3 entities)

**Total:** 13 tasks

### Phase 4: Admin Functionality
- [ ] services/PaymentService.java
- [ ] Update AdminCheckoutController.java (payment logic)
- [ ] Update AdminCheckout.fxml (payment UI)
- [ ] services/DiscountService.java
- [ ] Update AdminCheckoutController.java (discount logic)
- [ ] Update AdminCheckout.fxml (discount UI)
- [ ] view/fxml/FeedbackSubmission.fxml
- [ ] controller/FeedbackSubmissionController.java
- [ ] Update AdminFeedbackController.java
- [ ] Update AdminFeedback.fxml
- [ ] Update AdminWaitlistController.java
- [ ] Update AdminWaitlist.fxml

**Total:** 12 tasks

### Phase 5: Reporting & Export
- [ ] Update pom.xml (add OpenCSV, iText)
- [ ] util/CsvExporter.java
- [ ] util/PdfExporter.java
- [ ] util/TxtExporter.java
- [ ] services/ReportService.java
- [ ] Update AdminReportsController.java
- [ ] Update AdminReports.fxml

**Total:** 7 tasks

### Phase 6: Polish & Enhancement
- [ ] util/LoggerConfig.java
- [ ] util/ValidationUtil.java
- [ ] services/LoyaltyService.java
- [ ] Update GuestCheckoutController.java (loyalty enrollment)
- [ ] Update PaymentService.java (earn points)
- [ ] Update AdminLoyaltyController.java
- [ ] Update AdminLoyalty.fxml
- [ ] Add error handling to all controllers

**Total:** 8 tasks

### Phase 7: Testing & Documentation
- [ ] Complete end-to-end testing checklist
- [ ] Write ProjectDocumentation.pdf
- [ ] Write team reflections
- [ ] Record demonstration video
- [ ] Update README.md
- [ ] Submit all files


### NEVER Skip These (Will Result in Failure)
**From Phase 1:**
- ✅ Observer pattern - MANDATORY
- ✅ Decorator pattern - MANDATORY

**From Phase 2:**
- ✅ User entity - MANDATORY
- ✅ BCrypt authentication - MANDATORY
- ✅ Login functionality - MANDATORY

**From Phase 3:**
- ✅ At least Feedback entity - MANDATORY
- ✅ At least Waitlist entity - MANDATORY

**From Phase 5:**
- ✅ At least one report type - MANDATORY
- ✅ At least CSV export - MANDATORY

**From Phase 7:**
- ✅ Documentation - MANDATORY
- ✅ Video - MANDATORY
- ✅ Submission - MANDATORY

---

## Testing Strategy (Phase 7)

### Incremental Testing by Phase
Test each phase as you complete it - don't wait until the end!

**Phase 1 Testing:**
- [ ] Observer: Checkout triggers notification
- [ ] Decorator: Add-ons wrap base booking cost correctly
- [ ] Decorator: Bill shows correct total with all add-ons

**Phase 2 Testing:**
- [ ] User entity persists to database
- [ ] BCrypt hashes passwords correctly
- [ ] Login accepts valid credentials
- [ ] Login rejects invalid credentials
- [ ] Session tracks current user

**Phase 3 Testing:**
- [ ] Feedback entity persists with reservation link
- [ ] Waitlist entity persists with room type link
- [ ] ActivityLog entity persists with user link
- [ ] All repositories perform CRUD operations

**Phase 4 Testing:**
- [ ] Payment processes correctly for cash/card/points
- [ ] Discount applies with correct role caps
- [ ] Admin 15% cap enforced
- [ ] Manager 30% cap enforced
- [ ] Feedback submission saves to database
- [ ] Waitlist creation integrates with observer

**Phase 5 Testing:**
- [ ] Revenue report generates correct data
- [ ] Occupancy report calculates percentages
- [ ] CSV export creates valid file
- [ ] PDF export creates valid file
- [ ] Exported data matches screen data

**Phase 6 Testing:**
- [ ] File logging creates rotated files
- [ ] Validation catches invalid inputs
- [ ] Loyalty enrollment creates account
- [ ] Points earned on payment
- [ ] Error messages display properly

### Final Integration Testing (Phase 7)

#### Kiosk Flow Test (15 mins)
1. [ ] Welcome screen loads
2. [ ] Select dates: Aug 10-12, 2 adults
3. [ ] Room suggestions appear correctly
4. [ ] Select 1 Single Room
5. [ ] Enter guest details with validation
6. [ ] Select add-ons: Breakfast + WiFi
7. [ ] Checkout shows itemized bill with decorator pricing
8. [ ] Confirm reservation
9. [ ] Verify saved to database

#### Admin Flow Test (30 mins)
1. [ ] Login as admin (username: admin)
2. [ ] Dashboard displays reservations
3. [ ] Search for guest by name
4. [ ] View reservation details
5. [ ] Process payment: $300 cash
6. [ ] Apply 10% discount (admin cap test)
7. [ ] Try 20% discount (should fail admin cap)
8. [ ] Checkout guest
9. [ ] Verify observer notification sent
10. [ ] View activity log entries
11. [ ] Add another guest to waitlist
12. [ ] View feedback (none yet)
13. [ ] Generate revenue report
14. [ ] Export report to CSV
15. [ ] Logout

#### Pattern Verification Test (15 mins)
1. [ ] **Singleton:** Verify single EntityManagerFactory instance
2. [ ] **Factory:** Room creation via RoomFactory
3. [ ] **Strategy:** Weekend pricing applied correctly
4. [ ] **Observer:** Waitlist notification on checkout
5. [ ] **Decorator:** Add-ons wrap booking component

#### Guest Feedback Test (5 mins)
1. [ ] Navigate to feedback submission
2. [ ] Enter reservation ID
3. [ ] Select 5-star rating
4. [ ] Write comment
5. [ ] Submit feedback
6. [ ] Verify admin can see feedback

---

## Final Reminders

### Critical Success Factors
1. **Start with Phase 1** - Patterns are non-negotiable
2. **Test each phase** - Don't accumulate technical debt
3. **Communicate often** - No silent struggles
4. **Focus on completion** - Perfect is the enemy of done
5. **Document as you go** - Don't save all docs for last day

### Red Flags (Stop and Regroup If...)
- ⛔ Phase 1 not complete by end of Day 1
- ⛔ Phase 2 authentication not working by end of Day 1
- ⛔ Merge conflicts piling up
- ⛔ Team member stuck for more than 2 hours
- ⛔ Not starting Phase 7 by Day 4 morning

### Green Lights (You're On Track If...)
- ✅ Phases 1-2 done by end of Day 1
- ✅ Phases 3-4 done by end of Day 2
- ✅ Phases 5-6 done by end of Day 3
- ✅ All tests passing by Day 3 evening
- ✅ Documentation started by Day 4 morning

---

**Remember:** 
- A working system with all required patterns beats a fancy incomplete system
- Phase 1 (Patterns) is your top priority - everything else depends on it
- You have the skills and the plan - now execute systematically
- Help each other when blocked - don't suffer in silence

**You've got this! 🚀 Now go build an amazing hotel reservation system!**

---

*Last Updated: August 4, 2026*  
*Phases: 7 | Tasks: 69 | Estimated Time: 28-35 hours | Team Size: 3*
