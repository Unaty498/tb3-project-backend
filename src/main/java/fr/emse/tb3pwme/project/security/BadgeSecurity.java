        package fr.emse.tb3pwme.project.security;

import fr.emse.tb3pwme.project.application.BadgeService;
import fr.emse.tb3pwme.project.domain.Badge;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Helper to check badge-related permissions
 */
@Component("badgeSecurity")
public class BadgeSecurity {
    private final BadgeService badgeService;
    private final UserSecurity userSecurity;

    public BadgeSecurity(BadgeService badgeService, UserSecurity userSecurity) {
        this.badgeService = badgeService;
        this.userSecurity = userSecurity;
    }

    /**
     * Check if the currently authenticated user is the owner of the badge
     */
    public boolean isBadgeOwner(UUID badgeId) {
        UUID currentUserId = userSecurity.getCurrentUserId();
        if (currentUserId == null) {
            return false;
        }

        return badgeService.findById(badgeId)
            .map(Badge::getUserId)
            .map(userId -> userId.equals(currentUserId))
            .orElse(false);
    }
}

