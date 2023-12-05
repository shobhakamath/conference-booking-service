package com.conference.roomservice.service;

import com.conference.roomservice.bst.RoomReservationBST;
import com.conference.roomservice.controller.dto.CancelBookingDTO;
import com.conference.roomservice.controller.dto.CreateBookingDTO;
import com.conference.roomservice.entity.ReservationEntity;
import com.conference.roomservice.entity.Room;
import com.conference.roomservice.exception.OperationNotAllowedException;
import com.conference.roomservice.exception.OverlappingMaintenanceException;
import com.conference.roomservice.repository.ReservationRepository;
import com.conference.roomservice.service.impl.ReservationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.conference.roomservice.constant.ConferenceConstants.DATE_FORMATTER;
import static com.conference.roomservice.constant.ConferenceConstants.OVERLAP_MAINTENANCE;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReservationServiceImplTest {
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private RoomService roomService;
    @Mock
    private SchedulerService schedulerService;
    private  Map<Integer, RoomReservationBST<LocalTime>> roomTypeReservationsCache=new HashMap<>();

    private ReservationServiceImpl reservationService;

    @BeforeAll
    public void setup() {
        MockitoAnnotations.openMocks(this);
        reservationService = new ReservationServiceImpl(reservationRepository, roomService, schedulerService, roomTypeReservationsCache);

    }

    @Test
    public void testCreateReservation() {
        when(schedulerService.isOverlappingMaintenance(any(), any())).thenReturn(false);
        when(roomService.findRoomsByCapacity(anyInt())).thenReturn(List.of(Room.builder().id(2).build()));
        when(roomService.retrieveAllRooms()).thenReturn(List.of(Room.builder().id(1).build(),Room.builder().id(2).build()));
        List<ReservationEntity> reservations = List.of(ReservationEntity.builder()
                .id(1)
                .roomId(1)
                .attendeesCount(2)
                .endTime(LocalTime.of(21,30))
                .startTime(LocalTime.of(21,0))
                .timeDuration(30)
                .build());
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<ReservationEntity> page = new PageImpl<>(reservations, pageable, reservations.size());

        when(reservationRepository.findByRoomIdInAndMeetingDateAndAndStartTimeBetween(anySet(),any(),any(),any(),any()))
                .thenReturn(page);

        reservationService.clearCache();
        reservationService.createReservation(CreateBookingDTO.builder()
                .date(DATE_FORMATTER.format(LocalDate.now()))
                .startTime(LocalTime.of(15, 0))
                .endTime(LocalTime.of(15, 15))
                .noOfPersons(2)
                .meetingTitle("Test Title")
                .emailId("e@mail.com")
                .build());

        Assertions.assertEquals("[15:00 to 15:15, 21:00 to 21:30]",roomTypeReservationsCache.get(2).printInOrder().toString());
        Assertions.assertEquals("[21:00 to 21:30]",roomTypeReservationsCache.get(1).printInOrder().toString());
    }

    @Test
    public void testOperationNotAllowedException(){
        when(schedulerService.isOverlappingMaintenance(any(), any())).thenReturn(false);
        when(roomService.findRoomsByCapacity(anyInt())).thenReturn(List.of(Room.builder().id(2).build()));
        when(roomService.retrieveAllRooms()).thenReturn(List.of(Room.builder().id(1).build(),Room.builder().id(2).build()));
        List<ReservationEntity> reservations = List.of(ReservationEntity.builder()
                .id(1)
                .roomId(1)
                .attendeesCount(2)
                .endTime(LocalTime.of(21,30))
                .startTime(LocalTime.of(21,0))
                .timeDuration(30)
                .build());
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<ReservationEntity> page = new PageImpl<>(reservations, pageable, reservations.size());

        when(reservationRepository.findByRoomIdInAndMeetingDateAndAndStartTimeBetween(anySet(),any(),any(),any(),any()))
                .thenReturn(page);
        reservationService.clearCache();

        OperationNotAllowedException thrown = assertThrows(
                OperationNotAllowedException.class,
                () ->reservationService.createReservation(CreateBookingDTO.builder()
                .date(DATE_FORMATTER.format(LocalDate.now()))
                .startTime(LocalTime.of(21, 0))
                .endTime(LocalTime.of(21, 15))
                .noOfPersons(2)
                .meetingTitle("Test Title")
                .emailId("e@mail.com")
                .build()));
        Assertions.assertEquals("Cannot create the booking", thrown.getMessage());
    }


    @Test
    public void deleteReservation(){
        when(schedulerService.isOverlappingMaintenance(any(), any())).thenReturn(false);
        when(roomService.findRoomsByCapacity(anyInt())).thenReturn(List.of(Room.builder().id(2).build()));
        when(roomService.retrieveAllRooms()).thenReturn(List.of(Room.builder().id(1).build(),Room.builder().id(2).build()));
        List<ReservationEntity> reservations = List.of(ReservationEntity.builder()
                .id(1)
                .roomId(1)
                .attendeesCount(2)
                .endTime(LocalTime.of(21,30))
                .startTime(LocalTime.of(21,0))
                .timeDuration(30)
                .build());
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<ReservationEntity> page = new PageImpl<>(reservations, pageable, reservations.size());

        when(reservationRepository.findByRoomIdInAndMeetingDateAndAndStartTimeBetween(anySet(),any(),any(),any(),any()))
                .thenReturn(page);
        //create reservation
        reservationService.clearCache();
        reservationService.createReservation(CreateBookingDTO.builder()
                .date(DATE_FORMATTER.format(LocalDate.now()))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(9, 15))
                .noOfPersons(2)
                .meetingTitle("Test Title")
                .emailId("e@mail.com")
                .build());
        Assertions.assertEquals("[09:00 to 09:15, 21:00 to 21:30]",roomTypeReservationsCache.get(2).printInOrder().toString());
        Assertions.assertEquals("[21:00 to 21:30]",roomTypeReservationsCache.get(1).printInOrder().toString());

        when(reservationRepository.findByRoomIdAndStartTimeAndMeetingDate(anyInt(),any(),any()))
                        .thenReturn(ReservationEntity.builder().roomId(1).id(1).build());
        reservationService.deleteReservation(CancelBookingDTO.builder()
                .time(LocalTime.of(21,0))
                        .roomId(1)
                .build());
        Assertions.assertEquals("[09:00 to 09:15, 21:00 to 21:30]",roomTypeReservationsCache.get(2).printInOrder().toString());
        Assertions.assertEquals("[]",roomTypeReservationsCache.get(1).printInOrder().toString());

    }
}
