package fr.emse.tb3pwme.project.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Badge {
    private final UUID id;
    private final String badgeNumber;
    private final BadgeType type;
    private final UUID userId;
    private final boolean active;
    private final LocalDateTime expiryDate;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final boolean physicallyMapped;

    public Badge(UUID id, String badgeNumber, BadgeType type, UUID userId,
                 boolean active, LocalDateTime expiryDate,
                 LocalDateTime createdAt, LocalDateTime updatedAt, boolean physicallyMapped) {
        this.id = id;
        this.badgeNumber = badgeNumber;
        this.type = type;
        this.userId = userId;
        this.active = active;
        this.expiryDate = expiryDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.physicallyMapped = physicallyMapped;
    }

    public static Badge newBadge(String badgeNumber, BadgeType type, UUID userId, LocalDateTime expiryDate) {
        LocalDateTime now = LocalDateTime.now();
        return new Badge(UUID.randomUUID(), badgeNumber, type, userId, true, expiryDate, now, now, false);
    }

    public Badge deactivate() {
        if (!active) {
            throw new IllegalStateException("Badge is already deactivated.");
        }
        return new Badge(id, badgeNumber, type, userId, false, expiryDate, createdAt, LocalDateTime.now(), physicallyMapped);
    }

    public Badge activate() {
        if (active) {
            throw new IllegalStateException("Badge is already active.");
        }
        return new Badge(id, badgeNumber, type, userId, true, expiryDate, createdAt, LocalDateTime.now(), physicallyMapped);
    }

    public Badge updateExpiry(LocalDateTime newExpiryDate) {
        return new Badge(id, badgeNumber, type, userId, active, newExpiryDate, createdAt, LocalDateTime.now(), physicallyMapped);
    }

    public Badge updatePhysicalMapping(boolean newPhysicallyMapped) {
        return new Badge(id, badgeNumber, type, userId, active, expiryDate, createdAt, LocalDateTime.now(), newPhysicallyMapped);
    }

    public boolean isExpired() {
        return expiryDate != null && LocalDateTime.now().isAfter(expiryDate);
    }

    public boolean isValid() {
        return active && !isExpired();
    }

    public UUID getId() {
        return id;
    }

    public String getBadgeNumber() {
        return badgeNumber;
    }

    public BadgeType getType() {
        return type;
    }

    public UUID getUserId() {
        return userId;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isPhysicallyMapped() {
        return physicallyMapped;
    }
}
