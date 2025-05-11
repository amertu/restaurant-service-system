package com.spring.restaurant.backend.unittests;

import com.spring.restaurant.backend.entity.Reservation;
import com.spring.restaurant.backend.entity.RestaurantTable;
import com.spring.restaurant.backend.repository.ReservationRepository;
import com.spring.restaurant.backend.repository.RestaurantTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class ReservationRepositoryTest {


    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RestaurantTableRepository restaurantTableRepository;

    private LocalDateTime firstStartDate = LocalDateTime.of(2019, 11, 13, 12, 15, 0, 0);

    private Reservation reservationA;
    private Reservation reservationB;
    private RestaurantTable validRestaurantTable;


    @BeforeEach
    public void beforeEach() {
        reservationRepository.deleteAll();
        restaurantTableRepository.deleteAll();


        validRestaurantTable = RestaurantTable.RestaurantTableBuilder.aTable()
            .withTableNum(100L)
            .withPosDescription("Table Number 100")
            .withSeatCount(8)
            .withActive(true)
            .build();
        HashSet<RestaurantTable> validReservedRestaurantTables = new HashSet<RestaurantTable>();
        validReservedRestaurantTables.add(validRestaurantTable);
        restaurantTableRepository.save(validRestaurantTable);

        String STRINGS_FOR_A = ">>> A <<<";
        String STRINGS_FOR_B = ">>> B <<<";

        reservationA = Reservation.ReservationBuilder.aReservation()
            .withGuestName(STRINGS_FOR_A)
            .withNumberOfGuests(4)
            .withContactInformation(STRINGS_FOR_A)
            .withComment(STRINGS_FOR_A)
            .withTables(validReservedRestaurantTables)
            .build();

        reservationB = Reservation.ReservationBuilder.aReservation()
            .withGuestName(STRINGS_FOR_B)
            .withNumberOfGuests(4)
            .withContactInformation(STRINGS_FOR_B)
            .withComment(STRINGS_FOR_B)
            .withTables(validReservedRestaurantTables)
            .build();


    }

    private Reservation getReservationA_with(String startTime, String endTime){
        return getReservationFromTemplateWith(reservationA, startTime, endTime);
    }

    private Reservation getReservationB_with(String startTime, String endTime){
        return getReservationFromTemplateWith(reservationB, startTime, endTime);
    }

    private Reservation getReservationFromTemplateWith(Reservation template, String startTime, String endTime){
        LocalDate startDate = LocalDate.of(3999,5,1);
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);

        LocalDateTime startDateTime = LocalDateTime.of(startDate, start);
        LocalDateTime endDateTime = LocalDateTime.of(startDate, end);

        template.setStartDateTime(startDateTime);
        template.setEndDateTime(endDateTime);

        return template;
    }

    private RestaurantTable getValidTable() {
        return RestaurantTable.RestaurantTableBuilder.aTable()
            .withPosDescription("In ReservationEndpointTest")
            .withSeatCount(4)
            .withTableNum(100L)
            .withActive(true)
            .build();
    }

    private Reservation getValidReservationWithTables(Set<RestaurantTable> tables) {
        return Reservation.ReservationBuilder.aReservation()
            .withGuestName("Jay Jay O'Connor")
            .withTables(tables)
            .withStartDateTime(LocalDateTime.of(2021, 7, 15, 15, 0))
            .withEndDateTime(LocalDateTime.of(2021, 7, 15, 17, 0))
            .build();
    }

    private Reservation constructReservation(String guestName, Set<RestaurantTable> tableSet, LocalDateTime start, LocalDateTime end) {
        return Reservation.ReservationBuilder.aReservation()
            .withGuestName(guestName)
            .withTables(tableSet)
            .withStartDateTime(start)
            .withEndDateTime(end)
            .build();
    }

    public void givenTwoReservations_whenAllSearchParamsNull_returnsAllReservations() {
        Set<RestaurantTable> tables = new HashSet<>();
        RestaurantTable table = getValidTable();
        tables.add(table);
        restaurantTableRepository.save(table);
        Reservation res_1 = getValidReservationWithTables(tables);
        Reservation res_2 = getValidReservationWithTables(tables);

        reservationRepository.save(res_1);
        reservationRepository.save(res_2);

        assertEquals(2, reservationRepository.filter(null, null, null, null).size());
    }

    public void givenTwoReservations_searchByExactName_returnsCorrectReservation() {

    }



}
