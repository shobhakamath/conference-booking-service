package com.conference.roomservice.exception;

public class RoomNotFoundException  extends RuntimeException {
        public RoomNotFoundException(String message) {
            super(message);
        }
    }

