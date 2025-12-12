package fr.emse.tb3pwme.project.application;

import fr.emse.tb3pwme.project.domain.Door;
import fr.emse.tb3pwme.project.persistence.DoorRepository;
import fr.emse.tb3pwme.project.persistence.EntityMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class DoorService {
    private final DoorRepository doorRepository;
    private final EntityMapper mapper;

    public DoorService(DoorRepository doorRepository, EntityMapper mapper) {
        this.doorRepository = doorRepository;
        this.mapper = mapper;
    }

    public Door createDoor(String name, String location, String deviceId) {
        Door door = Door.newDoor(name, location, deviceId);
        return mapper.toDomain(doorRepository.save(mapper.toEntity(door)));
    }

    public Optional<Door> findById(UUID id) {
        return doorRepository.findById(id).map(mapper::toDomain);
    }

    public Optional<Door> findByDeviceId(String deviceId) {
        return doorRepository.findByDeviceId(deviceId).map(mapper::toDomain);
    }

    public List<Door> findAll() {
        return doorRepository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    public Door updateDoor(UUID id, String name, String location) {
        Door door = findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Door not found"));

        Door updatedDoor = door.update(name, location);
        return mapper.toDomain(doorRepository.save(mapper.toEntity(updatedDoor)));
    }

    public Door deactivateDoor(UUID id) {
        Door door = findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Door not found"));

        Door deactivatedDoor = door.deactivate();
        return mapper.toDomain(doorRepository.save(mapper.toEntity(deactivatedDoor)));
    }

    public Door activateDoor(UUID id) {
        Door door = findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Door not found"));

        Door activatedDoor = door.activate();
        return mapper.toDomain(doorRepository.save(mapper.toEntity(activatedDoor)));
    }

    public void deleteDoor(UUID id) {
        doorRepository.deleteById(id);
    }
}

