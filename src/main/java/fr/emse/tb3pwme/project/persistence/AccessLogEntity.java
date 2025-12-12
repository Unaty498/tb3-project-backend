package fr.emse.tb3pwme.project.persistence;

import fr.emse.tb3pwme.project.domain.AccessResult;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "access_logs")
public class AccessLogEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID badgeId;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID doorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccessResult result;

    @Column(length = 500)
    private String details;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public AccessLogEntity() {
    }

    public AccessLogEntity(UUID id, UUID badgeId, UUID userId, UUID doorId,
                           AccessResult result, String details, LocalDateTime timestamp) {
        this.id = id;
        this.badgeId = badgeId;
        this.userId = userId;
        this.doorId = doorId;
        this.result = result;
        this.details = details;
        this.timestamp = timestamp;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(UUID badgeId) {
        this.badgeId = badgeId;
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

    public AccessResult getResult() {
        return result;
    }

    public void setResult(AccessResult result) {
        this.result = result;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

