package com.conference.roomservice.runnables;

import com.conference.roomservice.controller.dto.CreateBookingDTO;
import com.conference.roomservice.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TransferQueue;

@Service
public class ReservationRequestConsumer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ReservationRequestConsumer.class);

    private final TransferQueue<CreateBookingDTO> createTransferQueue;
    private final ReservationService reservationService;

    @Autowired
    public ReservationRequestConsumer(TransferQueue<CreateBookingDTO> createTransferQueue,
                                      ReservationService ReservationService) {
        this.createTransferQueue = createTransferQueue;
        this.reservationService = ReservationService;
    }

    @Override
    public void run() {
        while (true) {
            try {
                CreateBookingDTO request = createTransferQueue.take();
                logger.info("Processing reservation request for client: " + request.getUuid());
                try {
                    reservationService.createReservation(request);
                } catch (Exception e) {
                    logger.info("Processing failed for the reservation request for client: " + request.getUuid());
                    logger.error(e.getMessage());
                }
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }
    }
}