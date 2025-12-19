package fr.emse.tb3pwme.project.web;

import fr.emse.tb3pwme.project.domain.*;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RepresentationMapper {

    // User representations
    public record UserRepresentation(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String phone,
        UserRole role,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {}

    public record CreateUserRequest(
        String email,
        String firstName,
        String lastName,
        String phone,
        UserRole role
    ) {}

    public record UpdateUserRequest(
        String firstName,
        String lastName,
        String phone
    ) {}

    public UserRepresentation toRepresentation(User user) {
        return new UserRepresentation(
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getPhone(),
            user.getRole(),
            user.isActive(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }

    // Badge representations
    public record BadgeRepresentation(
        UUID id,
        String badgeNumber,
        BadgeType type,
        UUID userId,
        boolean active,
        LocalDateTime expiryDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {}

    public record CreateBadgeRequest(
        String badgeNumber,
        BadgeType type,
        UUID userId,
        LocalDateTime expiryDate
    ) {}

    public record UpdateBadgeExpiryRequest(
        LocalDateTime expiryDate
    ) {}

    public record UpdateBadgeMappingRequest(
        boolean physicallyMapped
    ) {}

    public BadgeRepresentation toRepresentation(Badge badge) {
        return new BadgeRepresentation(
            badge.getId(),
            badge.getBadgeNumber(),
            badge.getType(),
            badge.getUserId(),
            badge.isActive(),
            badge.getExpiryDate(),
            badge.getCreatedAt(),
            badge.getUpdatedAt()
        );
    }

    // Door representations
    public record DoorRepresentation(
        UUID id,
        String name,
        String location,
        String deviceId,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {}

    public record CreateDoorRequest(
        String name,
        String location,
        String deviceId
    ) {}

    public record UpdateDoorRequest(
        String name,
        String location
    ) {}

    public DoorRepresentation toRepresentation(Door door) {
        return new DoorRepresentation(
            door.getId(),
            door.getName(),
            door.getLocation(),
            door.getDeviceId(),
            door.isActive(),
            door.getCreatedAt(),
            door.getUpdatedAt()
        );
    }

    // TimeSlot representations
    public record TimeSlotRepresentation(
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
    ) {}

    public TimeSlotRepresentation toRepresentation(TimeSlot timeSlot) {
        return new TimeSlotRepresentation(
            timeSlot.getDayOfWeek(),
            timeSlot.getStartTime(),
            timeSlot.getEndTime()
        );
    }

    public TimeSlot toDomain(TimeSlotRepresentation repr) {
        return new TimeSlot(repr.dayOfWeek(), repr.startTime(), repr.endTime());
    }

    // AccessRule representations
    public record AccessRuleRepresentation(
        UUID id,
        UUID userId,
        UUID doorId,
        List<TimeSlotRepresentation> timeSlots,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {}

    public record CreateAccessRuleRequest(
        UUID userId,
        UUID doorId,
        List<TimeSlotRepresentation> timeSlots
    ) {}

    public record UpdateAccessRuleRequest(
        List<TimeSlotRepresentation> timeSlots
    ) {}

    public AccessRuleRepresentation toRepresentation(AccessRule accessRule) {
        return new AccessRuleRepresentation(
            accessRule.getId(),
            accessRule.getUserId(),
            accessRule.getDoorId(),
            accessRule.getTimeSlots().stream()
                .map(this::toRepresentation)
                .collect(Collectors.toList()),
            accessRule.isActive(),
            accessRule.getCreatedAt(),
            accessRule.getUpdatedAt()
        );
    }

    // AccessLog representations
    public record AccessLogRepresentation(
        UUID id,
        UUID badgeId,
        UUID userId,
        UUID doorId,
        AccessResult result,
        String details,
        LocalDateTime timestamp
    ) {}

    public AccessLogRepresentation toRepresentation(AccessLog accessLog) {
        return new AccessLogRepresentation(
            accessLog.getId(),
            accessLog.getBadgeId(),
            accessLog.getUserId(),
            accessLog.getDoorId(),
            accessLog.getResult(),
            accessLog.getDetails(),
            accessLog.getTimestamp()
        );
    }

    // Lock API representations
    public record VerifyAccessRequest(
        String badgeNumber,
        String doorDeviceId
    ) {}

    public record VerifyAccessResponse(
        AccessResult result,
        String message
    ) {}
}

