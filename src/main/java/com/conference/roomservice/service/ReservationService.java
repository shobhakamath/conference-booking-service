package com.conference.roomservice.service;

import com.conference.roomservice.bst.RoomReservationBst;
import com.conference.roomservice.controller.dto.CancelBookingDTO;
import com.conference.roomservice.controller.dto.CreateBookingDTO;
import com.conference.roomservice.entity.ReservationEntity;
import com.conference.roomservice.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ReservationService {


    ReservationEntity findByRoomIdAndStartTime(int roomId, LocalTime startTime);

    Page<ReservationEntity> findBySearchParameters(LocalTime startDateTime, LocalTime endDateTime, Set<Integer> rooms, Pageable pageable);

    boolean isOverlappingMaintenance(LocalTime startTime, LocalTime endTime);

    void clearCache();

    void createReservation(CreateBookingDTO bookingDTO);

    void deleteReservation(CancelBookingDTO cancelBookingDTO);

    void reserveRoomForMaintenance();

    Map<Room, List<RoomReservationBst.Slots>> findAvailableSlotsBySearchParameters(LocalTime startTime, LocalTime endTime, Set<Integer> roomIds);
}
