package fr.emse.tb3pwme.project.web;

import fr.emse.tb3pwme.project.application.UserService;
import fr.emse.tb3pwme.project.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final RepresentationMapper mapper;

    public UserController(UserService userService, RepresentationMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<RepresentationMapper.UserRepresentation> getAllUsers() {
        return userService.findAll().stream()
            .map(mapper::toRepresentation)
            .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<RepresentationMapper.UserRepresentation> getUserById(@PathVariable UUID id) {
        return userService.findById(id)
            .map(mapper::toRepresentation)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RepresentationMapper.UserRepresentation> createUser(
            @RequestBody RepresentationMapper.CreateUserRequest request) {
        User user = userService.createUser(
            request.email(),
            request.firstName(),
            request.lastName(),
            request.phone(),
            request.role()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toRepresentation(user));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<RepresentationMapper.UserRepresentation> updateUser(
            @PathVariable UUID id,
            @RequestBody RepresentationMapper.UpdateUserRequest request) {
        try {
            User user = userService.updateUser(id, request.firstName(), request.lastName(), request.phone());
            return ResponseEntity.ok(mapper.toRepresentation(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RepresentationMapper.UserRepresentation> deactivateUser(@PathVariable UUID id) {
        try {
            User user = userService.deactivateUser(id);
            return ResponseEntity.ok(mapper.toRepresentation(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RepresentationMapper.UserRepresentation> activateUser(@PathVariable UUID id) {
        try {
            User user = userService.activateUser(id);
            return ResponseEntity.ok(mapper.toRepresentation(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

