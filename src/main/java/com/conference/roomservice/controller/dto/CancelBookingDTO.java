package com.conference.roomservice.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;
@Builder
@Getter
@Setter
public class CancelBookingDTO {
    @NotNull
    private Integer roomId;

    @NotNull
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime time;

    @JsonIgnore
    private String uuid;
}
