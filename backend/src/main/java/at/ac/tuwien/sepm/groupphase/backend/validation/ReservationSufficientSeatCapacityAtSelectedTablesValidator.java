package at.ac.tuwien.sepm.groupphase.backend.validation;

import at.ac.tuwien.sepm.groupphase.backend.entity.Reservation;
import at.ac.tuwien.sepm.groupphase.backend.entity.RestaurantTable;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ReservationSufficientSeatCapacityAtSelectedTablesValidator implements ConstraintValidator<ReservationSufficientSeatCapacityAtSelectedTablesConstraint, Reservation> {

    @Override
    public void initialize(ReservationSufficientSeatCapacityAtSelectedTablesConstraint constraint) {
    }

    @Override
    public boolean isValid(Reservation reservation, ConstraintValidatorContext cxt) {


        if(null == reservation){
            return false;
        }

        if(null == reservation.getNumberOfGuests()){
            return false;
        }

        if(null == reservation.getRestaurantTables()){
            return false;
        }

        if( reservation.getRestaurantTables().size() < 1){
            return false;
        }

        int numberOfGuests = reservation.getNumberOfGuests();
        int numberOfSeatsAtSelectedTables = 0;

        for(RestaurantTable t:reservation.getRestaurantTables()){
            if(null == t.getSeatCount()){
                return false;
            }
            numberOfSeatsAtSelectedTables += t.getSeatCount();
        }

        return (numberOfGuests <= numberOfSeatsAtSelectedTables);

    }
}
