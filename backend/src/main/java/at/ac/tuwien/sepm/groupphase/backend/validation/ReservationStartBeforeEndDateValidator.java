package at.ac.tuwien.sepm.groupphase.backend.validation;

import at.ac.tuwien.sepm.groupphase.backend.entity.Reservation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;


public class ReservationStartBeforeEndDateValidator implements ConstraintValidator<ReservationStartBeforeEndDateConstraint, Reservation> {

    @Override
    public void initialize(ReservationStartBeforeEndDateConstraint constraint) {
    }

    @Override
    public boolean isValid(Reservation reservation, ConstraintValidatorContext cxt) {

        LocalDateTime startDateTime = reservation.getStartDateTime();
        LocalDateTime endDateTime = reservation.getEndDateTime();

        if(null == startDateTime){
            return false;
        }

        if(null == endDateTime){
            return false;
        }

        return endDateTime.isAfter(startDateTime);
    }
}
