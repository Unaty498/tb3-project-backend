package fr.emse.tb3pwme.project.persistence;

import fr.emse.tb3pwme.project.domain.*;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class EntityMapper {

    // User mappings
    public User toDomain(UserEntity entity) {
        return new User(
            entity.getId(),
            entity.getEmail(),
            entity.getFirstName(),
            entity.getLastName(),
            entity.getPhone(),
            entity.getRole(),
            entity.isActive(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public UserEntity toEntity(User domain) {
        return new UserEntity(
            domain.getId(),
            domain.getEmail(),
            domain.getFirstName(),
            domain.getLastName(),
            domain.getPhone(),
            domain.getRole(),
            domain.isActive(),
            domain.getCreatedAt(),
            domain.getUpdatedAt()
        );
    }

    // Badge mappings
    public Badge toDomain(BadgeEntity entity) {
        return new Badge(
            entity.getId(),
            entity.getBadgeNumber(),
            entity.getType(),
            entity.getUserId(),
            entity.isActive(),
            entity.getExpiryDate(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public BadgeEntity toEntity(Badge domain) {
        return new BadgeEntity(
            domain.getId(),
            domain.getBadgeNumber(),
            domain.getType(),
            domain.getUserId(),
            domain.isActive(),
            domain.getExpiryDate(),
            domain.getCreatedAt(),
            domain.getUpdatedAt()
        );
    }

    // Door mappings
    public Door toDomain(DoorEntity entity) {
        return new Door(
            entity.getId(),
            entity.getName(),
            entity.getLocation(),
            entity.getDeviceId(),
            entity.isActive(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public DoorEntity toEntity(Door domain) {
        return new DoorEntity(
            domain.getId(),
            domain.getName(),
            domain.getLocation(),
            domain.getDeviceId(),
            domain.isActive(),
            domain.getCreatedAt(),
            domain.getUpdatedAt()
        );
    }

    // TimeSlot mappings
    public TimeSlot toDomain(TimeSlotEmbeddable entity) {
        return new TimeSlot(
            entity.getDayOfWeek(),
            entity.getStartTime(),
            entity.getEndTime()
        );
    }

    public TimeSlotEmbeddable toEmbeddable(TimeSlot domain) {
        return new TimeSlotEmbeddable(
            domain.getDayOfWeek(),
            domain.getStartTime(),
            domain.getEndTime()
        );
    }

    // AccessRule mappings
    public AccessRule toDomain(AccessRuleEntity entity) {
        return new AccessRule(
            entity.getId(),
            entity.getUserId(),
            entity.getDoorId(),
            entity.getTimeSlots().stream()
                .map(this::toDomain)
                .collect(Collectors.toList()),
            entity.isActive(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public AccessRuleEntity toEntity(AccessRule domain) {
        return new AccessRuleEntity(
            domain.getId(),
            domain.getUserId(),
            domain.getDoorId(),
            domain.getTimeSlots().stream()
                .map(this::toEmbeddable)
                .collect(Collectors.toList()),
            domain.isActive(),
            domain.getCreatedAt(),
            domain.getUpdatedAt()
        );
    }

    // AccessLog mappings
    public AccessLog toDomain(AccessLogEntity entity) {
        return new AccessLog(
            entity.getId(),
            entity.getBadgeId(),
            entity.getUserId(),
            entity.getDoorId(),
            entity.getResult(),
            entity.getDetails(),
            entity.getTimestamp()
        );
    }

    public AccessLogEntity toEntity(AccessLog domain) {
        return new AccessLogEntity(
            domain.getId(),
            domain.getBadgeId(),
            domain.getUserId(),
            domain.getDoorId(),
            domain.getResult(),
            domain.getDetails(),
            domain.getTimestamp()
        );
    }
}

