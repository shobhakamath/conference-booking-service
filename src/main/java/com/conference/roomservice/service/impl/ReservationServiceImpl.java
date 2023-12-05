package com.conference.roomservice.service.impl;

import com.conference.roomservice.bst.RoomReservationBst;
import com.conference.roomservice.controller.dto.CancelBookingDTO;
import com.conference.roomservice.controller.dto.CreateBookingDTO;
import com.conference.roomservice.entity.ReservationEntity;
import com.conference.roomservice.entity.Room;
import com.conference.roomservice.entity.ScheduleEntity;
import com.conference.roomservice.exception.OperationNotAllowedException;
import com.conference.roomservice.exception.OverlappingMaintenanceException;
import com.conference.roomservice.exception.RoomNotFoundException;
import com.conference.roomservice.repository.ReservationRepository;
import com.conference.roomservice.service.ReservationService;
import com.conference.roomservice.service.RoomService;
import com.conference.roomservice.service.SchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.conference.roomservice.constant.ConferenceConstants.CANNOT_CREATE_BOOKING;
import static com.conference.roomservice.constant.ConferenceConstants.OVERLAP_MAINTENANCE;

@Service
public class ReservationServiceImpl implements ReservationService {
    private static final Logger logger = LoggerFactory.getLogger(ReservationServiceImpl.class);

    private final ReservationRepository reservationRepository;
    private final RoomService roomService;

    private final SchedulerService schedulerService;
    private final Map<Integer, RoomReservationBst<LocalTime>> roomTypeReservationsCache;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository, RoomService roomService, SchedulerService schedulerService, Map<Integer, RoomReservationBst<LocalTime>> roomTypeReservationsCache) {
        this.reservationRepository = reservationRepository;
        this.roomService = roomService;
        this.schedulerService = schedulerService;
        this.roomTypeReservationsCache = roomTypeReservationsCache;
    }


    @Override
    public ReservationEntity findByRoomIdAndStartTime(int roomId, LocalTime startTime) {
        return reservationRepository.findByRoomIdAndStartTimeAndMeetingDate(roomId, startTime, LocalDate.now());
    }

    @Override
    public Page<ReservationEntity> findBySearchParameters(LocalTime startDateTime, LocalTime endDateTime, Set<Integer> roomIds, Pageable pageable) {
        roomIds = Optional.ofNullable(roomIds)
                .orElse(roomService.retrieveAllRooms().stream().map(Room::getId).collect(Collectors.toSet()));
        return reservationRepository.findByRoomIdInAndMeetingDateAndAndStartTimeBetween(roomIds, LocalDate.now(), startDateTime, endDateTime, pageable);
    }

    @Override
    public boolean isOverlappingMaintenance(LocalTime startTime, LocalTime endTime) {
        return schedulerService.isOverlappingMaintenance(startTime, endTime);
    }


    @Override
    public void clearCache() {
        roomTypeReservationsCache.clear();
        List<Room> rooms = roomService.retrieveAllRooms();
        rooms.forEach(room -> {
            List<ReservationEntity> roomBookings = findBySearchParameters(LocalTime.of(0, 0), LocalTime.of(23, 59), Set.of(room.getId()), Pageable.unpaged()).stream().toList();
            roomTypeReservationsCache.put(room.getId(), createReservationFromEntities(new RoomReservationBst<LocalTime>(), roomBookings));
        });
    }

    @Override
    public void createReservation(CreateBookingDTO bookingDTO) {
        if (roomTypeReservationsCache.isEmpty()) {
            clearCache();
        }
        if (isOverlappingMaintenance(bookingDTO.getStartTime(), bookingDTO.getEndTime())) {
            //Can be pushed to a messaging queue to process later and also log the error in the database.
            //But it is not required as per the scope of the problem.
            logger.error(new StringBuilder().append("Could not create booking as it overlaps with maintenance schedule.Send mail to ").append(bookingDTO.getEmailId()).toString());
            throw new OverlappingMaintenanceException(OVERLAP_MAINTENANCE);
        }
        List<Room> tentativeRooms = roomService.findRoomsByCapacity(bookingDTO.getNoOfPersons());
        boolean reserved = false;
        int roomId = 0;
        long duration = Duration.between(bookingDTO.getStartTime(), bookingDTO.getEndTime()).toMinutes();
        for (Room room : tentativeRooms) {
            try {
                roomTypeReservationsCache.get(room.getId()).insert(bookingDTO.getStartTime(), duration);
                reserved = true;
                roomId = room.getId();
                break;
            } catch (OperationNotAllowedException e) {
                //too early to send a mail to the user.we need to check the next tentative room for availability.
                logger.info(new StringBuilder().append("Unable to book the room").append(roomId).append("for  client uuid :").append(bookingDTO.getUuid()).toString());

            }
        }
        if (!reserved) {
            //Can be pushed to a messaging queue to process later and also log the error in the database.
            //But it is not required as per the scope of the problem.
            logger.error("Could not create booking.Send mail to %sand client uuid :%s".formatted(bookingDTO.getEmailId(), bookingDTO.getUuid()));
            throw new OperationNotAllowedException(CANNOT_CREATE_BOOKING);
        }

        reservationRepository.save(ReservationEntity.builder()
                .roomId(roomId)
                .timeDuration(duration)
                .meetingDate(bookingDTO.getConferenceDate())
                .startTime(bookingDTO.getStartTime())
                .endTime(bookingDTO.getEndTime())
                .attendeesCount(bookingDTO.getNoOfPersons()).
                meetingTitle(bookingDTO.getMeetingTitle())
                .emailId(bookingDTO.getEmailId())
                .uuid(bookingDTO.getUuid())
                .build());
    }

    @Override
    public void deleteReservation(CancelBookingDTO cancelBookingDTO) {
        Optional.ofNullable(cancelBookingDTO).map(cancelBooking -> findByRoomIdAndStartTime(cancelBooking.getRoomId(), cancelBooking.getTime())).ifPresent(reservationEntity -> {
            reservationRepository.deleteById(reservationEntity.getId());
            roomTypeReservationsCache.get(cancelBookingDTO.getRoomId()).delete(cancelBookingDTO.getTime());
        });
    }

    @Override
    public void reserveRoomForMaintenance() {
        List<Room> rooms = roomService.retrieveAllRooms();
        List<ScheduleEntity> maintenanceScheduleEntities = schedulerService.getActiveMaintenanceSchedules();
        List<ReservationEntity> maintenanceReservationEntities = new ArrayList<>();
        for (ScheduleEntity scheduleEntity : maintenanceScheduleEntities) {
            for (Room room : rooms) {
                ReservationEntity regularMaintenance = ReservationEntity.builder()
                        .roomId(room.getId())
                        .meetingDate(LocalDate.now())
                        .startTime(scheduleEntity.getStartTime())
                        .endTime(scheduleEntity.getEndTime())
                        .timeDuration(Duration.between(scheduleEntity.getStartTime(), scheduleEntity.getEndTime()).toMinutes())
                        .meetingTitle("Regular maintenance")
                        .emailId("maintenance@room.com")
                        .uuid(UUID.randomUUID().toString())
                        .build();
                maintenanceReservationEntities.add(regularMaintenance);
            }
        }
        reservationRepository.saveAll(maintenanceReservationEntities);
    }

    @Override
    public Map<Room, List<RoomReservationBst.Slots>> findAvailableSlotsBySearchParameters(LocalTime startTime, LocalTime endTime, Set<Integer> roomIds) {
        Map<Room, List<RoomReservationBst.Slots>> availableSlots = new HashMap<>();
        Map<Integer, Room> rooms = roomService.retrieveAllRooms().stream().collect(Collectors.toMap(Room::getId, Function.identity()));
        roomIds = Optional.ofNullable(roomIds)
                .orElse(rooms.keySet());
        roomIds.forEach(roomId -> {
            Room room = Optional.ofNullable(rooms.get(roomId)).orElseThrow(() -> new RoomNotFoundException("The given room doesnt exist: " + roomId));
            RoomReservationBst<LocalTime> roomReservations = roomTypeReservationsCache.get(roomId);
            availableSlots.put(room, roomReservations.findAvailableSlots(startTime, endTime));
        });
        return availableSlots;
    }

    private <T extends Temporal> RoomReservationBst<T> createReservationFromEntities(RoomReservationBst<T> roomReservationBST, List<ReservationEntity> reservationEntities) {
        if (reservationEntities == null || reservationEntities.isEmpty()) {
            return roomReservationBST;
        }
        reservationEntities.stream()
                .forEach(reservationEntity ->
                        roomReservationBST.insert(reservationEntity.getStartTime(), reservationEntity.getTimeDuration()));

        return roomReservationBST;
    }
}
