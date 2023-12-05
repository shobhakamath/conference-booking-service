package com.conference.roomservice.service;

import com.conference.roomservice.entity.ScheduleEntity;

import java.time.LocalTime;
import java.util.List;

public interface SchedulerService {
    List<ScheduleEntity> getMaintenanceSchedules();

    List<ScheduleEntity> getActiveMaintenanceSchedules();

    boolean isOverlappingMaintenance(LocalTime startTime, LocalTime endTime);
}
