package com.conference.roomservice.config;

import com.conference.roomservice.bst.RoomReservationBST;
import com.conference.roomservice.controller.dto.CancelBookingDTO;
import com.conference.roomservice.controller.dto.CreateBookingDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

@Configuration
public class ApplicationConfiguration {
    @Bean
    public TransferQueue<CreateBookingDTO> createTransferQueue() {
        return new LinkedTransferQueue<>();
    }

    @Bean
    public TransferQueue<CancelBookingDTO> cancelTransferQueue() {
        return new LinkedTransferQueue<>();
    }

    @Bean
    public Map<Integer, RoomReservationBST<LocalTime>> roomTypeReservationsCache() {
        return new ConcurrentHashMap<>();
    }
}
