package fr.emse.tb3pwme.project.application;
import fr.emse.tb3pwme.project.domain.User;
import fr.emse.tb3pwme.project.domain.UserRole;
import fr.emse.tb3pwme.project.persistence.EntityMapper;
import fr.emse.tb3pwme.project.persistence.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final EntityMapper mapper;

    public UserService(UserRepository userRepository, EntityMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    public User createUser(String keycloakId, String email, String firstName, String lastName, String phone, UserRole role) {
        User user = User.newUser(keycloakId, email, firstName, lastName, phone, role);
        return mapper.toDomain(userRepository.save(mapper.toEntity(user)));
    }

    public Optional<User> findById(UUID id) {
        return userRepository.findById(id).map(mapper::toDomain);
    }

    public Optional<User> findByKeycloakId(String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId).map(mapper::toDomain);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email).map(mapper::toDomain);
    }

    public List<User> findAll() {
        return userRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    public User updateUser(UUID id, String firstName, String lastName, String phone) {
        User user = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        User updatedUser = user.update(firstName, lastName, phone);
        return mapper.toDomain(userRepository.save(mapper.toEntity(updatedUser)));
    }

    public User deactivateUser(UUID id) {
        User user = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        User deactivatedUser = user.deactivate();
        return mapper.toDomain(userRepository.save(mapper.toEntity(deactivatedUser)));
    }

    public User activateUser(UUID id) {
        User user = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        User activatedUser = user.activate();
        return mapper.toDomain(userRepository.save(mapper.toEntity(activatedUser)));
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }
}