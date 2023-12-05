package com.conference.roomservice.bst;

import com.conference.roomservice.exception.OperationNotAllowedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReservationBSTTest {

    @Test
    public void testBSTInsertion() {
        RoomReservationBST<LocalTime> roomReservationBST = new RoomReservationBST<>();
        roomReservationBST.insert(LocalTime.of(0, 30), 30);
        roomReservationBST.insert(LocalTime.of(1, 1), 30);
        roomReservationBST.insert(LocalTime.of(1, 32), 30);
        Assertions.assertEquals("[00:30 to 01:00, 01:01 to 01:31, 01:32 to 02:02]", roomReservationBST.printInOrder().toString());
        OperationNotAllowedException thrown = assertThrows(
                OperationNotAllowedException.class,
                () -> roomReservationBST.insert(LocalTime.of(1, 30), 30)
        );
        Assertions.assertEquals("Cannot reserve the book at 01:30 for 30 minutes !", thrown.getMessage());
    }

    @Test
    public void testBSTRemoveRoot() {
        RoomReservationBST<LocalTime> roomReservationBST = new RoomReservationBST<>();
        roomReservationBST.insert(LocalTime.of(8, 30), 30);
        roomReservationBST.insert(LocalTime.of(6, 1), 30);
        roomReservationBST.insert(LocalTime.of(10, 32), 30);
        roomReservationBST.insert(LocalTime.of(7, 32), 30);
        roomReservationBST.insert(LocalTime.of(9, 32), 30);
        System.out.println("After deletion root");
        roomReservationBST.delete(LocalTime.of(8, 30));
        Assertions.assertEquals("[06:01 to 06:31, 07:32 to 08:02, 09:32 to 10:02, 10:32 to 11:02]", roomReservationBST.printInOrder().toString());
    }

    @Test
    public void testBSTDeletion() {
        RoomReservationBST<LocalTime> roomReservationBST = new RoomReservationBST<>();
        roomReservationBST.insert(LocalTime.of(0, 30), 30);
        roomReservationBST.insert(LocalTime.of(1, 1), 30);
        roomReservationBST.insert(LocalTime.of(1, 32), 30);
        roomReservationBST.delete(LocalTime.of(1, 1));
        Assertions.assertEquals("[00:30 to 01:00, 01:32 to 02:02]", roomReservationBST.printInOrder().toString());
    }

    @Test
    public void testBSTDeletionForLeftSkewedBST() {
        RoomReservationBST<LocalTime> roomReservationBST = new RoomReservationBST<>();
        roomReservationBST.insert(LocalTime.of(8, 30), 30);
        roomReservationBST.insert(LocalTime.of(7, 1), 30);
        roomReservationBST.insert(LocalTime.of(6, 30), 30);
        roomReservationBST.insert(LocalTime.of(5, 32), 30);
        Assertions.assertEquals("[05:32 to 06:02, 06:30 to 07:00, 07:01 to 07:31, 08:30 to 09:00]", roomReservationBST.printInOrder().toString());
        roomReservationBST.delete(LocalTime.of(8, 30));
        System.out.println("after deletion of root");
        Assertions.assertEquals("[05:32 to 06:02, 06:30 to 07:00, 07:01 to 07:31]", roomReservationBST.printInOrder().toString());
    }

    @Test
    public void testFindAvailableSlots() {
        RoomReservationBST<LocalTime> roomReservationBST = new RoomReservationBST<>();
        roomReservationBST.insert(LocalTime.of(8, 30), 30);
        roomReservationBST.insert(LocalTime.of(7, 1), 30);
        roomReservationBST.insert(LocalTime.of(6, 30), 30);
        roomReservationBST.insert(LocalTime.of(5, 32), 30);
        List<RoomReservationBST.Slots> availableSlots = roomReservationBST.findAvailableSlots(LocalTime.MIN, LocalTime.MAX);
        Assertions.assertEquals("[00:00 to 05:32, 06:02 to 06:30, 07:00 to 07:01, 07:31 to 08:30, 09:00 to 23:59:59.999999999]", availableSlots.toString());
        availableSlots = roomReservationBST.findAvailableSlots(LocalTime.of(5, 33), LocalTime.MAX);
        Assertions.assertEquals("[06:02 to 06:30, 07:00 to 07:01, 07:31 to 08:30, 09:00 to 23:59:59.999999999]", availableSlots.toString());
        roomReservationBST.delete(LocalTime.of(6, 2));
        availableSlots = roomReservationBST.findAvailableSlots(LocalTime.of(5, 33), LocalTime.MAX);
        Assertions.assertEquals("[06:02 to 06:30, 07:00 to 07:01, 07:31 to 08:30, 09:00 to 23:59:59.999999999]", availableSlots.toString());


    }

}
