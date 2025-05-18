package com.spring.restaurant.backend.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.restaurant.backend.basetest.TestData;
import com.spring.restaurant.backend.config.properties.SecurityProperties;
import com.spring.restaurant.backend.endpoint.dto.ReservationDto;
import com.spring.restaurant.backend.endpoint.dto.RestaurantTableDto;
import com.spring.restaurant.backend.endpoint.mapper.ReservationMapper;
import com.spring.restaurant.backend.endpoint.mapper.RestaurantTableMapper;
import com.spring.restaurant.backend.entity.Point;
import com.spring.restaurant.backend.entity.Reservation;
import com.spring.restaurant.backend.entity.RestaurantTable;
import com.spring.restaurant.backend.repository.ReservationRepository;
import com.spring.restaurant.backend.repository.RestaurantTableRepository;
import com.spring.restaurant.backend.security.JwtTokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class SimpleTableSuggestionStrategyTest implements TestData {


    /*
    // Test-Floorplan, created with/at: https://textik.com/#6819e29076187037


              |                                                           x-axis
         (0,0)|
       -------|--------------------------------------------------------------->
              |
              |
              |    +---------+                            +------------+
              |    |         |                            |            |
              |    | (1,1)   |                            |  (5,1)     |
              |    | seats:2 |                            |  seats:4   |
              |    +---------+                            |            |
              |                                           +------------+
              |
              |    +------------+
              |    | INACTIVE   |                          +---------+
              |    | (1,5)      |                          |         |
              |    | seats:4    |                          | (5,5)   |
              |    | INACTIVE   |                          | seats:2 |
              |    +------------+                          +---------+
              |
              |
              |
              |
              |                   +-------------------+
              |    +---------+    |                   |   +------------+
              |    |         |    |                   |   |            |
              |    | (1,9)   |    |    (3,9)          |   |  (5,9)     |
              |    | seats:2 |    |    seats:8        |   |  seats:4   |
              |    +---------+    |                   |   |            |
              |                   |                   |   +------------+
 y-axis       |                   +-------------------+
              v

     */

    @Autowired private MockMvc mockMvc;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private RestaurantTableRepository restaurantTableRepository;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ReservationMapper reservationMapper;
    @Autowired private JwtTokenizer jwtTokenizer;
    @Autowired private SecurityProperties securityProperties;
    @Autowired private RestaurantTableMapper restaurantTableMapper;
    private Reservation reservationTemplate;
    private RestaurantTable tableTemplate;
    // Table numbers are given according to the following convention:
    // t_<x-coordinate><ZERO><y-Coordinate><ZERO><seatCount>
    private RestaurantTable t_10102, t_10504, t_10902, t_30908, t_50104, t_50502, t_50904;



    @BeforeEach
    public void setup() {
        reservationRepository.deleteAll();
        restaurantTableRepository.deleteAll();

        tableTemplate = RestaurantTable.RestaurantTableBuilder.aTable()
            .withTableNum(100L)
            .withPosDescription("In SimpleTableSuggestionStrategyTest")
            .withSeatCount(TEST_TABLE_CAPACITY_FOR_VALID_RESERVATION)
            .withActive(true)
            .build();

        reservationTemplate = Reservation.ReservationBuilder.aReservation()
            .withGuestName(TEST_GUEST_FOR_VALID_RESERVATION)
            .withNumberOfGuests(TEST_GUEST_COUNT_FOR_VALID_RESERVATION)
            .withContactInformation(TEST_CONTACT_INFORMATION_FOR_VALID_RESERVATOIN)
            .withComment(TEST_COMMENT_FOR_VALID_RESERVATION)
            .withStartDateTime(TEST_START_DATE_TIME_FOR_VALID_RESERVATION)
            .withEndDateTime(TEST_END_DATE_TIME_FOR_VALID_RESERVATION)
            .withTables(null)
            .build();

        generateTables();
    }

    @Test
    public void Given2Guests_when_getSuggestion_Expect_Tables_OrderedBy_Origin_And_Size_ConsideringOnlyActiveTables_And_ExceptionWhenRunningOutOfSeats() throws Exception{

        Reservation reservation = buildReservation(reservationTemplate, 2);

        // First reservation
        List<RestaurantTable> suggestedTables;
        suggestedTables = getSuggestionForReservation(reservation);
        assertEquals(1, suggestedTables.size());
        assertEquals(t_10102.toString(), suggestedTables.get(0).toString());
        reservation.setRestaurantTables(new HashSet<>(suggestedTables));
        performPostForValidReservationAndAssertCreated(reservation);

        // Second reservation
        suggestedTables = getSuggestionForReservation(reservation);
        assertEquals(1, suggestedTables.size());
        assertEquals(t_50502.toString(), suggestedTables.get(0).toString());
        reservation.setRestaurantTables(new HashSet<>(suggestedTables));
        performPostForValidReservationAndAssertCreated(reservation);

        // Third reservation
        suggestedTables = getSuggestionForReservation(reservation);
        assertEquals(1, suggestedTables.size());
        assertEquals(t_10902.toString(), suggestedTables.get(0).toString());
        reservation.setRestaurantTables(new HashSet<>(suggestedTables));
        performPostForValidReservationAndAssertCreated(reservation);

        // 4th reservation
        suggestedTables = getSuggestionForReservation(reservation);
        assertEquals(1, suggestedTables.size());
        assertEquals(t_50104.toString(), suggestedTables.get(0).toString());
        reservation.setRestaurantTables(new HashSet<>(suggestedTables));
        performPostForValidReservationAndAssertCreated(reservation);

        // 5th reservation
        suggestedTables = getSuggestionForReservation(reservation);
        assertEquals(1, suggestedTables.size());
        assertEquals(t_50904.toString(), suggestedTables.get(0).toString());
        reservation.setRestaurantTables(new HashSet<>(suggestedTables));
        performPostForValidReservationAndAssertCreated(reservation);

        // 6th reservation
        suggestedTables = getSuggestionForReservation(reservation);
        assertEquals(1, suggestedTables.size());
        assertEquals(t_30908.toString(), suggestedTables.get(0).toString());
        reservation.setRestaurantTables(new HashSet<>(suggestedTables));
        performPostForValidReservationAndAssertCreated(reservation);

        // 7th reservation
        getSuggestionForReservationAndExpectException(reservation, "Not enough free seats available in the given time range");

    }

    @Test
    public void GivenTenGuests_Expect_SuggestionFor_TableFor8AndFor2Guests_WhichAreNextToEachOther() throws Exception{
        // 1. Erste Reservierung mit 10 Gästen
        Reservation r1 = buildReservation(reservationTemplate, 10);
        List<RestaurantTable> tablesR1 = getSuggestionForReservation(r1);
        assertEquals(2, tablesR1.size());
        assertEquals(t_30908.toString(), tablesR1.get(0).toString());
        assertEquals(t_10902.toString(), tablesR1.get(1).toString());
        r1.setRestaurantTables(new HashSet<>(tablesR1));
        performPostForValidReservationAndAssertCreated(r1);

        // 2. Zweite Reservierung mit anderen Tischen
        Reservation r2 = buildReservation(reservationTemplate, 10);
        List<RestaurantTable> tablesR2 = getSuggestionForReservation(r2);
        assertEquals(3, tablesR2.size());
        assertEquals(t_50104.toString(), tablesR2.get(0).toString());
        assertEquals(t_50904.toString(), tablesR2.get(1).toString());
        assertEquals(t_50502.toString(), tablesR2.get(2).toString());
        r2.setRestaurantTables(new HashSet<>(tablesR2));
        performPostForValidReservationAndAssertCreated(r2);

        // 3. Dritte Reservierung schlägt fehl
        Reservation r3 = buildReservation(reservationTemplate, 10);
        getSuggestionForReservationAndExpectException(r3, "Not enough free seats available in the given time range");
    }

    @Test
    public void GivenAMultiTableSelection_Expect_ClosestTablesAreFavouredOverSmallerOnes() throws Exception{
        // Reserve the tables for 2 near the table for 8:
        Reservation r1 = buildReservation(reservationTemplate, 2);
        HashSet<RestaurantTable> reservedTables = new HashSet<RestaurantTable>();
        reservedTables.add(t_10902);
        reservedTables.add(t_50502);
        r1.setRestaurantTables(reservedTables);
        performPostForValidReservationAndAssertCreated(r1);

        // Get suggestion for tables for 10 guests
        Reservation r2 = buildReservation(reservationTemplate, 10);
        List<RestaurantTable> tablesR2 = getSuggestionForReservation(r2);
        assertEquals(2, tablesR2.size());
        assertEquals(t_30908.toString(), tablesR2.get(0).toString());
        assertEquals(t_50904.toString(), tablesR2.get(1).toString());
        r2.setRestaurantTables(new HashSet<>(tablesR2));
        performPostForValidReservationAndAssertCreated(r2);
    }


    @Test
    public void GivenAllTablesReserved_When_UpdatingReservation_ExpectTablesOfReservationWhichIsToBeUpdated_AreIgnored() throws Exception{
        // Reserve the tables for 2 near the table for 8:
        Reservation r1 = buildReservation(reservationTemplate, 2);
        HashSet<RestaurantTable> reservedTables = new HashSet<RestaurantTable>();
        reservedTables.add(t_10902);
        reservedTables.add(t_50502);
        r1.setRestaurantTables(reservedTables);
        performPostForValidReservationAndAssertCreated(r1);

        // Get suggestion for tables for 10 guests
        Reservation r2 = buildReservation(reservationTemplate, 10);
        List<RestaurantTable> tablesR2 = getSuggestionForReservation(r2);
        assertEquals(2, tablesR2.size());
        assertEquals(t_30908.toString(), tablesR2.get(0).toString());
        assertEquals(t_50904.toString(), tablesR2.get(1).toString());
        r2.setRestaurantTables(new HashSet<>(tablesR2));

        // Now, there aren't enough tables for 8 guests anymore.
        // however, if we update the last created reservation for 10 guest,
        // to have only have 8 guests instead of 10, this should work.
        r2.setNumberOfGuests(8);
        tablesR2 = getSuggestionForReservation(r2);
        r2.setRestaurantTables(new HashSet<>(tablesR2));
        Reservation r3 = buildReservation(reservationTemplate, 8);
        List<RestaurantTable> tablesR3 = getSuggestionForReservation(r3);
        assertEquals(1, tablesR3.size());
        assertEquals(t_30908.toString(), tablesR3.get(0).toString());

        // Getting suggestion for 10 people again, will return the same result for 10 people as before:
        r2.setNumberOfGuests(10);
        tablesR2 = getSuggestionForReservation(r2);
        assertEquals(2, tablesR2.size());
        assertEquals(t_30908.toString(), tablesR2.get(0).toString());
        assertEquals(t_50904.toString(), tablesR2.get(1).toString());
    }

    private List<RestaurantTable> generateTables() {

        t_10102 = createAndGetTable(1, 1, 2, true);
        t_10504 = createAndGetTable(1, 5, 4, false);
        t_10902 = createAndGetTable(1, 9, 2, true);
        t_30908 = createAndGetTable(3, 9, 8, true);
        t_50104 = createAndGetTable(5, 1, 4, true);
        t_50502 = createAndGetTable(5, 5, 2, true);
        t_50904 = createAndGetTable(5, 9, 4, true);

        List<RestaurantTable> allTables = new ArrayList<RestaurantTable>();
        allTables.add(t_10102);
        allTables.add(t_10504);
        allTables.add(t_10902);
        allTables.add(t_30908);
        allTables.add(t_50104);
        allTables.add(t_50502);
        allTables.add(t_50904);

        return allTables;
    }

    private RestaurantTable createAndGetTable(int x, int y, int seatCount, boolean isActive) {

        RestaurantTable table = new RestaurantTable(tableTemplate);

        String tableName = x + "0" + y + "0" + seatCount;

        table.setSeatCount(seatCount);
        table.setTableNum(Long.parseLong(tableName));
        table.setCenterCoordinates(new Point(x, y));
        table.setActive(isActive);

        return restaurantTableRepository.save(table);
    }

    private List<RestaurantTable> getSuggestionForReservation(Reservation reservation) throws Exception{
        MockHttpServletResponse response = performFindTableSuggestionAndAssertOk(reservation);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<RestaurantTableDto> tablesDto = Arrays.asList(objectMapper.readValue(response.getContentAsString(), RestaurantTableDto[].class));
        return restaurantTableMapper.restaurantTableDtoToEntity(tablesDto);
    }

    private void getSuggestionForReservationAndExpectException(Reservation reservation, String exceptionMessage) throws Exception{
        MockHttpServletResponse response;
        response = performFindTableSuggestionAndAssertOk(reservation);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
        assertEquals("text/plain;charset=UTF-8", response.getContentType());
        assertTrue(response.getContentAsString().contains(exceptionMessage));
    }

    private MockHttpServletResponse performFindTableSuggestionAndAssertOk(Reservation reservation) throws Exception {

        String start = reservation.getStartDateTime().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_DATE_TIME);
        String end = reservation.getEndDateTime().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_DATE_TIME);
        Long idOfReservationToIgnoreAsString = 999L;
        Long numberOfGuests = Long.valueOf(reservation.getNumberOfGuests());

        MvcResult mvcResult = this.mockMvc.perform(get(TABLE_BASE_URI + "?numberOfGuests=" + numberOfGuests + "&idOfReservationToIgnore=" + idOfReservationToIgnoreAsString + "&startDateTime=" + start + "&endDateTime=" + end)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        return response;
    }

    private MockHttpServletResponse performPostForValidReservationAndAssertCreated(Reservation reservation) throws  Exception{

        ReservationDto reservationDto = reservationMapper.reservationToReservationDto(reservation);
        String body = objectMapper.writeValueAsString(reservationDto);

        MvcResult mvcResult = this.mockMvc.perform(post(RESERVATION_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        return response;
    }

    private Reservation buildReservation(Reservation reservation, int numberOfGuests) {
        return Reservation.ReservationBuilder.aReservation()
            .withGuestName(reservation.getGuestName())
            .withNumberOfGuests(numberOfGuests)
            .withContactInformation(TEST_CONTACT_INFORMATION_FOR_VALID_RESERVATOIN)
            .withComment(TEST_COMMENT_FOR_VALID_RESERVATION)
            .withStartDateTime(reservation.getStartDateTime())
            .withEndDateTime(reservation.getEndDateTime())
            .build();
    }

}
