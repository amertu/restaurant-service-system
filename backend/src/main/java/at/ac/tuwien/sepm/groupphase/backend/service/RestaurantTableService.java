package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.RestaurantTable;

import java.time.LocalDateTime;
import java.util.List;

public interface RestaurantTableService {
    /**
     * Find all tables ordered by ID
     *
     * @return ordered list of al tables
     */
    List<RestaurantTable> findAll();


    /**
     * Find a single table by id.
     *
     * @param id the id of the table
     * @return the table
     */
    RestaurantTable findOne(Long id);

    /**
     * Add a restaurantTable
     *
     * @param restaurantTable to add
     * @return added restaurantTable
     */
    RestaurantTable add(RestaurantTable restaurantTable);

    /**
     * modify a restaurantTable
     *
     * @param restaurantTable to modify
     * @return modified restaurantTable
     */
    RestaurantTable update(RestaurantTable restaurantTable);

    /**
     * delete a restaurantTable
     *
     * @param id of to delete
     */
    void delete(Long id);

    /**
     * update a restaurantTable's active field
     *
     * @param partialUpdate RestaurantTable containing fields to update (only id and active field are read)
     * @return the updated RestaurantTable (only active field updated)
     */
    RestaurantTable setActive(RestaurantTable partialUpdate);

    /**
     *  Returns a list of tables as suggestion where to place the guests.
     * @param numberOfGuests number of guests.
     * @param idOfReservationToIgnore is used to ignore the tables of a reservation which is currently going to be updated
     * @param startDateTime start time of the reservation.
     * @param endDateTime end time of the reservation.
     * @return a list of tables as suggestion where to place the guests.
     */
    List<RestaurantTable> findTableSuggestion(Integer numberOfGuests, Long idOfReservationToIgnore, LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * Clones a table which is identified by its id.
     * All properties should be simply copied, with two exceptions:
     * 1. database-generated id
     * 2. tableNum (which is simply next available tableNum which is greater than tableNum of supplied table)
     *
     * @param id the id of the table to clone
     * @return a table clone (all properties same as those of table to be cloned - exceptions: id and tableNum)
     */
    RestaurantTable clone(Long id);

    /**
     * update a restaurantTable's coordinates field
     *
     * @param partialUpdate RestaurantTable containing fields to update (only id and coordinates field are read)
     * @return the updated RestaurantTable (only coordinates field updated)
     */
    RestaurantTable setCoordinates(RestaurantTable partialUpdate);
}
