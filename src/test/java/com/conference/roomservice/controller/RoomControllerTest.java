package com.conference.roomservice.controller;

import com.conference.roomservice.entity.Room;
import com.conference.roomservice.service.RoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//TODO junit version details. its better to check all the versions of the dependencies
// and check all the import statements and get details
@WebMvcTest(RoomController.class)
public class RoomControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RoomService roomService;


    @Test
    public void testCreateNotes() throws Exception {
        // Mock the service behavior
        when(roomService.retrieveAllRooms()).thenReturn(List.of(Room.builder()
                .id(1)
                .roomCapacity(5)
                .roomName("Roomie")
                .build()));

        // Perform the POST request
        mockMvc.perform(get("/v1/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpectAll(
                        jsonPath("$[0].roomName").exists(),
                        jsonPath("$[0].roomName").value("Roomie")
                ).andReturn();


        verify(roomService, times(1)).retrieveAllRooms();
    }


}
