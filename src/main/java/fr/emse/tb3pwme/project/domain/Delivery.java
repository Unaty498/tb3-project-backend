package fr.emse.tb3pwme.project.domain;

import java.time.LocalDate;
import java.util.UUID;

public class Delivery {

    private final UUID id;

    private final Status status;

    private final LocalDate scheduledDate;

    private final LocalDate deliveredAt;

    private final String recipient;

    private final Address address;

    private final boolean rescheduleAllowed;

    public Delivery(UUID id, Status status, LocalDate scheduledDate, LocalDate deliveredAt, String recipient, Address address, boolean rescheduleAllowed) {
        this.id = id;
        this.status = status;
        this.scheduledDate = scheduledDate;
        this.deliveredAt = deliveredAt;
        this.recipient = recipient;
        this.address = address;
        this.rescheduleAllowed = rescheduleAllowed;
    }

    /**
     * Create a new delivery.
     *
     * @param scheduledDate the scheduled date
     * @param recipient     the recipient
     * @param address       the address
     * @return the delivery
     */
    public static Delivery newDelivery(LocalDate scheduledDate, String recipient, Address address) {
        return new Delivery(UUID.randomUUID(), Status.SCHEDULED, scheduledDate, null, recipient, address, true);
    }

    /**
     * Reschedule the delivery at a {@code newScheduledDate}.
     *
     * @param newScheduledDate the new scheduled date
     * @return the updated delivery
     */
    public Delivery reschedule(LocalDate newScheduledDate) {
        if (!rescheduleAllowed || status == Status.DELIVERED) {
            throw new IllegalStateException("Rescheduling delivery is not allowed.");
        }
        if (newScheduledDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot reschedule for a date earlier than now.");
        }
        return new Delivery(id, status, newScheduledDate, deliveredAt, recipient, address, false);
    }

    /**
     * Complete the delivery.
     *
     * @return the updated delivery
     */
    public Delivery complete() {
        if (status == Status.DELIVERED) {
            throw new IllegalStateException("Cannot complete a delivery that has already been delivered.");
        }
        return new Delivery(id, Status.DELIVERED, scheduledDate, LocalDate.now(), recipient, address, false);
    }

    public UUID getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public LocalDate getDeliveredAt() {
        return deliveredAt;
    }

    public String getRecipient() {
        return recipient;
    }

    public Address getAddress() {
        return address;
    }

    public boolean isRescheduleAllowed() {
        return rescheduleAllowed;
    }

}
