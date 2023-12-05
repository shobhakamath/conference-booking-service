package com.conference.roomservice.config;

import com.conference.roomservice.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class ScheduleConfig {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleConfig.class);

    private final ReservationService reservationService;

    @Autowired
    public ScheduleConfig(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Scheduled(cron = "0 0 0 * * ?") // Runs every day at midnight (00:00:00)
    public void reserveMaintenanceSchedule() {
        logger.info("Scheduler running at midnight 00:00 : Reserve for maintenance schedule and load the BST cache" );
        reservationService.reserveRoomForMaintenance();
        reservationService.clearCache();

    }

}
