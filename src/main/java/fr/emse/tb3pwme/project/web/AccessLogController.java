package fr.emse.tb3pwme.project.web;

import fr.emse.tb3pwme.project.application.AccessLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/access-logs")
public class AccessLogController {
    private final AccessLogService accessLogService;
    private final RepresentationMapper mapper;

    public AccessLogController(AccessLogService accessLogService, RepresentationMapper mapper) {
        this.accessLogService = accessLogService;
        this.mapper = mapper;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<RepresentationMapper.AccessLogRepresentation> getAllAccessLogs() {
        return accessLogService.findAll().stream()
            .map(mapper::toRepresentation)
            .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RepresentationMapper.AccessLogRepresentation> getAccessLogById(@PathVariable UUID id) {
        return accessLogService.findById(id)
            .map(mapper::toRepresentation)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public List<RepresentationMapper.AccessLogRepresentation> getAccessLogsByUserId(@PathVariable UUID userId) {
        return accessLogService.findByUserId(userId).stream()
            .map(mapper::toRepresentation)
            .collect(Collectors.toList());
    }

    @GetMapping("/door/{doorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<RepresentationMapper.AccessLogRepresentation> getAccessLogsByDoorId(@PathVariable UUID doorId) {
        return accessLogService.findByDoorId(doorId).stream()
            .map(mapper::toRepresentation)
            .collect(Collectors.toList());
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public List<RepresentationMapper.AccessLogRepresentation> getAccessLogsByDateRange(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        return accessLogService.findByDateRange(start, end).stream()
            .map(mapper::toRepresentation)
            .collect(Collectors.toList());
    }
}

