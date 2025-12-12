package fr.emse.tb3pwme.project.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BadgeRepository extends JpaRepository<BadgeEntity, UUID> {
    Optional<BadgeEntity> findByBadgeNumber(String badgeNumber);
    List<BadgeEntity> findByUserId(UUID userId);
}

