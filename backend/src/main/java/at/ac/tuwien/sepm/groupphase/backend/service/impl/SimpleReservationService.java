package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Reservation;
import at.ac.tuwien.sepm.groupphase.backend.entity.RestaurantTable;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ReservationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RestaurantTableRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ReservationService;
import at.ac.tuwien.sepm.groupphase.backend.util.ReservationComparatorStartDateTimeAscending;
import at.ac.tuwien.sepm.groupphase.backend.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SimpleReservationService implements ReservationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Validator validator;
    private final ReservationRepository reservationRepository;
    private final RestaurantTableRepository restaurantTableRepository;

    @Autowired
    public SimpleReservationService(Validator validator, ReservationRepository reservationRepository, RestaurantTableRepository restaurantTableRepository) {
        this.validator = validator;
        this.reservationRepository = reservationRepository;
        this.restaurantTableRepository = restaurantTableRepository;
    }

    @Override
    public List<Reservation> findAll() {
        LOGGER.debug("Find all reservations (ascending)");
        return reservationRepository.findAllByOrderByStartDateTimeAtAsc();
    }

    @Override
    public Reservation findOne(Long id) {
        LOGGER.debug("Find reservation with id {}", id);
        Optional<Reservation> reservationOptional = reservationRepository.findById(id);
        if (reservationOptional.isPresent()) {
            Reservation foundReservation = reservationOptional.get();
            LOGGER.debug("foundReservation: {}", foundReservation);
            return foundReservation;
        } else {
            throw new NotFoundException(String.format("Could not find reservation with id %s", id));
        }
    }

    @Override
    public List<Reservation> findByStartAndEndDateTime(LocalDateTime startDateTime, LocalDateTime endDateTime) {

        validator.validateStartAndEndDateTime(startDateTime, endDateTime);

        List<RestaurantTable> allRestaurantTables = restaurantTableRepository.findAll();

        // IMPORTANT: this is intentionally implemented as Set for the following reason:
        // If a reservation contains multiple tables, the reservation would be added multiple times to a list.
        Set<Reservation> conflictingReservations = new HashSet<Reservation>();


        for(RestaurantTable t:allRestaurantTables){
            conflictingReservations.addAll(reservationRepository.findConflictingReservations(t.getId(), startDateTime, endDateTime));
        }

        List<Reservation> listOfConflictingReservations = new ArrayList<>(conflictingReservations);

        // IMPORTANT: it isn't sufficient that the repository provides the reservations ordered by startDateTime
        // as the repository method is called multiple times and reservations are added in arbitrary order.
        listOfConflictingReservations.sort(new ReservationComparatorStartDateTimeAscending());

        return listOfConflictingReservations;
    }


    @Override
    public Reservation createReservation(Reservation reservation) {
        LOGGER.trace("createReservation(Reservation reservation)");
        LOGGER.debug(reservation.toString());

        Set<RestaurantTable> tablesWithFullInformation = getTablesWithFullInformation(reservation.getRestaurantTables());
        reservation.setRestaurantTables(tablesWithFullInformation);

        validator.validateNewReservation(reservation, reservationRepository, restaurantTableRepository);

        Reservation createdReservation = reservationRepository.save(reservation);

        return createdReservation;
    }

    @Override
    public void deleteReservationById(Long id) {
        LOGGER.debug("deleteReservationById(id={})", id);

        if(reservationRepository.existsById(id)){
            reservationRepository.deleteById(id);
        }else{
            throw new NotFoundException(String.format("Reserveration with id={} not found.", id));
        }
    }

    @Override
    public List<Reservation> search(String guestName, String startDateTime, String endDateTime, Long tableNum) {
        LocalDateTime start = null;
        LocalDateTime end = null;
        try {
            if (!StringUtils.isEmpty(startDateTime)) {
                start = LocalDateTime.parse(startDateTime, DateTimeFormatter.ISO_DATE_TIME);
            }
            if (!StringUtils.isEmpty(endDateTime)) {
                end = LocalDateTime.parse(endDateTime, DateTimeFormatter.ISO_DATE_TIME);
            }
        } catch(Exception e){
            throw new ValidationException("Failed to parse LocalDateTime.", e);
        }
        Set<Reservation> filteredReservationsSet = reservationRepository.filter(guestName, start, end, tableNum);
        return new ArrayList<>(filteredReservationsSet);
    }

    @Override
    @Transactional
    public Reservation updateReservation(Reservation reservation) {
        LOGGER.debug("updateReservation({})", reservation);
        // will throw an exception if reservation with given ID is not found:
        findOne(reservation.getId());

        Set<RestaurantTable> tablesWithFullInformation = getTablesWithFullInformation(reservation.getRestaurantTables());
        reservation.setRestaurantTables(tablesWithFullInformation);

        validator.validateModifiedReservation(reservation, reservationRepository, restaurantTableRepository);

        Reservation modifiedReservation =  reservationRepository.save(reservation);
        LOGGER.debug("Modified reservation: {}", modifiedReservation);
        return  modifiedReservation;
    }

    private Set<RestaurantTable> getTablesWithFullInformation(Set<RestaurantTable> tablesWithJustIdFilled){

        // TODO refactor
        if(null == tablesWithJustIdFilled){
            throw new ValidationException("The reservation must contain tables.");
        }

        Set<RestaurantTable> tablesWithFullInformation = new HashSet<RestaurantTable>();

        for(RestaurantTable t:tablesWithJustIdFilled){
            Optional<RestaurantTable> tableWithInformation = restaurantTableRepository.findById(t.getId());
            if(tableWithInformation.isPresent()){
                tablesWithFullInformation.add(tableWithInformation.get());
            }else{
                throw new ValidationException("Table with id=" + t.getId() + " not found.");
            }
        }

        return tablesWithFullInformation;
    }
}
