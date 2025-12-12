package fr.emse.tb3pwme.project.web;

import fr.emse.tb3pwme.project.application.AccessRuleService;
import fr.emse.tb3pwme.project.domain.AccessRule;
import fr.emse.tb3pwme.project.domain.TimeSlot;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/access-rules")
public class AccessRuleController {
    private final AccessRuleService accessRuleService;
    private final RepresentationMapper mapper;

    public AccessRuleController(AccessRuleService accessRuleService, RepresentationMapper mapper) {
        this.accessRuleService = accessRuleService;
        this.mapper = mapper;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<RepresentationMapper.AccessRuleRepresentation> getAllAccessRules() {
        return accessRuleService.findAll().stream()
            .map(mapper::toRepresentation)
            .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RepresentationMapper.AccessRuleRepresentation> getAccessRuleById(@PathVariable UUID id) {
        return accessRuleService.findById(id)
            .map(mapper::toRepresentation)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public List<RepresentationMapper.AccessRuleRepresentation> getAccessRulesByUserId(@PathVariable UUID userId) {
        return accessRuleService.findByUserId(userId).stream()
            .map(mapper::toRepresentation)
            .collect(Collectors.toList());
    }

    @GetMapping("/door/{doorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<RepresentationMapper.AccessRuleRepresentation> getAccessRulesByDoorId(@PathVariable UUID doorId) {
        return accessRuleService.findByDoorId(doorId).stream()
            .map(mapper::toRepresentation)
            .collect(Collectors.toList());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RepresentationMapper.AccessRuleRepresentation> createAccessRule(
            @RequestBody RepresentationMapper.CreateAccessRuleRequest request) {
        List<TimeSlot> timeSlots = request.timeSlots().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());

        AccessRule accessRule = accessRuleService.createAccessRule(
            request.userId(),
            request.doorId(),
            timeSlots
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toRepresentation(accessRule));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RepresentationMapper.AccessRuleRepresentation> updateAccessRule(
            @PathVariable UUID id,
            @RequestBody RepresentationMapper.UpdateAccessRuleRequest request) {
        try {
            List<TimeSlot> timeSlots = request.timeSlots().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());

            AccessRule accessRule = accessRuleService.updateTimeSlots(id, timeSlots);
            return ResponseEntity.ok(mapper.toRepresentation(accessRule));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RepresentationMapper.AccessRuleRepresentation> deactivateAccessRule(@PathVariable UUID id) {
        try {
            AccessRule accessRule = accessRuleService.deactivateRule(id);
            return ResponseEntity.ok(mapper.toRepresentation(accessRule));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RepresentationMapper.AccessRuleRepresentation> activateAccessRule(@PathVariable UUID id) {
        try {
            AccessRule accessRule = accessRuleService.activateRule(id);
            return ResponseEntity.ok(mapper.toRepresentation(accessRule));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAccessRule(@PathVariable UUID id) {
        accessRuleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }
}

