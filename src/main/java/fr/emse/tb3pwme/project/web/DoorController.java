package fr.emse.tb3pwme.project.web;

import fr.emse.tb3pwme.project.application.DoorService;
import fr.emse.tb3pwme.project.domain.Door;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doors")
public class DoorController {
    private final DoorService doorService;
    private final RepresentationMapper mapper;

    public DoorController(DoorService doorService, RepresentationMapper mapper) {
        this.doorService = doorService;
        this.mapper = mapper;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public List<RepresentationMapper.DoorRepresentation> getAllDoors() {
        return doorService.findAll().stream()
            .map(mapper::toRepresentation)
            .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<RepresentationMapper.DoorRepresentation> getDoorById(@PathVariable UUID id) {
        return doorService.findById(id)
            .map(mapper::toRepresentation)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RepresentationMapper.DoorRepresentation> createDoor(
            @RequestBody RepresentationMapper.CreateDoorRequest request) {
        Door door = doorService.createDoor(
            request.name(),
            request.location(),
            request.deviceId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toRepresentation(door));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RepresentationMapper.DoorRepresentation> updateDoor(
            @PathVariable UUID id,
            @RequestBody RepresentationMapper.UpdateDoorRequest request) {
        try {
            Door door = doorService.updateDoor(id, request.name(), request.location());
            return ResponseEntity.ok(mapper.toRepresentation(door));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RepresentationMapper.DoorRepresentation> deactivateDoor(@PathVariable UUID id) {
        try {
            Door door = doorService.deactivateDoor(id);
            return ResponseEntity.ok(mapper.toRepresentation(door));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RepresentationMapper.DoorRepresentation> activateDoor(@PathVariable UUID id) {
        try {
            Door door = doorService.activateDoor(id);
            return ResponseEntity.ok(mapper.toRepresentation(door));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDoor(@PathVariable UUID id) {
        doorService.deleteDoor(id);
        return ResponseEntity.noContent().build();
    }
}

