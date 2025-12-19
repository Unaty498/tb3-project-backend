package fr.emse.tb3pwme.project.web;

import fr.emse.tb3pwme.project.application.UserSyncService;
import fr.emse.tb3pwme.project.persistence.UserEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
public class MeController {

    private final UserSyncService userSyncService;

    public MeController(UserSyncService userSyncService) {
        this.userSyncService = userSyncService;
    }

    @GetMapping
    public UserEntity getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        // On transforme le token technique en utilisateur métier
        return userSyncService.syncUser(jwt);
    }
}