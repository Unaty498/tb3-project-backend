package fr.emse.tb3pwme.project;

import fr.emse.tb3pwme.project.application.*;
import fr.emse.tb3pwme.project.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Charge des données de démonstration au démarrage de l'application
 * Actif uniquement en profil 'dev'
 */
@Component
//@Profile("dev")
public class DataLoader implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final UserService userService;
    private final DoorService doorService;
    private final BadgeService badgeService;
    private final AccessRuleService accessRuleService;

    public DataLoader(UserService userService,
                     DoorService doorService,
                     BadgeService badgeService,
                     AccessRuleService accessRuleService) {
        this.userService = userService;
        this.doorService = doorService;
        this.badgeService = badgeService;
        this.accessRuleService = accessRuleService;
    }

    @Override
    public void run(String... args) {
        logger.info("🚀 Initialisation des données de démonstration...");

        // 1. Créer des utilisateurs
        User admin = userService.createUser(
            "admin@example.com",
            "Admin",
            "System",
            "+33612345678",
            UserRole.ADMIN
        );
        logger.info("✓ Admin créé: {}", admin.getEmail());

        User user1 = userService.createUser(
            "alice.dupont@example.com",
            "Alice",
            "Dupont",
            "+33623456789",
            UserRole.USER
        );
        logger.info("✓ Utilisateur créé: {}", user1.getEmail());

        User user2 = userService.createUser(
            "bob.martin@example.com",
            "Bob",
            "Martin",
            "+33634567890",
            UserRole.USER
        );
        logger.info("✓ Utilisateur créé: {}", user2.getEmail());

        User visitor = userService.createUser(
            "visitor@example.com",
            "Jane",
            "Visitor",
            "+33645678901",
            UserRole.VISITOR
        );
        logger.info("✓ Visiteur créé: {}", visitor.getEmail());

        // 2. Créer des portes
        Door mainEntrance = doorService.createDoor(
            "Entrée Principale",
            "Bâtiment A - Rez-de-chaussée",
            "DOOR-MAIN-001"
        );
        logger.info("✓ Porte créée: {} ({})", mainEntrance.getName(), mainEntrance.getDeviceId());

        Door serverRoom = doorService.createDoor(
            "Salle Serveurs",
            "Bâtiment A - Sous-sol",
            "DOOR-SERVER-001"
        );
        logger.info("✓ Porte créée: {} ({})", serverRoom.getName(), serverRoom.getDeviceId());

        Door officeFloor2 = doorService.createDoor(
            "Bureau Étage 2",
            "Bâtiment A - Étage 2",
            "DOOR-OFFICE-201"
        );
        logger.info("✓ Porte créée: {} ({})", officeFloor2.getName(), officeFloor2.getDeviceId());

        // 3. Créer des badges
        Badge adminBadge = badgeService.createBadge(
            "BADGE-ADMIN-001",
            BadgeType.PHYSICAL,
            admin.getId(),
            LocalDateTime.now().plusYears(5)
        );
        logger.info("✓ Badge créé: {} pour {}", adminBadge.getBadgeNumber(), admin.getEmail());

        Badge aliceBadge = badgeService.createBadge(
            "BADGE-USER-001",
            BadgeType.PHYSICAL,
            user1.getId(),
            LocalDateTime.now().plusYears(1)
        );
        logger.info("✓ Badge créé: {} pour {}", aliceBadge.getBadgeNumber(), user1.getEmail());

        Badge aliceMobileBadge = badgeService.createBadge(
            "MOBILE-" + user1.getId().toString(),
            BadgeType.MOBILE,
            user1.getId(),
            LocalDateTime.now().plusYears(1)
        );
        logger.info("✓ Badge mobile créé: {} pour {}", aliceMobileBadge.getBadgeNumber(), user1.getEmail());

        Badge bobBadge = badgeService.createBadge(
            "BADGE-USER-002",
            BadgeType.PHYSICAL,
            user2.getId(),
            LocalDateTime.now().plusYears(1)
        );
        logger.info("✓ Badge créé: {} pour {}", bobBadge.getBadgeNumber(), user2.getEmail());

        Badge visitorBadge = badgeService.createBadge(
            "BADGE-VISITOR-001",
            BadgeType.PHYSICAL,
            visitor.getId(),
            LocalDateTime.now().plusDays(7)
        );
        logger.info("✓ Badge visiteur créé: {} pour {}", visitorBadge.getBadgeNumber(), visitor.getEmail());

        // 4. Créer des règles d'accès

        // Admin - Accès 24/7 à toutes les portes
        accessRuleService.createAccessRule(admin.getId(), mainEntrance.getId(), List.of());
        accessRuleService.createAccessRule(admin.getId(), serverRoom.getId(), List.of());
        accessRuleService.createAccessRule(admin.getId(), officeFloor2.getId(), List.of());
        logger.info("✓ Règles d'accès 24/7 créées pour Admin");

        // Alice - Accès entrée principale en semaine 8h-18h
        List<TimeSlot> businessHours = Arrays.asList(
            new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(18, 0)),
            new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(18, 0)),
            new TimeSlot(DayOfWeek.WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(18, 0)),
            new TimeSlot(DayOfWeek.THURSDAY, LocalTime.of(8, 0), LocalTime.of(18, 0)),
            new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(8, 0), LocalTime.of(18, 0))
        );
        accessRuleService.createAccessRule(user1.getId(), mainEntrance.getId(), businessHours);
        accessRuleService.createAccessRule(user1.getId(), officeFloor2.getId(), businessHours);
        logger.info("✓ Règles d'accès heures de bureau créées pour Alice");

        // Bob - Accès étendu (6h-22h)
        List<TimeSlot> extendedHours = Arrays.asList(
            new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(6, 0), LocalTime.of(22, 0)),
            new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(6, 0), LocalTime.of(22, 0)),
            new TimeSlot(DayOfWeek.WEDNESDAY, LocalTime.of(6, 0), LocalTime.of(22, 0)),
            new TimeSlot(DayOfWeek.THURSDAY, LocalTime.of(6, 0), LocalTime.of(22, 0)),
            new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(6, 0), LocalTime.of(22, 0)),
            new TimeSlot(DayOfWeek.SATURDAY, LocalTime.of(9, 0), LocalTime.of(17, 0))
        );
        accessRuleService.createAccessRule(user2.getId(), mainEntrance.getId(), extendedHours);
        accessRuleService.createAccessRule(user2.getId(), serverRoom.getId(), extendedHours);
        logger.info("✓ Règles d'accès étendues créées pour Bob");

        // Visiteur - Accès entrée principale uniquement (9h-17h en semaine)
        List<TimeSlot> visitorHours = Arrays.asList(
            new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0)),
            new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(17, 0)),
            new TimeSlot(DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(17, 0)),
            new TimeSlot(DayOfWeek.THURSDAY, LocalTime.of(9, 0), LocalTime.of(17, 0)),
            new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(9, 0), LocalTime.of(17, 0))
        );
        accessRuleService.createAccessRule(visitor.getId(), mainEntrance.getId(), visitorHours);
        logger.info("Created visitor access rules");

        logger.info("Demo data loaded successfully!");
        logger.info("Summary:");
        logger.info("   - {} users", userService.findAll().size());
        logger.info("   - {} doors", doorService.findAll().size());
        logger.info("   - {} badges", badgeService.findAll().size());
        logger.info("   - {} access rules", accessRuleService.findAll().size());
    }
}
