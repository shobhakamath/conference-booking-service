package com.conference.roomservice.service.impl;

import com.conference.roomservice.bst.RoomReservationBst;
import com.conference.roomservice.controller.dto.CancelBookingDTO;
import com.conference.roomservice.controller.dto.CreateBookingDTO;
import com.conference.roomservice.controller.dto.ResponseDTO;
import com.conference.roomservice.entity.ReservationEntity;
import com.conference.roomservice.entity.Room;
import com.conference.roomservice.exception.BookingNotFoundException;
import com.conference.roomservice.exception.OverlappingMaintenanceException;
import com.conference.roomservice.service.BookingService;
import com.conference.roomservice.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TransferQueue;

import static com.conference.roomservice.constant.ConferenceConstants.OVERLAP_MAINTENANCE;

@Service
public class BookingServiceImpl implements BookingService {
    private final ReservationService reservationService;
    private final TransferQueue<CreateBookingDTO> createTransferQueue;
    private final TransferQueue<CancelBookingDTO> cancelTransferQueue;


    @Autowired
    public BookingServiceImpl(ReservationService reservationService,
                              TransferQueue<CreateBookingDTO> createTransferQueue,
                              TransferQueue<CancelBookingDTO> cancelTransferQueue) {
        this.reservationService = reservationService;
        this.createTransferQueue = createTransferQueue;
        this.cancelTransferQueue = cancelTransferQueue;
    }


    @Override
    public ResponseDTO createBooking(CreateBookingDTO bookingDTO) {
        if (reservationService.isOverlappingMaintenance(bookingDTO.getStartTime(), bookingDTO.getEndTime()))
            throw new OverlappingMaintenanceException(OVERLAP_MAINTENANCE);
        String uuid = UUID.randomUUID().toString();
        bookingDTO.setUuid(uuid);
        createTransferQueue.add(bookingDTO);
        return ResponseDTO.builder()
                .uuid(uuid)
                .response("Queued to create the booking")
                .build();
    }

    @Override
    public ResponseDTO cancelBooking(CancelBookingDTO cancelBookingDTO) {
        if (reservationService.findByRoomIdAndStartTime(cancelBookingDTO.getRoomId(), cancelBookingDTO.getTime()) == null)
            throw new BookingNotFoundException("Unable to find the specified booking");
        String uuid = UUID.randomUUID().toString();
        cancelBookingDTO.setUuid(uuid);
        cancelTransferQueue.add(cancelBookingDTO);
        return ResponseDTO.builder()
                .uuid(uuid)
                .response("Queued to delete the booking")
                .build();
    }

    @Override
    public Page<ReservationEntity> findBySearchParameters(LocalTime startTime, LocalTime endTime, Set<Integer> rooms, Pageable pageable) {
        startTime = Optional.ofNullable(startTime).orElse(LocalTime.of(0, 0));
        endTime = Optional.ofNullable(endTime).orElse(LocalTime.of(23, 59));
        return reservationService.findBySearchParameters(startTime, endTime, rooms, pageable);
    }

    @Override
    public Map<Room, List<RoomReservationBst.Slots>> findAvailableSlotsBySearchParameters(LocalTime startTime, LocalTime endTime, Set<Integer> roomIds) {
        return reservationService.findAvailableSlotsBySearchParameters(startTime, endTime, roomIds);
    }
}
