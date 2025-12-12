package fr.emse.tb3pwme.project.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class AccessLog {
    private final UUID id;
    private final UUID badgeId;
    private final UUID userId;
    private final UUID doorId;
    private final AccessResult result;
    private final String details;
    private final LocalDateTime timestamp;

    public AccessLog(UUID id, UUID badgeId, UUID userId, UUID doorId,
                     AccessResult result, String details, LocalDateTime timestamp) {
        this.id = id;
        this.badgeId = badgeId;
        this.userId = userId;
        this.doorId = doorId;
        this.result = result;
        this.details = details;
        this.timestamp = timestamp;
    }

    public static AccessLog newAccessLog(UUID badgeId, UUID userId, UUID doorId,
                                         AccessResult result, String details) {
        return new AccessLog(UUID.randomUUID(), badgeId, userId, doorId, result, details, LocalDateTime.now());
    }

    public UUID getId() {
        return id;
    }

    public UUID getBadgeId() {
        return badgeId;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getDoorId() {
        return doorId;
    }

    public AccessResult getResult() {
        return result;
    }

    public String getDetails() {
        return details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

