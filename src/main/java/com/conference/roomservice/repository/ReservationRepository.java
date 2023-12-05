package com.conference.roomservice.repository;

import com.conference.roomservice.entity.ReservationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long>, PagingAndSortingRepository<ReservationEntity, Long> {
    ReservationEntity findByRoomIdAndStartTimeAndMeetingDate(int roomId, LocalTime startTime, LocalDate localDate);

    Page<ReservationEntity> findByRoomIdInAndMeetingDateAndAndStartTimeBetween(Set<Integer> roomIds, LocalDate date, LocalTime startTime, LocalTime endTime, Pageable pageable);

}
