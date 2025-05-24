package com.spring.restaurant.backend.validation;

import com.spring.restaurant.backend.entity.Reservation;
import com.spring.restaurant.backend.entity.RestaurantTable;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ReservationSufficientSeatCapacityAtSelectedTablesValidator implements ConstraintValidator<ReservationSufficientSeatCapacityAtSelectedTablesConstraint, Reservation> {

    @Override
    public void initialize(ReservationSufficientSeatCapacityAtSelectedTablesConstraint constraint) {
    }

    @Override
    public boolean isValid(Reservation reservation, ConstraintValidatorContext cxt) {


        if (null == reservation) {
            return false;
        }

        if (null == reservation.getNumberOfGuests()) {
            return false;
        }

        if (null == reservation.getRestaurantTables()) {
            return false;
        }

        if (reservation.getRestaurantTables().isEmpty()) {
            return false;
        }

        int numberOfGuests = reservation.getNumberOfGuests();
        int numberOfSeatsAtSelectedTables = 0;

        for (RestaurantTable t : reservation.getRestaurantTables()) {
            if (null == t.getSeatCount()) {
                return false;
            }
            numberOfSeatsAtSelectedTables += t.getSeatCount();
        }

        return (numberOfGuests <= numberOfSeatsAtSelectedTables);

    }
}
