package com.conference.roomservice.integration;

import com.conference.roomservice.config.TestRedisConfiguration;
import com.conference.roomservice.controller.dto.CancelBookingDTO;
import com.conference.roomservice.controller.dto.CreateBookingDTO;
import com.conference.roomservice.entity.ReservationEntity;
import com.conference.roomservice.repository.ReservationRepository;
import com.conference.roomservice.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static com.conference.roomservice.constant.ConferenceConstants.DATE_FORMATTER;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestRedisConfiguration.class)
@AutoConfigureMockMvc
public class RoomBookingIntegrationTest {

    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testInsertionDeletion() throws Exception {
        //try to book meeting room for 2 persons. it books room id 1
        createBookingMVC();
        //try to book meeting room for 2 persons. it books next available room with id 2
        createBookingMVC();
        //try to book meeting room for 2 persons. it books next available room with id 3
        createBookingMVC();
        //try to book meeting room for 2 persons. it books next available room with id 4
        createBookingMVC();

        waitAndAssertCheck(4);
        //we delete the booking of room id 1 for that time
        mockMvc.perform(delete("/v1/bookings")
                        .content(TestUtils.asJsonString(CancelBookingDTO.builder()
                                .roomId(1)
                                .time(LocalTime.of(15, 0))
                                .build())).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        waitAndAssertCheck(3);
        //try to book meeting room for 2 persons. it books room id 1 as it is free now
        createBookingMVC();
        waitAndAssertCheck(4);
    }

    private void createBookingMVC() throws Exception {
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
                .andExpect(jsonPath("$.response", is("Queued to create the booking")))
                .andReturn();
    }
    private void waitAndAssertCheck(int expectedSize) throws Exception{
        Thread.sleep(1000);
        List<ReservationEntity> reservationEntity = reservationRepository.findByRoomIdInAndMeetingDateAndAndStartTimeBetween(Set.of(1, 2, 3, 4),
                LocalDate.now(), LocalTime.of(15, 0), LocalTime.of(15, 15), Pageable.unpaged()).stream().toList();
        Assertions.assertEquals(expectedSize,reservationEntity.size());
    }

}
