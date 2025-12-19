package fr.emse.tb3pwme.project.application;

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

        // Définir un rôle par défaut (à adapter selon votre logique métier)
        // Vous pouvez aussi extraire les rôles du JWT si configuré dans Keycloak
        user.setRole(UserRole.USER);

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

        // Mapper les rôles Keycloak aux rôles de l'application si nécessaire
        user.setRole(getRoleFromJwt(jwt));

        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    private UserRole getRoleFromJwt(Jwt jwt) {
        // Exemple simple : mapper un rôle Keycloak à un rôle de l'application
        // Adapter selon votre configuration Keycloak
        List<String> roles = jwt.getClaimAsStringList("realm_access.roles");
        if (roles != null && !roles.isEmpty() && roles.contains("ADMIN")) {
            return UserRole.ADMIN;
        }
        return UserRole.USER; // Rôle par défaut
    }
}

