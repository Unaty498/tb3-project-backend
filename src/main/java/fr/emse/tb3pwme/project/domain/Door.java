package fr.emse.tb3pwme.project.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Door {
    private final UUID id;
    private final String name;
    private final String location;
    private final String deviceId;
    private final boolean active;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public Door(UUID id, String name, String location, String deviceId,
                boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.deviceId = deviceId;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Door newDoor(String name, String location, String deviceId) {
        LocalDateTime now = LocalDateTime.now();
        return new Door(UUID.randomUUID(), name, location, deviceId, true, now, now);
    }

    public Door deactivate() {
        if (!active) {
            throw new IllegalStateException("Door is already deactivated.");
        }
        return new Door(id, name, location, deviceId, false, createdAt, LocalDateTime.now());
    }

    public Door activate() {
        if (active) {
            throw new IllegalStateException("Door is already active.");
        }
        return new Door(id, name, location, deviceId, true, createdAt, LocalDateTime.now());
    }

    public Door update(String name, String location) {
        return new Door(id, name, location, deviceId, active, createdAt, LocalDateTime.now());
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getDeviceId() {
        return deviceId;
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

