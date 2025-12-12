package fr.emse.tb3pwme.project.web;

import java.time.LocalDate;

record DeliveryToCreateRepresentation(LocalDate scheduledDate, String recipient, String street, String city, String zipCode, String country) {
}
