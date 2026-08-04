package ca.senecacollege.malibuluminahotel.models;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ActivityLog entity - represents admin activity logging for audit trail.
 * Matches ERD: log_id, Reservationreservation_id, timestamp, action,
 * entity, entity_id, message, AdminUseradmin_jd
 */
@Entity
@Table(name = "ActivityLog")
public class ActivityLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AdminUseradmin_jd", nullable = false)
    private AdminUser adminUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Reservationreservation_id")
    private Reservation reservation;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false, length = 255)
    private String action;

    @Column(length = 255)
    private String entity;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(length = 255)
    private String message;

    // Constructors
    public ActivityLog() {
        this.timestamp = LocalDateTime.now();
    }

    public ActivityLog(AdminUser adminUser, String action, String entity, Long entityId, String message) {
        this();
        this.adminUser = adminUser;
        this.action = action;
        this.entity = entity;
        this.entityId = entityId;
        this.message = message;
    }

    public ActivityLog(AdminUser adminUser, Reservation reservation, String action, String message) {
        this();
        this.adminUser = adminUser;
        this.reservation = reservation;
        this.action = action;
        this.entity = "Reservation";
        this.entityId = reservation != null ? reservation.getReservationId() : null;
        this.message = message;
    }

    /**
     * Record method as per ERD class diagram.
     * Records the activity log entry.
     */
    public void record() {
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public AdminUser getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(AdminUser adminUser) {
        this.adminUser = adminUser;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
