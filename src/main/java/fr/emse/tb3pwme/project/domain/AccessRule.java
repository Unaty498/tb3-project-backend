package fr.emse.tb3pwme.project.domain;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class AccessRule {
    private final UUID id;
    private final UUID userId;
    private final UUID doorId;
    private final List<TimeSlot> timeSlots;
    private final boolean active;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public AccessRule(UUID id, UUID userId, UUID doorId, List<TimeSlot> timeSlots,
                      boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.doorId = doorId;
        this.timeSlots = timeSlots != null ? List.copyOf(timeSlots) : Collections.emptyList();
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AccessRule newAccessRule(UUID userId, UUID doorId, List<TimeSlot> timeSlots) {
        LocalDateTime now = LocalDateTime.now();
        return new AccessRule(UUID.randomUUID(), userId, doorId, timeSlots, true, now, now);
    }

    public AccessRule deactivate() {
        if (!active) {
            throw new IllegalStateException("Access rule is already deactivated.");
        }
        return new AccessRule(id, userId, doorId, timeSlots, false, createdAt, LocalDateTime.now());
    }

    public AccessRule activate() {
        if (active) {
            throw new IllegalStateException("Access rule is already active.");
        }
        return new AccessRule(id, userId, doorId, timeSlots, true, createdAt, LocalDateTime.now());
    }

    public AccessRule updateTimeSlots(List<TimeSlot> newTimeSlots) {
        return new AccessRule(id, userId, doorId, newTimeSlots, active, createdAt, LocalDateTime.now());
    }

    public boolean isCurrentlyValid() {
        if (!active) {
            return false;
        }

        // Si pas de créneaux horaires, accès 24/7
        if (timeSlots.isEmpty()) {
            return true;
        }

        // Vérifier si au moins un créneau est actif
        return timeSlots.stream().anyMatch(TimeSlot::isCurrentlyActive);
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getDoorId() {
        return doorId;
    }

    public List<TimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}

