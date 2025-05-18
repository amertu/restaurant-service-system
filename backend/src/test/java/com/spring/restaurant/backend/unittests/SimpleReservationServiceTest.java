package com.spring.restaurant.backend.unittests;

import com.spring.restaurant.backend.entity.Reservation;
import com.spring.restaurant.backend.entity.RestaurantTable;
import com.spring.restaurant.backend.exception.ValidationException;
import com.spring.restaurant.backend.repository.ReservationRepository;
import com.spring.restaurant.backend.repository.RestaurantTableRepository;
import com.spring.restaurant.backend.service.impl.SimpleReservationService;
import com.spring.restaurant.backend.validation.Validator;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ActiveProfiles("test")
public class SimpleReservationServiceTest {


    private SimpleReservationService reservationService;


    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RestaurantTableRepository restaurantTableRepository;

    private Reservation reservationA;
    private Reservation reservationB;
    private RestaurantTable validRestaurantTable;


    @BeforeEach
    public void setup() {
        restaurantTableRepository.deleteAll();
        reservationRepository.deleteAll();

        Validator validator = new Validator();

        reservationService = new SimpleReservationService(validator, reservationRepository, restaurantTableRepository);

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



    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // TESTING: Table 1.  Allen's thirteen basic relations
    // https://www.ics.uci.edu/~alspaugh/cls/shr/allen.html
    // referenced by: https://salman-w.blogspot.com/2012/06/sql-query-overlapping-date-ranges.html


    @Test
    public void p___given_A_precedes_B(){

        Reservation a = getReservationA_with("12:00", "13:59");
        Reservation b = getReservationB_with("14:00", "15:00");

        assertReservationCreatedSuccessfully(a);
        assertReservationCreatedSuccessfully(b);

    }

    @Test
    public void m___given_A_meets_B(){

        Reservation a = getReservationA_with("12:00", "14:00");
        Reservation b = getReservationB_with("14:00", "15:00");

        assertReservationCreatedSuccessfully(a);
        assertReservationCreatedSuccessfully(b);

    }

    @Test
    public void o___given_A_overlaps_B(){

        Reservation a = getReservationA_with("13:59", "14:01");
        Reservation b = getReservationB_with("14:00", "15:00");

        assertReservationCreatedSuccessfully(a);
        assertReservationInConflictWith(b, a);

    }

    @Test
    public void F___given_A_finishedBy_B(){

        Reservation a = getReservationA_with("12:00", "15:00");
        Reservation b = getReservationB_with("14:00", "15:00");

        assertReservationCreatedSuccessfully(a);
        assertReservationInConflictWith(b, a);

    }

    @Test
    public void D___given_A_contains_B(){

        Reservation a = getReservationA_with("12:00", "16:00");
        Reservation b = getReservationB_with("14:00", "15:00");

        assertReservationCreatedSuccessfully(a);
        assertReservationInConflictWith(b, a);

    }

    @Test
    public void s___given_A_starts_B(){

        Reservation a = getReservationA_with("12:00", "14:00");
        Reservation b = getReservationB_with("12:00", "15:00");

        assertReservationCreatedSuccessfully(a);
        assertReservationInConflictWith(b, a);

    }

    @Test
    public void e___given_A_equals_B(){

        Reservation a = getReservationA_with("12:00", "14:00");
        Reservation b = getReservationB_with("12:00", "14:00");

        assertReservationCreatedSuccessfully(a);
        assertReservationInConflictWith(b, a);

    }

    @Test
    public void S___given_A_startedBy_B(){

        Reservation a = getReservationA_with("12:00", "14:00");
        Reservation b = getReservationB_with("12:00", "13:00");

        assertReservationCreatedSuccessfully(a);
        assertReservationInConflictWith(b, a);

    }

    @Test
    public void d___given_A_during_B(){

        Reservation a = getReservationA_with("12:00", "14:00");
        Reservation b = getReservationB_with("11:00", "15:00");

        assertReservationCreatedSuccessfully(a);
        assertReservationInConflictWith(b, a);

    }


    @Test
    public void f___given_A_finishes_B(){

        Reservation a = getReservationA_with("13:00", "14:00");
        Reservation b = getReservationB_with("12:00", "14:00");

        assertReservationCreatedSuccessfully(a);
        assertReservationInConflictWith(b, a);

    }

    @Test
    public void O___given_A_overlappedBy_B(){

        Reservation a = getReservationA_with("13:59", "14:01");
        Reservation b = getReservationB_with("12:00", "14:00");

        assertReservationCreatedSuccessfully(a);
        assertReservationInConflictWith(b, a);

    }

    @Test
    public void M___given_A_metBy_B(){

        Reservation a = getReservationA_with("14:00", "15:00");
        Reservation b = getReservationB_with("12:00", "14:00");

        assertReservationCreatedSuccessfully(a);
        assertReservationCreatedSuccessfully(b);

    }

    @Test
    public void P___given_A_preceedBy_B(){

        Reservation a = getReservationA_with("13:00", "14:00");
        Reservation b = getReservationB_with("12:00", "12:59");

        assertReservationCreatedSuccessfully(a);
        assertReservationCreatedSuccessfully(b);

    }

    private void assertReservationCreatedSuccessfully(Reservation reservation){
        Reservation createdReservation = reservationService.createReservation(reservation);
        reservation.setId(createdReservation.getId());
        assertEquals(reservation, createdReservation);
    }

    private void assertReservationInConflictWith(Reservation reservationToCreate, Reservation conflictingReservation){

        try{
            Reservation createdReservation = reservationService.createReservation(reservationToCreate);
            Assert.fail("This line should not be reached. Exception expected!");
        }catch (ValidationException e){
            assertTrue(e.getMessage().contains(conflictingReservation.toUserFriendlyString()));
        }

    }




    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
}
