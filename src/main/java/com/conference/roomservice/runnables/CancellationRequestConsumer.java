package com.conference.roomservice.runnables;

import com.conference.roomservice.controller.dto.CancelBookingDTO;
import com.conference.roomservice.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TransferQueue;

@Service
public class CancellationRequestConsumer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(CancellationRequestConsumer.class);

    private final TransferQueue<CancelBookingDTO> cancelTransferQueue;
    private final ReservationService reservationService;

    @Autowired
    public CancellationRequestConsumer(TransferQueue<CancelBookingDTO> cancelTransferQueue,
                                       ReservationService reservationService) {
        this.cancelTransferQueue = cancelTransferQueue;
        this.reservationService = reservationService;
    }

    @Override
    public void run() {
        while (true) {//work indefinitely
            try {
                CancelBookingDTO request = cancelTransferQueue.take();
                logger.info(new StringBuilder().append("Processing cancellation request for client: ").append(request.getUuid()).toString());
                deleteReservation(request);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private void deleteReservation(CancelBookingDTO request) {
        try {
            reservationService.deleteReservation(request);
        } catch (Exception e) {
            logger.info("Processing failed for the cancellation request for client: " + request.getUuid());
            logger.error(e.getMessage());
        }
    }
}