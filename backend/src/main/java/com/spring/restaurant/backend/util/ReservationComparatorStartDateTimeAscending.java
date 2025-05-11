package com.spring.restaurant.backend.util;

import com.spring.restaurant.backend.entity.Reservation;

import java.util.Comparator;

public class ReservationComparatorStartDateTimeAscending implements Comparator<Reservation> {

    @Override
    public int compare(Reservation b1, Reservation b2) {
        return b1.getStartDateTime().compareTo(b2.getStartDateTime());
    }
}
