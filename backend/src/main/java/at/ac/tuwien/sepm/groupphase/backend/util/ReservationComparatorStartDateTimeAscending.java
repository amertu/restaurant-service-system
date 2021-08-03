package at.ac.tuwien.sepm.groupphase.backend.util;

import at.ac.tuwien.sepm.groupphase.backend.entity.Reservation;

import java.util.Comparator;

public class ReservationComparatorStartDateTimeAscending implements Comparator<Reservation> {

    @Override
    public int compare(Reservation b1, Reservation b2) {
        return b1.getStartDateTime().compareTo(b2.getStartDateTime());
    }
}
