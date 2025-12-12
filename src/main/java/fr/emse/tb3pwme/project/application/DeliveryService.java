package fr.emse.tb3pwme.project.application;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import fr.emse.tb3pwme.project.domain.Delivery;
import fr.emse.tb3pwme.project.persistence.DeliveryEntity;
import fr.emse.tb3pwme.project.persistence.DeliveryRepository;
import org.springframework.stereotype.Service;

@Service
public class DeliveryService {

    private final DeliveryRepository repository;

    DeliveryService(DeliveryRepository repository) {
        this.repository = repository;
    }

    /**
     * Save the delivery.
     *
     * @param delivery the delivery to save
     */
    public void save(Delivery delivery) {

        repository.save(DeliveryEntity.fromDomain(delivery));
        
    }

    /**
     * Reschedule the delivery with the specified {@code id} to another date.
     *
     * @param id               the delivery id
     * @param newScheduledDate the new scheduled date
     * @return the updated delivery
     */
    public Delivery reschedule(UUID id, LocalDate newScheduledDate) {
        Delivery delivery = getDelivery(id);
        delivery = delivery.reschedule(newScheduledDate);

        save(delivery);
        return delivery;
    }

    /**
     * Complete the delivery with the specified {@code id}.
     *
     * @param id the delivery id
     * @return the updated delivery
     */
    public Delivery complete(UUID id) {
        Delivery delivery = getDelivery(id);
        delivery = delivery.complete();
        save(delivery);
        return delivery;
    }

    /**
     * Get the delivery with the specified {@code id}.
     *
     * @param id the delivery id
     * @return the delivery
     */
    public Delivery getDelivery(UUID id) {
        Optional<DeliveryEntity> delivery = repository.findById(id);
        if (delivery.isPresent()) {
            return delivery.get().toDomain();
        }
        throw new IllegalArgumentException();
    }

    /**
     * Get all the deliveries.
     *
     * @return the deliveries
     */
    public List<Delivery> getDeliveries() {
        return repository.findAll().stream().map(DeliveryEntity::toDomain).collect(Collectors.toList());
    }

}
