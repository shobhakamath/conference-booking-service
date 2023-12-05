package com.conference.roomservice.service.impl;

import com.conference.roomservice.controller.dto.RoomDTO;
import com.conference.roomservice.entity.Room;
import com.conference.roomservice.repository.RoomRepository;
import com.conference.roomservice.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "roomsCache")
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    @Cacheable(value="roomsInfo")
    public List<Room> retrieveAllRooms() {
        return roomRepository.findAll();

    }

    @Override
    public List<Room> findRoomsByCapacity(int numberOfPersons) {
        return roomRepository.findByRoomCapacityGreaterThanEqual(numberOfPersons);

    }
}
