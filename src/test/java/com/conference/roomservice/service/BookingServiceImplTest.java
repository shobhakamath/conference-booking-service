package com.conference.roomservice.service;

import com.conference.roomservice.controller.dto.CancelBookingDTO;
import com.conference.roomservice.controller.dto.CreateBookingDTO;
import com.conference.roomservice.controller.dto.ResponseDTO;
import com.conference.roomservice.entity.ReservationEntity;
import com.conference.roomservice.exception.BookingNotFoundException;
import com.conference.roomservice.exception.OverlappingMaintenanceException;
import com.conference.roomservice.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.LinkedTransferQueue;

import static com.conference.roomservice.constant.ConferenceConstants.DATE_FORMATTER;
import static com.conference.roomservice.constant.ConferenceConstants.OVERLAP_MAINTENANCE;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingServiceImplTest {
    @Mock
    private ReservationService reservationService;
    @Mock
    private RoomService roomService;

    @Mock
    private SchedulerService schedulerService;
    private BookingServiceImpl bookingService;


    @BeforeAll
    public void setup() {
        MockitoAnnotations.openMocks(this);
        bookingService = new BookingServiceImpl(reservationService, new LinkedTransferQueue<>(), new LinkedTransferQueue<>());
    }

    @Test
    void testCreateBooking() throws Exception {
        when(reservationService.isOverlappingMaintenance(any(), any())).thenReturn(false);
        ResponseDTO responseDTO = bookingService.createBooking(CreateBookingDTO.builder()
                .date(DATE_FORMATTER.format(LocalDate.now()))
                .startTime(LocalTime.of(15, 0))
                .endTime(LocalTime.of(15, 15))
                .noOfPersons(2)
                .meetingTitle("Test Title")
                .emailId("e@mail.com")
                .build());
        Assertions.assertEquals("Queued to create the booking", responseDTO.getResponse());
    }

    @Test
    void testCreateBookingOverlapMaintenanceException() throws Exception {
        when(reservationService.isOverlappingMaintenance(any(), any())).thenReturn(true);
        OverlappingMaintenanceException thrown;
        thrown = Assertions.assertThrows(
                OverlappingMaintenanceException.class,
                () -> bookingService.createBooking(CreateBookingDTO.builder()
                        .date(DATE_FORMATTER.format(LocalDate.now()))
                        .startTime(LocalTime.of(15, 0))
                        .endTime(LocalTime.of(15, 15))
                        .noOfPersons(2)
                        .meetingTitle("Test Title")
                        .emailId("e@mail.com")
                        .build()));
        Assertions.assertEquals(OVERLAP_MAINTENANCE, thrown.getMessage());
    }

    @Test
    void testCancelBooking() throws Exception {
        when(reservationService.findByRoomIdAndStartTime(anyInt(), any())).thenReturn(ReservationEntity.builder().build());
        ResponseDTO responseDTO = bookingService.cancelBooking(CancelBookingDTO.builder()
                .roomId(1)
                .time(LocalTime.now())
                .build());
        Assertions.assertEquals("Queued to delete the booking", responseDTO.getResponse());
    }

    @Test
    void testCancelBookingNotFoundException() throws Exception {
        when(reservationService.findByRoomIdAndStartTime(anyInt(), any())).thenReturn(null);
        BookingNotFoundException thrown;
        thrown = assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.cancelBooking(CancelBookingDTO.builder()
                        .roomId(1)
                        .time(LocalTime.now())
                        .build()));
        Assertions.assertEquals("Unable to find the specified booking", thrown.getMessage());
    }

}
