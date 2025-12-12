package fr.emse.tb3pwme.project.web;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.emse.tb3pwme.project.application.DeliveryService;
import fr.emse.tb3pwme.project.domain.Address;
import fr.emse.tb3pwme.project.domain.Delivery;

@RestController
@RequestMapping("/api/deliveries")
class DeliveryController {

    private final DeliveryService deliveryService;

    DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PostMapping
    ResponseEntity<DeliveryRepresentation> createDelivery(@RequestBody DeliveryToCreateRepresentation deliveryToCreate) {
        // Build domain object from representation
        Address address = new Address(deliveryToCreate.street(), deliveryToCreate.city(), deliveryToCreate.zipCode(), deliveryToCreate.country());
        Delivery delivery = Delivery.newDelivery(deliveryToCreate.scheduledDate(), deliveryToCreate.recipient(), address);

        // Persist
        deliveryService.save(delivery);

        // Build response with Location header
        URI location = URI.create("/api/deliveries/" + delivery.getId());
        return ResponseEntity.created(location).body(DeliveryRepresentation.fromDomain(delivery));
    }

    @GetMapping
    ResponseEntity<List<DeliveryRepresentation>> getDeliveries() {
        List<Delivery> deliveries = deliveryService.getDeliveries();
        return ResponseEntity.ok(deliveries.stream().map(DeliveryRepresentation::fromDomain).toList());
    }

    @GetMapping("/{id}")
    ResponseEntity<DeliveryRepresentation> getDelivery(@PathVariable("id") UUID id) {
        try {
            Delivery delivery = deliveryService.getDelivery(id);
            return ResponseEntity.ok(DeliveryRepresentation.fromDomain(delivery));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/reschedule")
    ResponseEntity<DeliveryRepresentation> reschedule(@PathVariable("id") UUID id, @RequestBody RescheduleRequestRepresentation rescheduleRequest) {
        LocalDate date = rescheduleRequest.newScheduledDate();
        try {
            return ResponseEntity.ok(DeliveryRepresentation.fromDomain(deliveryService.reschedule(id, date)));
        } catch (Exception e) {
            return ResponseEntity.status(409).build();
        }
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasRole('ROLE_POSTMAN')")
    ResponseEntity<DeliveryRepresentation> complete(@PathVariable("id") UUID id) {

        try {
            return ResponseEntity.ok(DeliveryRepresentation.fromDomain(deliveryService.complete(id)));
        } catch (Exception e) {
            return ResponseEntity.status(409).build();
        }
    }

}
