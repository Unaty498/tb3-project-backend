package fr.emse.tb3pwme.project.application;

import fr.emse.tb3pwme.project.domain.Badge;
import fr.emse.tb3pwme.project.domain.BadgeType;
import fr.emse.tb3pwme.project.persistence.BadgeRepository;
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
public class BadgeService {
    private final BadgeRepository badgeRepository;
    private final EntityMapper mapper;

    public BadgeService(BadgeRepository badgeRepository, EntityMapper mapper) {
        this.badgeRepository = badgeRepository;
        this.mapper = mapper;
    }

    public Badge createBadge(String badgeNumber, BadgeType type, UUID userId, LocalDateTime expiryDate) {
        Badge badge = Badge.newBadge(badgeNumber, type, userId, expiryDate);
        return mapper.toDomain(badgeRepository.save(mapper.toEntity(badge)));
    }

    public Optional<Badge> findById(UUID id) {
        return badgeRepository.findById(id).map(mapper::toDomain);
    }

    public Optional<Badge> findByBadgeNumber(String badgeNumber) {
        return badgeRepository.findByBadgeNumber(badgeNumber).map(mapper::toDomain);
    }

    public List<Badge> findByUserId(UUID userId) {
        return badgeRepository.findByUserId(userId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    public List<Badge> findAll() {
        return badgeRepository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    public Badge updateExpiry(UUID id, LocalDateTime newExpiryDate) {
        Badge badge = findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Badge not found"));

        Badge updatedBadge = badge.updateExpiry(newExpiryDate);
        return mapper.toDomain(badgeRepository.save(mapper.toEntity(updatedBadge)));
    }

    public Badge deactivateBadge(UUID id) {
        Badge badge = findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Badge not found"));

        Badge deactivatedBadge = badge.deactivate();
        return mapper.toDomain(badgeRepository.save(mapper.toEntity(deactivatedBadge)));
    }

    public Badge activateBadge(UUID id) {
        Badge badge = findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Badge not found"));

        Badge activatedBadge = badge.activate();
        return mapper.toDomain(badgeRepository.save(mapper.toEntity(activatedBadge)));
    }

    public void deleteBadge(UUID id) {
        badgeRepository.deleteById(id);
    }
}

