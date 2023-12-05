package com.conference.roomservice.bst;


import com.conference.roomservice.exception.OperationNotAllowedException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;

public class RoomReservationBst<T extends Temporal> {


    private ReservationTree root = null;

    public void insert(LocalTime element, long time) {

        if (element == null) {
            throw new IllegalArgumentException("Cannot pass null arguments");
        }

        root = insert(root, element, time);
    }

    private ReservationTree insert(ReservationTree current, LocalTime element, long time) {

        if (current == null) {
            return new ReservationTree(element, time);
        }

        long t1 = Duration.between(current.getElement().plusMinutes(current.getDuration()), element).toMinutes();
        long t2 = Duration.between(current.getElement(), element.plusMinutes(time)).toMinutes();

        if (t1 <= 0 && t2 >= 0) {
            throw new OperationNotAllowedException("Cannot reserve the book at "
                    + element + " for " + time + " minutes !");
        }

        if (element.isBefore(current.getElement())) {
            current.setLeft(insert(current.getLeft(), element, time));
        } else {
            current.setRight(insert(current.getRight(), element, time));
        }

        return current;
    }


    public void delete(LocalTime element) {
        root = delete(root, element);
    }

    private ReservationTree delete(ReservationTree current, LocalTime element) {
        if (current == null) {
            return null; // Element not found
        }

        int compareResult = element.compareTo(current.getElement());

        if (compareResult < 0) {
            current.setLeft(delete(current.getLeft(), element));
        } else if (compareResult > 0) {
            current.setRight(delete(current.getRight(), element));
        } else {
            // Node with only one child or no child
            if (current.getLeft() == null) {
                return current.getRight();
            } else if (current.getRight() == null) {
                return current.getLeft();
            }

            // Node with two children: Find the in-order successor (or predecessor)
            ReservationTree minRight = findMin(current.getRight());
            ReservationTree right = delete(current.getRight(), minRight.getElement());
            minRight.setLeft(current.getLeft());
            minRight.setRight(right);
            current = minRight;
        }

        return current;
    }

    private ReservationTree findMin(ReservationTree node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
    }

    public List<Slots> printInOrder() {
        List<Slots> slots = new ArrayList<>();
        printInOrder(root, slots);
        return slots;
    }

    private void printInOrder(ReservationTree node, List<Slots> slots) {
        if (node != null) {
            printInOrder(node.getLeft(), slots);
            slots.add(new Slots(node.getElement(), node.getElement().plusMinutes(node.getDuration())));
            printInOrder(node.getRight(), slots);
        }
    }

    public List<Slots> findAvailableSlots(LocalTime startTime, LocalTime endTime) {
        List<Slots> availableSlots = new ArrayList<>();
        findAvailableSlots(root, LocalTime.MIN, LocalTime.MAX, availableSlots);
        List<Slots> list = new ArrayList<>();
        for (Slots slot : availableSlots) {
            if (!slot.endTime.isBefore(startTime) && !slot.startTime.isAfter(endTime)) {
                list.add(slot);
            }
        }
        return list;
    }

    private void findAvailableSlots(ReservationTree node, LocalTime startTime, LocalTime endTime, List<Slots> availableSlots) {
        if (node == null) {
            // Base case: empty node
            availableSlots.add(new Slots(startTime, endTime));
            return;
        }

        // Check left subtree
        findAvailableSlots(node.getLeft(), startTime, node.getElement(), availableSlots);

        // Check right subtree
        findAvailableSlots(node.getRight(), node.getElement().plusMinutes(node.getDuration()), endTime, availableSlots);
    }


    @Builder
    @Getter
    @Setter
    public static class Slots {
        private final LocalTime startTime;
        private final LocalTime endTime;

        @Override
        public String toString() {
            return startTime + " to " + endTime;
        }
    }
}
