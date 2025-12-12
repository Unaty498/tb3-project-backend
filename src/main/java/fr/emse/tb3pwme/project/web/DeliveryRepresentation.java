package fr.emse.tb3pwme.project.web;

import fr.emse.tb3pwme.project.domain.Delivery;
import fr.emse.tb3pwme.project.domain.Status;

import java.time.LocalDate;
import java.util.UUID;

record DeliveryRepresentation(UUID id, Status status, LocalDate scheduledDate, LocalDate deliveredAt, String recipient, String street, String city, String zipCode, String country, boolean rescheduleAllowed) {

    public static DeliveryRepresentation fromDomain(Delivery delivery) {
        return new DeliveryRepresentation(delivery.getId(), delivery.getStatus(), delivery.getScheduledDate(), delivery.getDeliveredAt(), delivery.getRecipient(),
                delivery.getAddress().getStreet(), delivery.getAddress().getCity(), delivery.getAddress().getZipCode(), delivery.getAddress().getCountry(),
                delivery.isRescheduleAllowed());
    }

}
