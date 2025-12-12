package fr.emse.tb3pwme.project.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AccessLogRepository extends JpaRepository<AccessLogEntity, UUID> {
    List<AccessLogEntity> findByUserIdOrderByTimestampDesc(UUID userId);
    List<AccessLogEntity> findByDoorIdOrderByTimestampDesc(UUID doorId);
    List<AccessLogEntity> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime start, LocalDateTime end);
}

