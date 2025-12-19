package fr.emse.tb3pwme.project.persistence;

import fr.emse.tb3pwme.project.domain.BadgeType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "badges")
public class BadgeEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String badgeNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BadgeType type;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private boolean active;

    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean physicallyMapped;

    public BadgeEntity() {
    }

    public BadgeEntity(UUID id, String badgeNumber, BadgeType type, UUID userId,
                       boolean active, LocalDateTime expiryDate,
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.badgeNumber = badgeNumber;
        this.type = type;
        this.userId = userId;
        this.active = active;
        this.expiryDate = expiryDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.physicallyMapped = false;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBadgeNumber() {
        return badgeNumber;
    }

    public void setBadgeNumber(String badgeNumber) {
        this.badgeNumber = badgeNumber;
    }

    public BadgeType getType() {
        return type;
    }

    public void setType(BadgeType type) {
        this.type = type;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isPhysicallyMapped() {
        return physicallyMapped;
    }

    public void setPhysicallyMapped(boolean physicallyMapped) {
        this.physicallyMapped = physicallyMapped;
    }
}

