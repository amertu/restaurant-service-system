package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {


    final String queryForConflictingReservationsWithExclusiveEndDates = "SELECT * FROM reservation INNER JOIN contains_tables ON reservation.id = contains_tables.reservation_id " +
        "where restaurant_table_id = :tableId " +
        "and (:endDateTime > reservation.start_date_time and reservation.end_date_time > :startDateTime) ORDER BY reservation.start_date_time ASC";

    /**
     * Returns all conflicting reservations for the given table within the given time range.
     * EndDateTime is considered exclusive. So it is possible to have a reservation ending at 14:00
     * and another one starting at 14:00. This situation will not be reported as a reservation conflict.
     *
     * @param tableId ID of table to be checked for conflicting reservations
     * @param startDateTime start of range to check for conflicts
     * @param endDateTime end of range to check for conflicts (exclusive)
     * @return
     */
    @Query(value = queryForConflictingReservationsWithExclusiveEndDates, nativeQuery = true)
    Set<Reservation> findConflictingReservations(@Param("tableId") Long tableId, @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);


    /**
     * Find all reservations ordered by start date time (ascending).
     *
     * @return ordered list of all reservations ordered by start date time (ascending).
     */
    @Query(value = "SELECT * FROM reservation ORDER BY start_date_time ASC", nativeQuery = true)
    List<Reservation> findAllByOrderByStartDateTimeAtAsc(); // TODO check how make this work without a native query

    /**
     * Filters saved reservations by the given parameters.
     * @param guestName     the name of the guest that made the reservation(s) to find.
     * @param startDateTime the earliest Date and Time of the reservation(s) to find. (exclusive)
     * @param endDateTime   the latest Date and Time of the reservations(s) to find. (exclusive)
     * @param tableNum      the table number of a table shall be part of the reservation(s) to find.
     * @return a Set of reservations that fit the query.
     */
    @Query(value = "SELECT * FROM reservation INNER JOIN contains_tables ON reservation.id = contains_tables.reservation_id " +
        "WHERE (UPPER(reservation.guest_name) LIKE CONCAT('%', UPPER(:guestName), '%')) " +
        "AND (:tableNum IS NULL OR restaurant_table_Id IN " +
        "(SELECT id FROM restaurant_table WHERE table_num = :tableNum)) " +
        "AND (:startDateTime IS NULL OR reservation.start_date_time < :endDateTime) " +
        "AND (:endDateTime IS NULL OR reservation.end_date_time > :startDateTime) " +
        "ORDER BY reservation.start_date_time ASC", nativeQuery = true)
    Set<Reservation> filter(@Param("guestName") String guestName,
                            @Param("startDateTime") LocalDateTime startDateTime,
                            @Param("endDateTime") LocalDateTime endDateTime,
                            @Param("tableNum") Long tableNum);
}