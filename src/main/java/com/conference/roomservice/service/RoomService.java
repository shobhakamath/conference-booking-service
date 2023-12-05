package com.conference.roomservice.service;

import com.conference.roomservice.entity.Room;

import java.util.List;

public interface RoomService {
    List<Room> retrieveAllRooms();

    List<Room> findRoomsByCapacity(int numberOfPersons);
}
