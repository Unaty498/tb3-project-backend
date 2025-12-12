package fr.emse.tb3pwme.project.web;

import fr.emse.tb3pwme.project.application.BadgeService;
import fr.emse.tb3pwme.project.domain.Badge;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/badges")
public class BadgeController {
    private final BadgeService badgeService;
    private final RepresentationMapper mapper;

    public BadgeController(BadgeService badgeService, RepresentationMapper mapper) {
        this.badgeService = badgeService;
        this.mapper = mapper;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<RepresentationMapper.BadgeRepresentation> getAllBadges() {
        return badgeService.findAll().stream()
            .map(mapper::toRepresentation)
            .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @badgeSecurity.isBadgeOwner(#id)")
    public ResponseEntity<RepresentationMapper.BadgeRepresentation> getBadgeById(@PathVariable UUID id) {
        return badgeService.findById(id)
            .map(mapper::toRepresentation)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public List<RepresentationMapper.BadgeRepresentation> getBadgesByUserId(@PathVariable UUID userId) {
        return badgeService.findByUserId(userId).stream()
            .map(mapper::toRepresentation)
            .collect(Collectors.toList());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RepresentationMapper.BadgeRepresentation> createBadge(
            @RequestBody RepresentationMapper.CreateBadgeRequest request) {
        Badge badge = badgeService.createBadge(
            request.badgeNumber(),
            request.type(),
            request.userId(),
            request.expiryDate()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toRepresentation(badge));
    }

    @PutMapping("/{id}/expiry")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RepresentationMapper.BadgeRepresentation> updateBadgeExpiry(
            @PathVariable UUID id,
            @RequestBody RepresentationMapper.UpdateBadgeExpiryRequest request) {
        try {
            Badge badge = badgeService.updateExpiry(id, request.expiryDate());
            return ResponseEntity.ok(mapper.toRepresentation(badge));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RepresentationMapper.BadgeRepresentation> deactivateBadge(@PathVariable UUID id) {
        try {
            Badge badge = badgeService.deactivateBadge(id);
            return ResponseEntity.ok(mapper.toRepresentation(badge));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RepresentationMapper.BadgeRepresentation> activateBadge(@PathVariable UUID id) {
        try {
            Badge badge = badgeService.activateBadge(id);
            return ResponseEntity.ok(mapper.toRepresentation(badge));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBadge(@PathVariable UUID id) {
        badgeService.deleteBadge(id);
        return ResponseEntity.noContent().build();
    }
}

