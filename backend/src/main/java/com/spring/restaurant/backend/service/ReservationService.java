package com.spring.restaurant.backend.service;

import com.spring.restaurant.backend.entity.Reservation;
import com.spring.restaurant.backend.exception.CurrentlyUnavailableException;
import com.spring.restaurant.backend.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationService {


    /**
     * Find all reservations by start date ascending.
     *
     * @return ordered list of all reservations (ascending by start date time)
     */
    List<Reservation> findAll();

    // TODO add NotFoundException
    /**
     * Find a single reservation by ID
     * @param id the ID of the reservation to find.
     * @return the reservation.
     */
    Reservation findOne(Long id);

    /**
     * Finds reservations in the time interval [startDateTime, endDateTime).
     * This means that the interval is open on side of endDateTime.
     * @param startDateTime the start date time of the time interval (inclusive)
     * @param endDateTime the end date of the of the time interval (exclusive)
     * @return reservations in the given time interval. Might be an empty list.
     * @throws ValidationException  will be thrown if the validation of the time range fails.
     * @throws CurrentlyUnavailableException will be thrown if something goes wrong during the data processing.
     */
    List<Reservation> findByStartAndEndDateTime(LocalDateTime startDateTime, LocalDateTime endDateTime);



    /**
     * Creates the given reservation.
     *
     * @param reservation the reservation to create.
     * @return the created reservation.
     * @throws ValidationException           will be thrown if the validation of the reservation fails.
     * @throws CurrentlyUnavailableException will be thrown if something goes wrong during the data processing.
     */
    Reservation createReservation(Reservation reservation);


    // TODO add NotFoundException
    /**
     * Deletes the reservation with the given id.
     * @param id the id of the reservation, which is to be deleted.
     * @throws CurrentlyUnavailableException will be thrown if something goes wrong during the data processing.
     */
    void deleteReservationById(Long id);

    /**
     * Filters saved reservations by the given parameters.
     * @param guestName     the name of the guest that made the reservation(s) to find.
     * @param startDateTime the earliest Date and Time of the reservation(s) to find. (exclusive)
     * @param endDateTime   the latest Date and Time of the reservations(s) to find. (exclusive)
     * @param tableNum      the table number of a table that shall be part of the reservation(s) to find.
     * @return a list of reservations that fit the the query (all, if every parameter is null)
     */
    List<Reservation> search(String guestName, String startDateTime, String endDateTime, Long tableNum);

    // TODO add NotFoundException
    /**
     * Modifies a reservation.
     *
     * @param reservation to modify
     * @return modified reservation
     */
    Reservation updateReservation(Reservation reservation);
}




