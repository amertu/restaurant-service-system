package com.spring.restaurant.backend.service.impl;

import com.spring.restaurant.backend.entity.Point;
import com.spring.restaurant.backend.entity.Reservation;
import com.spring.restaurant.backend.entity.RestaurantTable;
import com.spring.restaurant.backend.exception.NotFoundException;
import com.spring.restaurant.backend.repository.RestaurantTableRepository;
import com.spring.restaurant.backend.service.RestaurantTableService;
import com.spring.restaurant.backend.util.TableSuggestionStrategy;
import com.spring.restaurant.backend.util.impl.SimpleTableSuggestionStrategy;
import com.spring.restaurant.backend.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class SimpleRestaurantTableService implements RestaurantTableService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final RestaurantTableRepository restaurantTableRepository;
    private final Validator validator;

    @Autowired
    private SimpleReservationService reservationService;

    public SimpleRestaurantTableService(RestaurantTableRepository restaurantTableRepository,  Validator validator) {
        this.restaurantTableRepository = restaurantTableRepository;
        this.validator = validator;
    }

    @Override
    public List<RestaurantTable> findAll() {
        LOGGER.debug("Find all tables");
        return restaurantTableRepository.findAll();
    }

    @Override
    public RestaurantTable findOne(Long id) {
        LOGGER.debug("Find table with id {}", id);
        Optional<RestaurantTable> tableOptional = restaurantTableRepository.findById(id);
        if (tableOptional.isPresent()) {
            return tableOptional.get();
        }
        else throw new NotFoundException(String.format("Could not find table with id %s", id));
    }

    @Override
    public RestaurantTable add(RestaurantTable restaurantTable) {
        LOGGER.debug("Save table");
        return restaurantTableRepository.save(restaurantTable);
    }

    @Override
    @Transactional
    public RestaurantTable update(RestaurantTable updatedTable) {
        Long id = updatedTable.getId();
        LOGGER.debug("update table with id {}", id);
        RestaurantTable tableFound =  restaurantTableRepository.getOne(id);
        tableFound.setTableNum(updatedTable.getTableNum());
        tableFound.setSeatCount(updatedTable.getSeatCount());
        tableFound.setPosDescription(updatedTable.getPosDescription());
        tableFound.setActive(updatedTable.getActive());
        tableFound.setCenterCoordinates(updatedTable.getCenterCoordinates());
        return restaurantTableRepository.save(tableFound);
    }

    @Override
    public void delete(Long id) {
        LOGGER.debug("delete table with id {}", id);
        //TODO: decide if there should be another two delete methods: "safe" and regular delete
        /*Long reservationsInFutureUsingTable = restaurantTableRepository.findReservationsForTableWithIdIEndingInFuture(id);
        if (restaurantTableRepository.findReservationsForTableWithIdIEndingInFuture(id) >= 1L)
            throw new DependentDataException("Delete failed: There are still bookings for this table in the future!");*/
        restaurantTableRepository.deleteById(id);
    }

    @Override
    @Transactional
    public RestaurantTable setActive(RestaurantTable partialUpdate) {
        Long id = partialUpdate.getId();
        Boolean active = partialUpdate.getActive();
        LOGGER.debug("set active of table with id {} to {}", id, active);
        RestaurantTable tableFound =  restaurantTableRepository.getOne(id);
        if (!partialUpdate.getActive() && restaurantTableRepository.findReservationsForTableWithIdIEndingInFuture(id) >= 1L) {
            //TODO: decide if there should be two deactivate methods: "safe" and regular deactivate (latter deleting all assigned tables)
            //throw new DependentDataException("Cannot deactivate table: There are still bookings for this table in the future!");
            List<Long> reservationIds = restaurantTableRepository.findReservationIdsOfReservationsInFutureForTableWithId(id);
            if (!reservationIds.isEmpty()) {
                for(Long resId : reservationIds) reservationService.deleteReservationById(resId);
            }
        }
        tableFound.setActive(active);
        return restaurantTableRepository.save(tableFound);
    }

    @Override
    public List<RestaurantTable> findTableSuggestion(Integer numberOfGuests, Long idOfReservationToIgnore,  LocalDateTime startDateTime, LocalDateTime endDateTime) {

        validator.validateNumberOfGuests(numberOfGuests);
        validator.validateStartAndEndDateTime(startDateTime, endDateTime);

        List<RestaurantTable> freeTables = getFreeTables(idOfReservationToIgnore, startDateTime, endDateTime);

        validator.validateSufficientSeatsFor(numberOfGuests, freeTables);

        // TODO: maybe allow the user to select a strategy
        TableSuggestionStrategy strategy = new SimpleTableSuggestionStrategy();
        return strategy.getSuggestedTables(numberOfGuests, freeTables);

    }

    private List<RestaurantTable> getFreeTables(Long idOfReservationToIgnore, LocalDateTime startDateTime, LocalDateTime endDateTime){
        List<Reservation> conflictReservations = reservationService.findByStartAndEndDateTime(startDateTime, endDateTime);
        Set<Long> tableNumbersOfReservedTables = new HashSet<Long>();


        for(Reservation conflictReservation: conflictReservations){

            if( null != idOfReservationToIgnore && Objects.equals(conflictReservation.getId(), idOfReservationToIgnore)){
                continue;
            }

            for(RestaurantTable table: conflictReservation.getRestaurantTables()){
                tableNumbersOfReservedTables.add(table.getTableNum());
            }
        }

        return restaurantTableRepository.findAvailableTables(new ArrayList<Long>(tableNumbersOfReservedTables));

    }

    @Override
    public RestaurantTable clone(Long id) {
        Optional<RestaurantTable> tableOptional = restaurantTableRepository.findById(id);
        if (tableOptional.isPresent()) {
            RestaurantTable tableFound = tableOptional.get();
            RestaurantTable tableClone = RestaurantTable.RestaurantTableBuilder.aTable()
                .withActive(tableFound.getActive())
                .withPosDescription(tableFound.getPosDescription())
                .withSeatCount(tableFound.getSeatCount())
                .build();

            for (long tableNum = tableFound.getTableNum() + 1; tableNum < Long.MAX_VALUE; tableNum++) {
                if (restaurantTableRepository.findNumberOfTablesWithTableNum(tableNum) == 0) {
                    tableClone.setTableNum(tableNum);
                    break;
                }
            }
            //actually I could throw a custom exception if there was no free tableNum available, but this scenario is practically impossible
            return restaurantTableRepository.save(tableClone);
        }
        else throw new NotFoundException(String.format("Clone failed: No table with supplied ID %s was found!", id));
    }

    @Override
    @Transactional
    public RestaurantTable setCoordinates(RestaurantTable partialUpdate) {
        Long id = partialUpdate.getId();
        Point coordinates = partialUpdate.getCenterCoordinates();
        LOGGER.debug("set coordinates of table with id {} to {}", id , coordinates);
        RestaurantTable tableFound =  restaurantTableRepository.getOne(id);
        tableFound.setCenterCoordinates(coordinates);
        return restaurantTableRepository.save(tableFound);
    }


}
