package com.conference.roomservice.controller;

import com.conference.roomservice.bst.RoomReservationBST;
import com.conference.roomservice.controller.dto.CancelBookingDTO;
import com.conference.roomservice.controller.dto.CreateBookingDTO;
import com.conference.roomservice.controller.dto.ResponseDTO;
import com.conference.roomservice.entity.ReservationEntity;
import com.conference.roomservice.entity.Room;
import com.conference.roomservice.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/v1/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }


    //TODO validation not working
    @Operation(summary = "Reserve a room")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request queued successfully", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ReservationEntity.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)})
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO createBooking(@Valid @RequestBody CreateBookingDTO bookingDTO) {
        return bookingService.createBooking(bookingDTO);
    }


    @Operation(summary = "Cancel a booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Room reserved successfully", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ReservationEntity.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)})
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO cancelBooking(@Valid @RequestBody CancelBookingDTO cancelBookingDTO) {
        return bookingService.cancelBooking(cancelBookingDTO);
    }

    @Operation(summary = "Get reservations by pagination based on request params ")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ReservationEntity> getReservationsPages(
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "HH:mm")
            LocalTime startTime,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "HH:mm")
            LocalTime endTime,
            @RequestParam(required = false)
            Set<Integer> roomIds,
            Pageable pageable) {
        startTime= Optional.ofNullable(startTime).orElse(LocalTime.of(0,0));
        endTime=Optional.ofNullable(endTime).orElse(LocalTime.of(23,59));
        return bookingService.findBySearchParameters(startTime, endTime, roomIds, pageable);
    }


    @Operation(summary = "Get available slots by pagination based on request params ")
    @GetMapping("/availableSlots")
    @ResponseStatus(HttpStatus.OK)
    public Map<Room, List<RoomReservationBST.Slots>> getAvailableSlots(
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "HH:mm")
            LocalTime startTime,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "HH:mm")
            LocalTime endTime,
            @RequestParam(required = false)
            Set<Integer> roomIds ) {
        startTime= Optional.ofNullable(startTime).orElse(LocalTime.of(0,0));
        endTime=Optional.ofNullable(endTime).orElse(LocalTime.of(23,59));
        return bookingService.findAvailableSlotsBySearchParameters(startTime, endTime, roomIds);
    }



}
