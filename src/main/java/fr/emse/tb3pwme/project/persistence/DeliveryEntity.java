package fr.emse.tb3pwme.project.persistence;

import fr.emse.tb3pwme.project.domain.Delivery;
import fr.emse.tb3pwme.project.domain.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "deliveries")
public class DeliveryEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Temporal(TemporalType.DATE)
    private LocalDate scheduledDate;

    @Temporal(TemporalType.DATE)
    private LocalDate deliveredAt;

    @Column(nullable = false)
    private String recipient;

    @Embedded
    private AddressEmbeddable address;

    private boolean rescheduleAllowed;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public LocalDate getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDate deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public AddressEmbeddable getAddress() {
        return address;
    }

    public void setAddress(AddressEmbeddable address) {
        this.address = address;
    }

    public boolean isRescheduleAllowed() {
        return rescheduleAllowed;
    }

    public void setRescheduleAllowed(boolean rescheduleAllowed) {
        this.rescheduleAllowed = rescheduleAllowed;
    }

    public static DeliveryEntity fromDomain(Delivery delivery) {
        DeliveryEntity entity = new DeliveryEntity();
        entity.setId(delivery.getId());
        entity.setStatus(delivery.getStatus());
        entity.setScheduledDate(delivery.getScheduledDate());
        entity.setDeliveredAt(delivery.getDeliveredAt());
        entity.setRecipient(delivery.getRecipient());
        entity.setAddress(AddressEmbeddable.fromDomain(delivery.getAddress()));
        entity.setRescheduleAllowed(delivery.isRescheduleAllowed());
        return entity;
    }

    public Delivery toDomain() {
        return new Delivery(id, status, scheduledDate, deliveredAt, recipient, address.toDomain(), rescheduleAllowed);
    }

}
