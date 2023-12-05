package com.conference.roomservice.service;


import com.conference.roomservice.bst.RoomReservationBST;
import com.conference.roomservice.controller.dto.CancelBookingDTO;
import com.conference.roomservice.controller.dto.CreateBookingDTO;
import com.conference.roomservice.controller.dto.ResponseDTO;
import com.conference.roomservice.entity.ReservationEntity;
import com.conference.roomservice.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BookingService {
    ResponseDTO createBooking(CreateBookingDTO bookingDTO);
    ResponseDTO cancelBooking(CancelBookingDTO cancelBookingDTO);
    Page<ReservationEntity> findBySearchParameters(LocalTime startDateTime, LocalTime endDateTime, Set<Integer> rooms, Pageable pageable);
    Map<Room, List<RoomReservationBST.Slots>> findAvailableSlotsBySearchParameters(LocalTime startTime, LocalTime endTime, Set<Integer> roomIds);
}

