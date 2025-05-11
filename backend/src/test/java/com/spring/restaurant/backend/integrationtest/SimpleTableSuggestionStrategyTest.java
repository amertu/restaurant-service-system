package com.spring.restaurant.backend.integrationtest;

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
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

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


    // Table numbers are given according to the following convention:
    // t_<x-coordinate><ZERO><y-Coordinate><ZERO><seatCount>
    private RestaurantTable t_10102, t_10504, t_10902, t_30908, t_50104, t_50502, t_50904;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RestaurantTableRepository restaurantTableRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;


    private RestaurantTable tableTemplate;
    private Set<RestaurantTable> restaurantTables;

    @Autowired
    RestaurantTableMapper restaurantTableMapper;

    private Reservation reservationTemplate;

    private List<RestaurantTable> allRestaurantTables;


    @BeforeEach
    public void beforeEach() {
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

        allRestaurantTables = generateTables();


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



    @Test
    public void Given2Guests_when_getSuggestion_Expect_Tables_OrderedBy_Origin_And_Size_ConsideringOnlyActiveTables_And_ExceptionWhenRunningOutOfSeats() throws Exception{

        Reservation reservation = getReservationFromTemplateWithGuestCount(2);

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

        Reservation reservation = getReservationFromTemplateWithGuestCount(10);

        // First reservation
        List<RestaurantTable> suggestedTables;
        suggestedTables = getSuggestionForReservation(reservation);

        assertEquals(2, suggestedTables.size());
        assertEquals(t_30908.toString(), suggestedTables.get(0).toString());
        assertEquals(t_10902.toString(), suggestedTables.get(1).toString());

        reservation.setRestaurantTables(new HashSet<>(suggestedTables));
        performPostForValidReservationAndAssertCreated(reservation);

        // Second reservation
        suggestedTables = getSuggestionForReservation(reservation);

        assertEquals(3, suggestedTables.size());
        assertEquals(t_50104.toString(), suggestedTables.get(0).toString());
        assertEquals(t_50904.toString(), suggestedTables.get(1).toString());
        assertEquals(t_50502.toString(), suggestedTables.get(2).toString());

        reservation.setRestaurantTables(new HashSet<>(suggestedTables));
        performPostForValidReservationAndAssertCreated(reservation);

        // Third reservation
        getSuggestionForReservationAndExpectException(reservation, "Not enough free seats available in the given time range");
    }

    @Test
    public void GivenAMultiTableSelection_Expect_ClosestTablesAreFavouredOverSmallerOnes() throws Exception{

        // Reserve the tables for 2 near the table for 8:
        Reservation reservation = getReservationFromTemplateWithGuestCount(2);
        HashSet<RestaurantTable> reservedTables = new HashSet<RestaurantTable>();
        reservedTables.add(t_10902);
        reservedTables.add(t_50502);
        reservation.setRestaurantTables(reservedTables);
        performPostForValidReservationAndAssertCreated(reservation);


        // Get suggestion for tables for 10 guests
        reservation = getReservationFromTemplateWithGuestCount(10);

        List<RestaurantTable> suggestedTables;
        suggestedTables = getSuggestionForReservation(reservation);

        assertEquals(2, suggestedTables.size());
        assertEquals(t_30908.toString(), suggestedTables.get(0).toString());
        assertEquals(t_50904.toString(), suggestedTables.get(1).toString());

        reservation.setRestaurantTables(new HashSet<>(suggestedTables));
        performPostForValidReservationAndAssertCreated(reservation);

    }


    @Test
    public void GivenAllTablesReserved_When_UpdatingReservation_ExpectTablesOfReservationWhichIsToBeUpdated_AreIgnored() throws Exception{

        // Reserve the tables for 2 near the table for 8:
        Reservation reservation = getReservationFromTemplateWithGuestCount(2);
        HashSet<RestaurantTable> reservedTables = new HashSet<RestaurantTable>();
        reservedTables.add(t_10902);
        reservedTables.add(t_50502);
        reservation.setRestaurantTables(reservedTables);
        performPostForValidReservationAndAssertCreated(reservation);


        // Get suggestion for tables for 10 guests
        reservation = getReservationFromTemplateWithGuestCount(10);

        List<RestaurantTable> suggestedTables;
        suggestedTables = getSuggestionForReservation(reservation);

        assertEquals(2, suggestedTables.size());
        assertEquals(t_30908.toString(), suggestedTables.get(0).toString());
        assertEquals(t_50904.toString(), suggestedTables.get(1).toString());

        reservation.setRestaurantTables(new HashSet<>(suggestedTables));
        MockHttpServletResponse response = performPostForValidReservationAndAssertCreated(reservation);
        ReservationDto createdReservationDto = objectMapper.readValue(response.getContentAsString(),
            ReservationDto.class);


        // Now, there aren't enough tables for 8 guests anymore.
        // however, if we update the last created reservation for 10 guest,
        // to have only have 8 guests instead of 10, this should work.

        createdReservationDto.setNumberOfGuests(8);
        suggestedTables = getSuggestionForReservation(reservationMapper.reservationDtoToReservation(createdReservationDto));
        assertEquals(1, suggestedTables.size());
        assertEquals(t_30908.toString(), suggestedTables.get(0).toString());

        // Getting suggestion for 10 people again, will return the same result for 10 people as before:
        createdReservationDto.setNumberOfGuests(10);
        suggestedTables = getSuggestionForReservation(reservationMapper.reservationDtoToReservation(createdReservationDto));

        assertEquals(2, suggestedTables.size());
        assertEquals(t_30908.toString(), suggestedTables.get(0).toString());
        assertEquals(t_50904.toString(), suggestedTables.get(1).toString());

    }



    private List<RestaurantTable> getSuggestionForReservation(Reservation r) throws Exception{
        MockHttpServletResponse response;
        response = performFindTableSuggestionAndAssertOk(r.getNumberOfGuests(), r.getId(), r.getStartDateTime(), r.getEndDateTime());

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<RestaurantTableDto> tablesDto = Arrays.asList(objectMapper.readValue(response.getContentAsString(), RestaurantTableDto[].class));
        return restaurantTableMapper.restaurantTableDtoToEntity(tablesDto);
    }

    private void getSuggestionForReservationAndExpectException(Reservation r, String exceptionMessage) throws Exception{
        MockHttpServletResponse response;
        response = performFindTableSuggestionAndAssertOk(r.getNumberOfGuests(), r.getId(), r.getStartDateTime(), r.getEndDateTime());

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
        assertEquals("text/plain;charset=UTF-8", response.getContentType());
        assertTrue(response.getContentAsString().contains(exceptionMessage));
    }

    private MockHttpServletResponse performFindTableSuggestionAndAssertOk(Integer numberOfGuests, Long idOfReservationToIgnore, LocalDateTime start, LocalDateTime end) throws Exception{

        start = start.truncatedTo(ChronoUnit.SECONDS);
        end = end.truncatedTo(ChronoUnit.SECONDS);

        String startAsString, endAsString;
        startAsString = start.format(DateTimeFormatter.ISO_DATE_TIME);
        endAsString = end.format(DateTimeFormatter.ISO_DATE_TIME);
        String idOfReservationToIgnoreAsString = "";

        if(null != idOfReservationToIgnore){
            idOfReservationToIgnoreAsString = "" + idOfReservationToIgnore;
        }

        MvcResult mvcResult = this.mockMvc.perform(get(TABLE_BASE_URI +"?numberOfGuests=" + numberOfGuests + "&idOfReservationToIgnore=" + idOfReservationToIgnoreAsString + "&startDateTime=" + startAsString + "&endDateTime=" + endAsString)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response =  mvcResult.getResponse();
        return response;
    }

    private Reservation getReservationFromTemplateWithGuestCount(int guestCount) {
        Reservation reservation = reservationTemplate;
        reservation.setNumberOfGuests(guestCount);
        reservation.setGuestName("Guest with " + guestCount + " guests.");
        return reservation;
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

}
