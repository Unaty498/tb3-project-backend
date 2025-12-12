package fr.emse.tb3pwme.project.web;
import fr.emse.tb3pwme.project.application.AccessControlService;
import fr.emse.tb3pwme.project.domain.AccessResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * API publique pour les serrures connectées
 * Permet aux serrures de vérifier si un badge a accès
 */
@RestController
@RequestMapping("/api/locks")
public class LockController {
    private final AccessControlService accessControlService;
    private final RepresentationMapper mapper;

    public LockController(AccessControlService accessControlService, RepresentationMapper mapper) {
        this.accessControlService = accessControlService;
        this.mapper = mapper;
    }

    /**
     * Endpoint public pour vérifier l'accès
     * Appelé par les serrures IoT pour déterminer si une porte doit s'ouvrir
     */
    @PostMapping("/verify-access")
    public ResponseEntity<RepresentationMapper.VerifyAccessResponse> verifyAccess(
            @RequestBody RepresentationMapper.VerifyAccessRequest request) {

        AccessResult result = accessControlService.verifyAccess(
                request.badgeNumber(),
                request.doorDeviceId()
        );

        String message = switch (result) {
            case GRANTED -> "Access granted";
            case DENIED_INVALID_BADGE -> "Invalid or inactive badge";
            case DENIED_INACTIVE_USER -> "User account is inactive";
            case DENIED_NO_PERMISSION -> "No access permission for this door";
            case DENIED_TIME_RESTRICTION -> "Access denied: outside of allowed time";
            case DENIED_EXPIRED_BADGE -> "Badge has expired";
            case DENIED_INACTIVE_DOOR -> "Door is inactive";
        };

        return ResponseEntity.ok(new RepresentationMapper.VerifyAccessResponse(result, message));
    }
}
