package fr.emse.tb3pwme.project.application;

import fr.emse.tb3pwme.project.domain.AccessRule;
import fr.emse.tb3pwme.project.domain.TimeSlot;
import fr.emse.tb3pwme.project.persistence.AccessRuleRepository;
import fr.emse.tb3pwme.project.persistence.EntityMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccessRuleService {
    private final AccessRuleRepository accessRuleRepository;
    private final EntityMapper mapper;

    public AccessRuleService(AccessRuleRepository accessRuleRepository, EntityMapper mapper) {
        this.accessRuleRepository = accessRuleRepository;
        this.mapper = mapper;
    }

    public AccessRule createAccessRule(UUID userId, UUID doorId, List<TimeSlot> timeSlots) {
        AccessRule accessRule = AccessRule.newAccessRule(userId, doorId, timeSlots);
        return mapper.toDomain(accessRuleRepository.save(mapper.toEntity(accessRule)));
    }

    public Optional<AccessRule> findById(UUID id) {
        return accessRuleRepository.findById(id).map(mapper::toDomain);
    }

    public List<AccessRule> findByUserId(UUID userId) {
        return accessRuleRepository.findByUserId(userId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    public List<AccessRule> findByDoorId(UUID doorId) {
        return accessRuleRepository.findByDoorId(doorId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    public List<AccessRule> findActiveRules(UUID userId, UUID doorId) {
        return accessRuleRepository.findByUserIdAndDoorIdAndActive(userId, doorId, true).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    public List<AccessRule> findAll() {
        return accessRuleRepository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    public AccessRule updateTimeSlots(UUID id, List<TimeSlot> newTimeSlots) {
        AccessRule accessRule = findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Access rule not found"));

        AccessRule updatedRule = accessRule.updateTimeSlots(newTimeSlots);
        return mapper.toDomain(accessRuleRepository.save(mapper.toEntity(updatedRule)));
    }

    public AccessRule deactivateRule(UUID id) {
        AccessRule accessRule = findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Access rule not found"));

        AccessRule deactivatedRule = accessRule.deactivate();
        return mapper.toDomain(accessRuleRepository.save(mapper.toEntity(deactivatedRule)));
    }

    public AccessRule activateRule(UUID id) {
        AccessRule accessRule = findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Access rule not found"));

        AccessRule activatedRule = accessRule.activate();
        return mapper.toDomain(accessRuleRepository.save(mapper.toEntity(activatedRule)));
    }

    public void deleteRule(UUID id) {
        accessRuleRepository.deleteById(id);
    }
}

