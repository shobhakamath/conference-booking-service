package com.conference.roomservice.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ResponseDTO {
    private String uuid;
    private String response;
}
