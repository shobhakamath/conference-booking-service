package com.conference.roomservice.controller;

import com.conference.roomservice.controller.dto.CancelBookingDTO;
import com.conference.roomservice.controller.dto.CreateBookingDTO;
import com.conference.roomservice.controller.dto.ResponseDTO;
import com.conference.roomservice.entity.ReservationEntity;
import com.conference.roomservice.exception.BookingNotFoundException;
import com.conference.roomservice.exception.OverlappingMaintenanceException;
import com.conference.roomservice.service.BookingService;
import com.conference.roomservice.utils.TestUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.conference.roomservice.constant.ConferenceConstants.DATE_FORMATTER;
import static com.conference.roomservice.constant.ConferenceConstants.OVERLAP_MAINTENANCE;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;


    @Test
    public void testCreateBookingSuccess() throws Exception {
        when(bookingService.createBooking(any())).thenReturn(ResponseDTO.builder()
                .uuid("uuid")
                .response("Queued to create the booking")
                .build());

        // Perform the POST request
        mockMvc.perform(post("/v1/bookings")
                        .content(TestUtils.asJsonString(CreateBookingDTO.builder()
                                .date(DATE_FORMATTER.format(LocalDate.now()))
                                .startTime(LocalTime.of(15, 0))
                                .endTime(LocalTime.of(15, 15))
                                .noOfPersons(2)
                                .meetingTitle("Test Title")
                                        .emailId("e@mail.com")
                                .build())).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").exists())
                .andExpect(jsonPath("$.uuid", is("uuid")))
                .andReturn();


        verify(bookingService, times(1)).createBooking(any());
    }


    @Test
    public void testCreateBookingOverlappingError() throws Exception {
        when(bookingService.createBooking(any())).thenThrow(new OverlappingMaintenanceException(OVERLAP_MAINTENANCE));

        // Perform the POST request
        mockMvc.perform(post("/v1/bookings")
                        .content(TestUtils.asJsonString(CreateBookingDTO.builder()
                                .date(DATE_FORMATTER.format(LocalDate.now()))
                                .startTime(LocalTime.of(15, 0))
                                .endTime(LocalTime.of(15, 15))
                                .noOfPersons(2)
                                .meetingTitle("Test Title")
                                .emailId("e@mail.com")
                                .build())).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages").exists())
                .andExpect(jsonPath("$.messages[0]", is("The meeting overlaps with room maintenance time")))
                .andReturn();


        verify(bookingService, times(1)).createBooking(any());
    }
    @Test
    public void testCreateBookingFutureDate() throws Exception {
        // Perform the POST request
        mockMvc.perform(post("/v1/bookings")
                        .content(TestUtils.asJsonString(CreateBookingDTO.builder()
                                .date(DATE_FORMATTER.format(LocalDate.now().plusDays(20)))
                                .startTime(LocalTime.of(15, 0))
                                .endTime(LocalTime.of(15, 15))
                                .noOfPersons(2)
                                .meetingTitle("Test Title")
                                .emailId("e@mail.com")
                                .build())).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages").exists())
                .andExpect(jsonPath("$.messages[0]", is("date: Date must be today")))
                .andReturn();

        verify(bookingService, times(0)).createBooking(any());
    }

    @Test
    public void testCreateBookingTimesWrongFormat() throws Exception {

        // Perform the POST request
        mockMvc.perform(post("/v1/bookings")
                        .content(String.format("""
                                {
                                    "date":  "%s",
                                    "startTime":"24:00",
                                    "endTime": "13:45",
                                    "noOfPersons": 20,
                                    "meetingTitle": "Test title"
                                }
                                """, DATE_FORMATTER.format(LocalDate.now())))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.messages[0]", is("JSON parse error: Cannot deserialize value of type `java.time.LocalTime` from String \"24:00\": Failed to deserialize java.time.LocalTime: (java.time.format.DateTimeParseException) Text '24:00' could not be parsed: Invalid value for HourOfDay (valid values 0 - 23): 24")))
                .andReturn();

        verify(bookingService, times(0)).createBooking(any());
    }

    @Test
    public void testCreateBookingDurationMultipleOf15() throws Exception {

        // Perform the POST request
        mockMvc.perform(post("/v1/bookings")
                        .content(TestUtils.asJsonString(CreateBookingDTO.builder()
                                .date(DATE_FORMATTER.format(LocalDate.now()))
                                .startTime(LocalTime.of(3, 0))
                                .endTime(LocalTime.of(2, 14))
                                .noOfPersons(2)
                                .meetingTitle("Test Title")
                                .build())).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.messages", hasSize(4)))
                .andExpect(jsonPath("$.messages").exists())
                .andExpect(jsonPath("$.messages", containsInAnyOrder(
                        is("emailId: must not be null"),
                        is("emailId: must not be empty"),
                        is("endTime: The end time is less than the start time."),
                        is("endTime: The duration must be a multiple of 15 minutes.")
                )))
                .andReturn();

        verify(bookingService, times(0)).createBooking(any());
    }

    @Test
    public void testGetReservations() throws Exception {
        List<ReservationEntity> reservations = List.of(ReservationEntity.builder()
                .id(1)
                .roomId(1)
                .attendeesCount(2)
                .endTime(LocalTime.now().plusMinutes(15))
                .startTime(LocalTime.now())
                .timeDuration(15)
                .build());
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<ReservationEntity> page = new PageImpl<>(reservations, pageable, reservations.size());
        when(bookingService.findBySearchParameters(any(), any(), any(), any())).thenReturn(page);


        mockMvc.perform(get("/v1/bookings")
                        .param("startTime", String.valueOf(LocalTime.now()))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].roomId").value(1))
                .andExpect(jsonPath("$.content[0].timeDuration").value(15))
                .andExpect(jsonPath("$.content[0].attendeesCount").value(2))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(10));
    }
    @Test
    public void testDeleteReservationQueueingSuccess() throws Exception {
        when(bookingService.cancelBooking(any())).thenReturn(ResponseDTO.builder()
                .uuid("uuid")
                .response("Queued to create the booking")
                .build());
        mockMvc.perform(delete("/v1/bookings")
                        .content(TestUtils.asJsonString(CancelBookingDTO.builder()
                                        .time(LocalTime.of(1,0))
                                        .roomId(1)
                                .build())).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").exists())
                .andExpect(jsonPath("$.uuid", is("uuid")))
                .andReturn();
    }

    @Test
    public void testDeleteReservationBookingNotFound() throws Exception {
        when(bookingService.cancelBooking(any())).thenThrow(new BookingNotFoundException("Unable to find the specified booking"));
        mockMvc.perform(delete("/v1/bookings")
                        .content(TestUtils.asJsonString(CancelBookingDTO.builder()
                                .time(LocalTime.of(1,0))
                                .roomId(1)
                                .build())).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages").exists())
                .andExpect(jsonPath("$.messages[0]", is("Unable to find the specified booking")))
                .andReturn();
    }

}
