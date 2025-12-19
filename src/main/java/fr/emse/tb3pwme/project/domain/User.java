package fr.emse.tb3pwme.project.domain;
import java.time.LocalDateTime;
import java.util.UUID;
public class User {
    private final UUID id;
    private final String keycloakId;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String phone;
    private final UserRole role;
    private final boolean active;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    public User(UUID id, String keycloakId, String email, String firstName, String lastName, String phone,
                UserRole role, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.keycloakId = keycloakId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.role = role;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public static User newUser(String keycloakId, String email, String firstName, String lastName, String phone, UserRole role) {
        LocalDateTime now = LocalDateTime.now();
        return new User(UUID.randomUUID(), keycloakId, email, firstName, lastName, phone, role, true, now, now);
    }
    public User deactivate() {
        if (!active) {
            throw new IllegalStateException("User is already deactivated.");
        }
        return new User(id, keycloakId, email, firstName, lastName, phone, role, false, createdAt, LocalDateTime.now());
    }
    public User activate() {
        if (active) {
            throw new IllegalStateException("User is already active.");
        }
        return new User(id, keycloakId, email, firstName, lastName, phone, role, true, createdAt, LocalDateTime.now());
    }
    public User update(String firstName, String lastName, String phone) {
        return new User(id, keycloakId, email, firstName, lastName, phone, role, active, createdAt, LocalDateTime.now());
    }
    public UUID getId() {
        return id;
    }
    public String getKeycloakId() {
        return keycloakId;
    }
    public String getEmail() {
        return email;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getPhone() {
        return phone;
    }
    public UserRole getRole() {
        return role;
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
