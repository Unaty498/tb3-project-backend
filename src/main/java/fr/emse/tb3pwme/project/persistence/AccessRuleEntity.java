package fr.emse.tb3pwme.project.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "access_rules")
public class AccessRuleEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID doorId;

    @ElementCollection
    @CollectionTable(name = "access_rule_time_slots", joinColumns = @JoinColumn(name = "access_rule_id"))
    private List<TimeSlotEmbeddable> timeSlots = new ArrayList<>();

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public AccessRuleEntity() {
    }

    public AccessRuleEntity(UUID id, UUID userId, UUID doorId, List<TimeSlotEmbeddable> timeSlots,
                            boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.doorId = doorId;
        this.timeSlots = timeSlots != null ? new ArrayList<>(timeSlots) : new ArrayList<>();
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getDoorId() {
        return doorId;
    }

    public void setDoorId(UUID doorId) {
        this.doorId = doorId;
    }

    public List<TimeSlotEmbeddable> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<TimeSlotEmbeddable> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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
}

