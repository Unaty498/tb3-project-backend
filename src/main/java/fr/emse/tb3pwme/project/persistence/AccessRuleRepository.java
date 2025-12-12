package fr.emse.tb3pwme.project.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccessRuleRepository extends JpaRepository<AccessRuleEntity, UUID> {
    List<AccessRuleEntity> findByUserIdAndDoorIdAndActive(UUID userId, UUID doorId, boolean active);
    List<AccessRuleEntity> findByUserId(UUID userId);
    List<AccessRuleEntity> findByDoorId(UUID doorId);
}

