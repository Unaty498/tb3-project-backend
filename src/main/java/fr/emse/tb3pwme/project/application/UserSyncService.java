package fr.emse.tb3pwme.project.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fr.emse.tb3pwme.project.domain.UserRole;
import fr.emse.tb3pwme.project.persistence.UserEntity;
import fr.emse.tb3pwme.project.persistence.UserRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import java.util.Map;

@Service
public class UserSyncService {

    private static final Logger logger = LoggerFactory.getLogger(UserSyncService.class);

    private final UserRepository userRepository;

    public UserSyncService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserEntity syncUser(Jwt jwt) {
        String keycloakId = jwt.getClaimAsString("sub");
        String email = jwt.getClaimAsString("email");

        // 1. Chercher par ID Keycloak
        return userRepository.findByKeycloakId(keycloakId)
                .map(existingUser -> updateUser(existingUser, jwt)) // Mise à jour si existe
                .orElseGet(() -> {
                    // 2. Si pas trouvé par ID Keycloak, chercher par email (cas de migration ou pré-création)
                    return userRepository.findByEmail(email)
                            .map(existingUser -> {
                                // On lie le compte existant à Keycloak
                                existingUser.setKeycloakId(keycloakId);
                                return updateUser(existingUser, jwt);
                            })
                            .orElseGet(() -> createUser(jwt, keycloakId)); // 3. Création complète
                });
    }

    private UserEntity createUser(Jwt jwt, String keycloakId) {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID()); // Génération de l'ID technique
        user.setKeycloakId(keycloakId);
        user.setCreatedAt(LocalDateTime.now());
        user.setActive(true); // Actif par défaut
        user.setRole(getRoleFromJwt(jwt));

        return updateUser(user, jwt);
    }

    private UserEntity updateUser(UserEntity user, Jwt jwt) {
        user.setEmail(jwt.getClaimAsString("email"));
        user.setFirstName(jwt.getClaimAsString("given_name"));
        user.setLastName(jwt.getClaimAsString("family_name"));

        // Gestion du téléphone si présent dans le token (claim standard 'phone_number')
        if (jwt.hasClaim("phone_number")) {
            user.setPhone(jwt.getClaimAsString("phone_number"));
        }

        user.setRole(getRoleFromJwt(jwt));

        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    private UserRole getRoleFromJwt(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        List<String> roles = null;
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            try {
                roles = (List<String>) realmAccess.get("roles");
            } catch (ClassCastException e) {
                logger.warn("Could not cast realm_access.roles to List<String>", e);
            }
        }

        logger.debug("User roles from JWT: {}", roles);

        if (roles != null && !roles.isEmpty() && roles.contains("ADMIN")) {
            return UserRole.ADMIN;
        }
        return UserRole.USER; // Rôle par défaut
    }
}

