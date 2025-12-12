package fr.emse.tb3pwme.project.application;

import fr.emse.tb3pwme.project.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AccessControlService {
    private final BadgeService badgeService;
    private final UserService userService;
    private final DoorService doorService;
    private final AccessRuleService accessRuleService;
    private final AccessLogService accessLogService;

    public AccessControlService(BadgeService badgeService,
                                UserService userService,
                                DoorService doorService,
                                AccessRuleService accessRuleService,
                                AccessLogService accessLogService) {
        this.badgeService = badgeService;
        this.userService = userService;
        this.doorService = doorService;
        this.accessRuleService = accessRuleService;
        this.accessLogService = accessLogService;
    }

    /**
     * Vérifie si l'accès doit être accordé pour un badge donné à une porte donnée
     */
    public AccessResult verifyAccess(String badgeNumber, String doorDeviceId) {
        // 1. Vérifier si le badge existe
        Badge badge = badgeService.findByBadgeNumber(badgeNumber).orElse(null);
        if (badge == null) {
            logAccess(null, null, null, AccessResult.DENIED_INVALID_BADGE,
                     "Badge number not found: " + badgeNumber);
            return AccessResult.DENIED_INVALID_BADGE;
        }

        // 2. Vérifier si le badge est expiré
        if (badge.isExpired()) {
            logAccess(badge.getId(), badge.getUserId(), null, AccessResult.DENIED_EXPIRED_BADGE,
                     "Badge expired: " + badgeNumber);
            return AccessResult.DENIED_EXPIRED_BADGE;
        }

        // 3. Vérifier si le badge est actif
        if (!badge.isActive()) {
            logAccess(badge.getId(), badge.getUserId(), null, AccessResult.DENIED_INVALID_BADGE,
                     "Badge inactive: " + badgeNumber);
            return AccessResult.DENIED_INVALID_BADGE;
        }

        // 4. Récupérer l'utilisateur
        User user = userService.findById(badge.getUserId()).orElse(null);
        if (user == null) {
            logAccess(badge.getId(), badge.getUserId(), null, AccessResult.DENIED_INACTIVE_USER,
                     "User not found for badge: " + badgeNumber);
            return AccessResult.DENIED_INACTIVE_USER;
        }

        // 5. Vérifier si l'utilisateur est actif
        if (!user.isActive()) {
            logAccess(badge.getId(), user.getId(), null, AccessResult.DENIED_INACTIVE_USER,
                     "User inactive: " + user.getEmail());
            return AccessResult.DENIED_INACTIVE_USER;
        }

        // 6. Récupérer la porte
        Door door = doorService.findByDeviceId(doorDeviceId).orElse(null);
        if (door == null) {
            logAccess(badge.getId(), user.getId(), null, AccessResult.DENIED_NO_PERMISSION,
                     "Door not found: " + doorDeviceId);
            return AccessResult.DENIED_NO_PERMISSION;
        }

        // 7. Vérifier si la porte est active
        if (!door.isActive()) {
            logAccess(badge.getId(), user.getId(), door.getId(), AccessResult.DENIED_INACTIVE_DOOR,
                     "Door inactive: " + door.getName());
            return AccessResult.DENIED_INACTIVE_DOOR;
        }

        // 8. Vérifier les règles d'accès
        List<AccessRule> activeRules = accessRuleService.findActiveRules(user.getId(), door.getId());

        if (activeRules.isEmpty()) {
            logAccess(badge.getId(), user.getId(), door.getId(), AccessResult.DENIED_NO_PERMISSION,
                     "No access rule found for user " + user.getEmail() + " at door " + door.getName());
            return AccessResult.DENIED_NO_PERMISSION;
        }

        // 9. Vérifier les contraintes temporelles
        boolean hasValidTimeSlot = activeRules.stream()
            .anyMatch(AccessRule::isCurrentlyValid);

        if (!hasValidTimeSlot) {
            logAccess(badge.getId(), user.getId(), door.getId(), AccessResult.DENIED_TIME_RESTRICTION,
                     "Access denied due to time restrictions for " + user.getEmail() + " at " + door.getName());
            return AccessResult.DENIED_TIME_RESTRICTION;
        }

        // 10. Accès accordé !
        logAccess(badge.getId(), user.getId(), door.getId(), AccessResult.GRANTED,
                 "Access granted for " + user.getEmail() + " at " + door.getName());
        return AccessResult.GRANTED;
    }

    /**
     * Enregistre un événement d'accès dans les logs
     */
    private void logAccess(UUID badgeId, UUID userId, UUID doorId, AccessResult result, String details) {
        accessLogService.logAccess(badgeId, userId, doorId, result, details);
    }

    /**
     * Obtient l'historique d'accès pour un utilisateur
     */
    public List<AccessLog> getUserAccessHistory(UUID userId) {
        return accessLogService.findByUserId(userId);
    }

    /**
     * Obtient l'historique d'accès pour une porte
     */
    public List<AccessLog> getDoorAccessHistory(UUID doorId) {
        return accessLogService.findByDoorId(doorId);
    }
}

