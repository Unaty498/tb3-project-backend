package fr.emse.tb3pwme.project.application;

import fr.emse.tb3pwme.project.domain.AccessLog;
import fr.emse.tb3pwme.project.domain.AccessResult;
import fr.emse.tb3pwme.project.persistence.AccessLogRepository;
import fr.emse.tb3pwme.project.persistence.EntityMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccessLogService {
    private final AccessLogRepository accessLogRepository;
    private final EntityMapper mapper;

    public AccessLogService(AccessLogRepository accessLogRepository, EntityMapper mapper) {
        this.accessLogRepository = accessLogRepository;
        this.mapper = mapper;
    }

    public AccessLog logAccess(UUID badgeId, UUID userId, UUID doorId, AccessResult result, String details) {
        AccessLog accessLog = AccessLog.newAccessLog(badgeId, userId, doorId, result, details);
        return mapper.toDomain(accessLogRepository.save(mapper.toEntity(accessLog)));
    }

    public Optional<AccessLog> findById(UUID id) {
        return accessLogRepository.findById(id).map(mapper::toDomain);
    }

    public List<AccessLog> findByUserId(UUID userId) {
        return accessLogRepository.findByUserIdOrderByTimestampDesc(userId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    public List<AccessLog> findByDoorId(UUID doorId) {
        return accessLogRepository.findByDoorIdOrderByTimestampDesc(doorId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    public List<AccessLog> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return accessLogRepository.findByTimestampBetweenOrderByTimestampDesc(start, end).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    public List<AccessLog> findAll() {
        return accessLogRepository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
}

