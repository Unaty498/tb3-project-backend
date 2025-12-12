package fr.emse.tb3pwme.project.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeliveryRepository extends JpaRepository<DeliveryEntity, UUID> {
}
