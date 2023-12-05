package com.conference.roomservice.service.impl;

import com.conference.roomservice.entity.ScheduleEntity;
import com.conference.roomservice.repository.ScheduleRepository;
import com.conference.roomservice.service.SchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@CacheConfig(cacheNames = "scheduleCache")
public class SchedulerServiceImpl implements SchedulerService {
    private final ScheduleRepository scheduleRepository;

    @Autowired
    public SchedulerServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public List<ScheduleEntity> getMaintenanceSchedules() {
        return scheduleRepository.findAll();
    }

    @Override
    @Cacheable(value = "activeSchedule")
    public List<ScheduleEntity> getActiveMaintenanceSchedules() {
        return scheduleRepository.findByIsActive(true);
    }

    @Override
    public boolean isOverlappingMaintenance(LocalTime startTime, LocalTime endTime) {
        List<ScheduleEntity> scheduleList = getActiveMaintenanceSchedules();
        // Check if the input range overlaps with the schedule range
        return scheduleList.stream()
                .anyMatch(scheduleEntity ->
                        (startTime.equals(scheduleEntity.getStartTime()) ||
                                startTime.isAfter(scheduleEntity.getStartTime()) &&
                                        (endTime.equals(scheduleEntity.getEndTime()) ||
                                                endTime.isBefore(scheduleEntity.getEndTime()))));
    }


}
