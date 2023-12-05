package com.conference.roomservice.config;

import com.conference.roomservice.controller.dto.CancelBookingDTO;
import com.conference.roomservice.controller.dto.CreateBookingDTO;
import com.conference.roomservice.runnables.CancellationRequestConsumer;
import com.conference.roomservice.runnables.ReservationRequestConsumer;
import com.conference.roomservice.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TransferQueue;

@Component
public class ApplicationStartupListener {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartupListener.class);

    private final TransferQueue<CreateBookingDTO> createTransferQueue;
    private final TransferQueue<CancelBookingDTO> cancelTransferQueue;
    private final ReservationService reservationService;

    @Autowired
    public ApplicationStartupListener(TransferQueue<CreateBookingDTO> createTransferQueue,
                                      TransferQueue<CancelBookingDTO> cancelTransferQueue,
                                      ReservationService reservationService) {
        this.createTransferQueue = createTransferQueue;
        this.cancelTransferQueue = cancelTransferQueue;
        this.reservationService = reservationService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        logger.info("Application ready.Reserve for maintenance schedule and load the BST cache");
        reservationService.reserveRoomForMaintenance();
        reservationService.clearCache();
        logger.info("ReservationRequestConsumer and CancellationRequestConsumer started");
        new Thread(new ReservationRequestConsumer(createTransferQueue, reservationService)).start();
        new Thread(new CancellationRequestConsumer(cancelTransferQueue, reservationService)).start();


    }
}