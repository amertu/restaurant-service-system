package com.spring.restaurant.backend.validation;

import com.spring.restaurant.backend.entity.Reservation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

// https://www.baeldung.com/spring-mvc-custom-validator

public class ReservationSameDayValidator implements ConstraintValidator<ReservationSameDayConstraint, Reservation> {


    @Override
    public void initialize(ReservationSameDayConstraint constraint) {
    }

    @Override
    public boolean isValid(Reservation reservation, ConstraintValidatorContext cxt) {

        // https://www.tutorialspoint.com/javatime/javatime_localdatetime_truncatedto.htm

        LocalDateTime startDateTime = reservation.getStartDateTime();
        LocalDateTime endDateTime = reservation.getEndDateTime();


        if(null == startDateTime){
            return false;
        }

        if(null == endDateTime){
            return false;
        }


        LocalDateTime startDay = startDateTime.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime endDay = endDateTime.truncatedTo(ChronoUnit.DAYS);

        // The reservation must start and end on the same day
        return startDay.equals(endDay);

    }

}
