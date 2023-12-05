package com.conference.roomservice.repository;

import com.conference.roomservice.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<ScheduleEntity,Integer> {
    List<ScheduleEntity> findByIsActive(boolean isActive);
}
