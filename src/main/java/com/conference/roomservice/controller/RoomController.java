package com.conference.roomservice.controller;

import com.conference.roomservice.entity.Room;
import com.conference.roomservice.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/rooms")
public class RoomController {
    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @Operation(summary = "Get all the rooms")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieve all the rooms", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Room.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)})
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Room> getRooms() {
        return roomService.retrieveAllRooms();
    }
}