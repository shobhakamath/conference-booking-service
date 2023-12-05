package com.conference.roomservice.bst;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Builder
@Getter
@Setter
public class ReservationTree {

    private ReservationTree left;
    private ReservationTree right;

    private final LocalTime element;
    private final long duration;

    public ReservationTree(LocalTime element, long duration) {
        this.duration = duration;
        this.element = element;
        this.left = null;
        this.right = null;
    }

    public ReservationTree(ReservationTree left, ReservationTree right, LocalTime element, long duration) {
        this.duration = duration;
        this.element = element;
        this.left = left;
        this.right = right;
    }
}