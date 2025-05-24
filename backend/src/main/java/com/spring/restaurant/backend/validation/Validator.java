package com.spring.restaurant.backend.validation;

import com.spring.restaurant.backend.entity.Reservation;
import com.spring.restaurant.backend.entity.RestaurantTable;
import com.spring.restaurant.backend.exception.NotFoundException;
import com.spring.restaurant.backend.exception.ValidationException;
import com.spring.restaurant.backend.repository.ReservationRepository;
import com.spring.restaurant.backend.repository.RestaurantTableRepository;
import com.spring.restaurant.backend.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.*;

// https://www.baeldung.com/javax-validation
@Component
public class Validator {

    private final String VALIDATION_ERROR_PREFIX = "Validation error occured:\n";
    private final String RESERVATION_DOES_NOT_CONTAIN_TABLES = "The reservation does not contain any tables.\n";
    private  String TABLE_DOES_NOT_EXIST_MESSAGE = "Table {} doesn't exist.";
    private  String TABLE_IS_NOT_AVAILABLE_MESSAGE = "Table {} is not available.";


    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    jakarta.validation.Validator validator = factory.getValidator();
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    private void performCommonReservationValidation(Reservation reservation, ReservationRepository reservationRepository, RestaurantTableRepository restaurantTableRepository){
        if (null == reservation.getRestaurantTables() || reservation.getRestaurantTables().isEmpty()){
            LOGGER.debug("null == reservation.getRestaurantTables() || reservation.getRestaurantTables().isEmpty()");
            logErrorAndThrow(new ValidationException(RESERVATION_DOES_NOT_CONTAIN_TABLES));
        }

        LOGGER.debug("Number of tables: " + reservation.getRestaurantTables().size());

        for(RestaurantTable t: reservation.getRestaurantTables()){
            LOGGER.debug("Table: " + t);
            try{
                // TODO maybe it is better to have a restaurantTabeRepository-method providing only 'enabled' tables
                checkIfTableExistsAndIsEnabled(restaurantTableRepository, t.getId());
            }catch(NotFoundException ex){
                LOGGER.debug("Table id is: " + t.getId());
                logErrorAndThrow( new ValidationException((VALIDATION_ERROR_PREFIX + "Table number '" + t.getId() + "' doesn't exist")));
            }
        }

        Set<ConstraintViolation<Reservation>> violations = validator.validate(reservation);

        if (!violations.isEmpty()) {
            StringBuilder violationLog = new StringBuilder();
            for (ConstraintViolation<Reservation> violation : violations) {
                violationLog.append(" ").append(violation.getMessage()).append("\n");
            }

            logErrorAndThrow(new ValidationException( VALIDATION_ERROR_PREFIX + violationLog));
        }
    }

    private List<Reservation> findConflictingReservations(Reservation reservation, ReservationRepository reservationRepository){
        // IMPORTANT: this is intentionally implemented as Set for the following reason:
        // If a reservation contains multiple tables, the reservation would be added multiple times to a list.
        Set<Reservation> conflictingReservations = new HashSet<>();
        Set<RestaurantTable> tablesInReservation = reservation.getRestaurantTables();
        for(RestaurantTable restaurantTable : tablesInReservation){
            Set<Reservation> conflictingReservationsForTable = reservationRepository.findConflictingReservations(restaurantTable.getId(), reservation.getStartDateTime(), reservation.getEndDateTime());
            conflictingReservations.addAll(conflictingReservationsForTable);
        }
        return new ArrayList<Reservation>(conflictingReservations);
    }

    public void validateNewReservation(Reservation reservation, ReservationRepository reservationRepository, RestaurantTableRepository restaurantTableRepository) throws ValidationException {

        LOGGER.debug("validateNewReservation: {}", reservation);

        performCommonReservationValidation(reservation, reservationRepository, restaurantTableRepository);

        //TODO this seems to be the easiest solution to implement it. But maybe (also) another JDBC query might need to be provided, checking all tables.
        List<Reservation> conflictingReservations = findConflictingReservations(reservation, reservationRepository);

        if (!conflictingReservations.isEmpty()) {
            String conflictingReservationsString = getUserFriendlyRepresentationFor(conflictingReservations);
            logErrorAndThrow(new ValidationException(VALIDATION_ERROR_PREFIX + "The following conflicting reservations were detected: \n" + conflictingReservationsString));
        }

    }


    public void validateModifiedReservation(Reservation reservation, ReservationRepository reservationRepository, RestaurantTableRepository restaurantTableRepository) {

        LOGGER.debug("validateModifiedReservation: {}", reservation);


        performCommonReservationValidation(reservation, reservationRepository, restaurantTableRepository);

        //TODO this seems to be the easiest solution to implement it. But maybe (also) another JDBC query might need to be provided, checking all tables.
        List<Reservation> allConflictingReservations = findConflictingReservations(reservation, reservationRepository);

        List<Reservation> conflictingReservationsExceptIgnoredReservation = getConflictingReservationsWithoutId(reservation.getId(), allConflictingReservations);

        if (!conflictingReservationsExceptIgnoredReservation.isEmpty()) {
            String conflictingReservationsString = getUserFriendlyRepresentationFor(conflictingReservationsExceptIgnoredReservation);
            logErrorAndThrow(new ValidationException(VALIDATION_ERROR_PREFIX + "The following conflicting reservations were detected: \n" + conflictingReservationsString));
        }
    }

    public void validateNumberOfGuests(Integer numberOfGuests) {
        if (numberOfGuests == null || numberOfGuests < 1) {
            String message = (numberOfGuests == null)
                ? "Number of guests must not be null."
                : "Number of guests must be >= 1.";
            logErrorAndThrow(new ValidationException(message));
        }
    }




    private String getUserFriendlyRepresentationFor(List<Reservation> conflictingReservations){

        StringBuilder result = new StringBuilder();

        for(int i=0; i<conflictingReservations.size(); i++){
            result.append("\n");
            result.append((i + 1)).append(".");
            result.append(conflictingReservations.get(i).toUserFriendlyString());
        }

        return result.toString();
    }


    private List<Reservation> getConflictingReservationsWithoutId(Long idOfReservationToBeIgnored, List<Reservation> allConflictingReservations){
        List<Reservation> conflictingReservationsExceptIngoredReservation = new ArrayList<Reservation>();
        for(Reservation r:allConflictingReservations){
            if(!Objects.equals(r.getId(), idOfReservationToBeIgnored)){
                conflictingReservationsExceptIngoredReservation.add(r);
            }
        }
        return conflictingReservationsExceptIngoredReservation;
    }

    // TODO refactor: LOGGER as an argument and method under: util.LogUtils
    private void logErrorAndThrow(RuntimeException e) throws RuntimeException {
        LOGGER.error(e.getMessage());
        throw e;
    }

    private void checkIfTableExistsAndIsEnabled(RestaurantTableRepository restaurantTableRepository, Long tableID) throws NotFoundException, ValidationException{
        LOGGER.debug("tableID: " + tableID);
        Optional<RestaurantTable> existingTable = restaurantTableRepository.findById(tableID);
        LOGGER.debug("Found table is: " + existingTable);
        if(existingTable.isPresent()){
            LOGGER.debug("Table is present: " + existingTable.get());
            if(!existingTable.get().getActive()){
                LOGGER.debug("Table is inactive.");
                logErrorAndThrow( new ValidationException((VALIDATION_ERROR_PREFIX + "Table " + existingTable.get().getTableNum() + " is not available.")));
            }
        }else{
            throw new NotFoundException("Table with ID " + tableID + " not found.");
        }
    }

    public void validateStartAndEndDateTime(LocalDateTime startDateTime, LocalDateTime endDateTime) {

        if(null == startDateTime){
            logErrorAndThrow(new ValidationException("The start time must not be null"));
        }

        if(null == endDateTime){
            logErrorAndThrow(new ValidationException("The end time must not be null"));
        }

        assert endDateTime != null;
        if (!endDateTime.isAfter(startDateTime)) {
            logErrorAndThrow(new ValidationException("The end time must be after the start time."));
        }
    }

    public void validateSufficientSeatsFor(Integer numberOfGuests, List<RestaurantTable> freeTables) {

        int totalNumberOfFreeSeats = Util.calculateTotalNumberOfSeats(freeTables);

        if( numberOfGuests > totalNumberOfFreeSeats){
            logErrorAndThrow(new ValidationException("Not enough free seats available in the given time range.\n" +
                "Only " + totalNumberOfFreeSeats + " free seats available."));
        }

    }
}
