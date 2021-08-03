package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
    /**
     * Get number of reservations for the specified table which are in future or currently active.
     * @param id ID of table
     * @return number of rows
     */
    @Query(value = "SELECT COUNT(*) FROM (SELECT * FROM CONTAINS_TABLES WHERE restaurant_table_id=:id) c JOIN RESERVATION r ON c.reservation_id = r.id AND END_DATE_TIME >= now();", nativeQuery = true)
    Long findReservationsForTableWithIdIEndingInFuture(@Param("id") Long id);

    @Query(value = "SELECT reservation_id FROM (SELECT * FROM CONTAINS_TABLES WHERE restaurant_table_id=:id) c JOIN RESERVATION r ON c.reservation_id = r.id", nativeQuery = true)
    List<Long> findReservationIdsOfReservationsInFutureForTableWithId(@Param("id") Long id);

    /**
     * Is intended to be used for finding free and active tables.
     * @param tableNumbersOfReservedTables the table numbers of the already reserved tables.
     * @returns free and active tables.
     */
   @Query(value = "SELECT * from RESTAURANT_TABLE where table_num NOT IN (:tableNumbersOfReservedTables) AND active = TRUE", nativeQuery = true)
    List<RestaurantTable> findActiveTablesNotIn(@Param("tableNumbersOfReservedTables") List<Long> tableNumbersOfReservedTables);

    @Query(value= "SELECT COUNT(*) FROM RESTAURANT_TABLE t WHERE t.table_num = :table_num", nativeQuery = true)
    Long findNumberOfTablesWithTableNum(@Param("table_num") Long tableNum);
}
