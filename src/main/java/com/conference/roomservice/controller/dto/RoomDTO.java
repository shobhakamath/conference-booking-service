package com.conference.roomservice.controller.dto;

import lombok.Builder;

@Builder
public class RoomDTO {
    private int id;
    private String roomName;
    private String roomDescription;
    private int roomCapacity;
}
