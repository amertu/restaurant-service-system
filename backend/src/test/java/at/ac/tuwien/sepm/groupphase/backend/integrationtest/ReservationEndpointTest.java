package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ReservationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ReservationMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.RestaurantTableMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Reservation;
import at.ac.tuwien.sepm.groupphase.backend.entity.RestaurantTable;
import at.ac.tuwien.sepm.groupphase.backend.repository.ReservationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RestaurantTableRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class ReservationEndpointTest implements TestData {


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


    private RestaurantTable validRestaurantTable;
    private Set<RestaurantTable> validReservedRestaurantTables;

    @Autowired
    RestaurantTableMapper restaurantTableMapper;

    private Reservation validReservation;


    @BeforeEach
    public void beforeEach() {
        reservationRepository.deleteAll();
        restaurantTableRepository.deleteAll();

        validRestaurantTable = RestaurantTable.RestaurantTableBuilder.aTable()
            .withTableNum(100L)
            .withPosDescription("In ReservationEndpointTest")
            .withSeatCount(TEST_TABLE_CAPACITY_FOR_VALID_RESERVATION)
            .withActive(true)
            .build();
        validReservedRestaurantTables = new HashSet<RestaurantTable>();
        validReservedRestaurantTables.add(validRestaurantTable);

        validReservation = Reservation.ReservationBuilder.aReservation()
            .withGuestName(TEST_GUEST_FOR_VALID_RESERVATION)
            .withNumberOfGuests(TEST_GUEST_COUNT_FOR_VALID_RESERVATION)
            .withContactInformation(TEST_CONTACT_INFORMATION_FOR_VALID_RESERVATOIN)
            .withComment(TEST_COMMENT_FOR_VALID_RESERVATION)
            .withStartDateTime(TEST_START_DATE_TIME_FOR_VALID_RESERVATION )
            .withEndDateTime( TEST_END_DATE_TIME_FOR_VALID_RESERVATION)

            .withTables(validReservedRestaurantTables)
            .build();


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


    private String performPostForInvalidReservationAndAssertValidationError(Reservation invalidReservation) throws  Exception{
        // https://stackoverflow.com/questions/25288930/mockmvc-test-error-message

        ReservationDto reservationDto = reservationMapper.reservationToReservationDto(invalidReservation);
        String body = objectMapper.writeValueAsString(reservationDto);

        String errorMessage = this.mockMvc.perform(post(RESERVATION_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()))
            .andReturn().getResolvedException().getMessage();

        return errorMessage;
    }

    private MockHttpServletResponse performUpdateForValidReservationAndAssertCreated(Reservation reservation) throws  Exception{

        ReservationDto reservationDto = reservationMapper.reservationToReservationDto(reservation);
        String body = objectMapper.writeValueAsString(reservationDto);

        MvcResult mvcResult = this.mockMvc.perform(put(RESERVATION_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        return response;
    }

    private MockHttpServletResponse performFindAllAndAssertOkWithCorrectMediaType() throws Exception{
        MvcResult mvcResult = this.mockMvc.perform(get(RESERVATION_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response =  mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        return response;
    }

    private MockHttpServletResponse performFindByIdAssertOkWithCorrectMediaType(Long id) throws Exception{
        MvcResult mvcResult = this.mockMvc.perform(get(RESERVATION_BASE_URI + "/" + id)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response =  mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        return response;
    }

    private MockHttpServletResponse performFindByStartAndDateTimeAndAssertOk(LocalDateTime start, LocalDateTime end) throws Exception{

        start = start.truncatedTo(ChronoUnit.MILLIS);
        end = end.truncatedTo(ChronoUnit.MILLIS);

        String startAsString, endAsString;
        startAsString = start.format(DateTimeFormatter.ISO_DATE_TIME);
        endAsString = end.format(DateTimeFormatter.ISO_DATE_TIME);


        MvcResult mvcResult = this.mockMvc.perform(get(RESERVATION_BASE_URI +"?startDateTime=" + startAsString + "&endDateTime=" + endAsString)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response =  mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        return response;
    }



    private MockHttpServletResponse pefromDeleteForReservationWithId(Long id) throws  Exception{

        MvcResult mvcResult = this.mockMvc.perform(delete(RESERVATION_BASE_URI + "/" + id)
            .contentType(MediaType.APPLICATION_JSON)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        return response;
    }

    private Reservation getValidReservation(){
        return validReservation;
    }


    @Test
    public void givenNothing_whenPostValid_then201AndAllValuesSet() throws Exception {

        restaurantTableRepository.save(validRestaurantTable);

        MockHttpServletResponse response = performPostForValidReservationAndAssertCreated(validReservation);


        ReservationDto createdReservationDto = objectMapper.readValue(response.getContentAsString(),
            ReservationDto.class);

        assertAll(
            () -> assertEquals(TEST_GUEST_FOR_VALID_RESERVATION, createdReservationDto.getGuestName()),
            () -> assertEquals(TEST_START_DATE_TIME_FOR_VALID_RESERVATION, createdReservationDto.getStartDateTime()),
            () -> assertEquals(TEST_END_DATE_TIME_FOR_VALID_RESERVATION, createdReservationDto.getEndDateTime()),
            () -> assertEquals(validReservedRestaurantTables.toString(), createdReservationDto.getRestaurantTables().toString())  // TODO: check why equals fails for Set
        );

        //Set generated properties to null to make the response comparable with the original input
        assertTrue(isNowPlusMinusSeconds(createdReservationDto.getCreatedAt(), 1));
        createdReservationDto.setId(null);
        createdReservationDto.setCreatedAt(null);

        validReservation.setId(createdReservationDto.getId());
        validReservation.setCreatedAt(createdReservationDto.getCreatedAt());
        assertEquals(validReservation.toString(), reservationMapper.reservationDtoToReservation(createdReservationDto).toString()); // TODO: check why equals fails for Set

        // Check if findAll() returns the same result:
        response = performFindAllAndAssertOkWithCorrectMediaType();

        List<ReservationDto> reservationDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            ReservationDto[].class));

        assertEquals(1, reservationDtos.size());
        ReservationDto reservationDtoViaFindAll = reservationDtos.get(0);
        reservationDtoViaFindAll.setId(null);
        reservationDtoViaFindAll.setCreatedAt(null);
        assertEquals(validReservation.toString(), reservationMapper.reservationDtoToReservation(reservationDtoViaFindAll).toString()); // TODO: check why equals fails for Set

    }

    @Test
    public void When_ReservationOneTableAtTheSameTime_Expect_ValidationError() throws Exception{

        restaurantTableRepository.save(validRestaurantTable);

        MockHttpServletResponse response = performPostForValidReservationAndAssertCreated(validReservation);
        ReservationDto createdReservationDto = objectMapper.readValue(response.getContentAsString(),
            ReservationDto.class);


        assertTrue(isNowPlusMinusSeconds(createdReservationDto.getCreatedAt(), 1));


        validReservation.setId(createdReservationDto.getId());
        validReservation.setCreatedAt(createdReservationDto.getCreatedAt());

        String receivedErrorMessage  = performPostForInvalidReservationAndAssertValidationError(validReservation);

        String expectedErrorMessage =  "Validation error occured:\nThe following conflicting reservations were detected: \n\n1." + validReservation.toUserFriendlyString();

        /*
        int indexOfCreatedAt = expectedErrorMessage.indexOf("createdAt");
        int untilEndOfSeconds = indexOfCreatedAt+"createdAt=2020-05-11T11:24:34".length();

        expectedErrorMessage = expectedErrorMessage.substring(0, untilEndOfSeconds);
        receivedErrorMessage = receivedErrorMessage.substring(0, untilEndOfSeconds);
        */

        assertEquals(expectedErrorMessage, receivedErrorMessage);

    }



    @Test
    public void When_ReservationOneTableWithOverlappingStartDate_Expect_ValidationError() throws Exception{

        restaurantTableRepository.save(validRestaurantTable);

        MockHttpServletResponse response = performPostForValidReservationAndAssertCreated(validReservation);
        ReservationDto createdReservationDto = objectMapper.readValue(response.getContentAsString(),
            ReservationDto.class);
        validReservation.setId(createdReservationDto.getId());
        validReservation.setCreatedAt(createdReservationDto.getCreatedAt());

        Reservation newReservation = getVariationOfValidReservation("NewGuest", 2, validReservation.getStartDateTime().minusHours(1), 2);

        String receivedErrorMessage  = performPostForInvalidReservationAndAssertValidationError(newReservation);

        String expectedErrorMessage =  "Validation error occured:\nThe following conflicting reservations were detected: \n\n1." + validReservation.toUserFriendlyString();


        assertEquals(expectedErrorMessage, receivedErrorMessage);

    }

    @Test
    public void When_ReservationOneTableWithOverlappingEndDate_Expect_ValidationError() throws Exception{

        restaurantTableRepository.save(validRestaurantTable);

        MockHttpServletResponse response = performPostForValidReservationAndAssertCreated(validReservation);
        ReservationDto createdReservationDto = objectMapper.readValue(response.getContentAsString(),
            ReservationDto.class);

        assertTrue(isNowPlusMinusSeconds(createdReservationDto.getCreatedAt(), 1));

        validReservation.setId(createdReservationDto.getId());
        validReservation.setCreatedAt(createdReservationDto.getCreatedAt());


        Reservation newReservation = getVariationOfValidReservation("NewGuest", 5, validReservation.getStartDateTime().plusHours(1), 2);

        String receivedErrorMessage  = performPostForInvalidReservationAndAssertValidationError(newReservation);

        String expectedErrorMessage =  "Validation error occured:\nThe following conflicting reservations were detected: \n\n1." + validReservation.toUserFriendlyString();

        assertEquals(expectedErrorMessage, receivedErrorMessage);

    }

    @Test
    public void When_ReservationOneTable_With_StartDateTimeOfNewReservation_Equals_EndDateTimeOfExistingReservation_Expect_CreatedAndAllValuesSet() throws Exception{
        // INFO: EndDateTime is implemented as excluded of range
        restaurantTableRepository.save(validRestaurantTable);

        MockHttpServletResponse response = performPostForValidReservationAndAssertCreated(validReservation);
        ReservationDto createdReservationDto = objectMapper.readValue(response.getContentAsString(),
            ReservationDto.class);
        validReservation.setId(createdReservationDto.getId());

        Reservation newReservation = getVariationOfValidReservation("NewGuest", 4,  validReservation.getEndDateTime(), 1);

        response = performPostForValidReservationAndAssertCreated(newReservation);
        ReservationDto newlyCreatedReservationDto = objectMapper.readValue(response.getContentAsString(),
            ReservationDto.class);

        assertAll(
            () -> assertEquals("NewGuest", newlyCreatedReservationDto.getGuestName()),
            () -> assertEquals(validReservation.getEndDateTime(), newlyCreatedReservationDto.getStartDateTime()),
            () -> assertEquals(validReservation.getEndDateTime().plusHours(1), newlyCreatedReservationDto.getEndDateTime()),
            () -> assertEquals(new ArrayList<>(validReservedRestaurantTables), new ArrayList<>(newlyCreatedReservationDto.getRestaurantTables())) // TODO: check why equals fails for Set
        );

        response = performFindAllAndAssertOkWithCorrectMediaType();

        List<ReservationDto> reservationDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            ReservationDto[].class));

        assertEquals(2, reservationDtos.size());
    }



    @Test
    public void When_ReservationIsSaved_WithGuestNameIsNull_Expect_ValidationError() throws Exception{

        restaurantTableRepository.save(validRestaurantTable);

        Reservation reservation = getValidReservation();
        reservation.setGuestName(null);

        performPostForInvalidReservationAndAssertValidationError(reservation);

    }

    @Test
    public void When_ReservationIsSaved_WithGuestNameIsEmpty_Expect_ValidationError() throws Exception{

        restaurantTableRepository.save(validRestaurantTable);

        validReservation.setGuestName("");

        performPostForInvalidReservationAndAssertValidationError(validReservation);

    }

    @Test
    public void When_ReservationIsSaved_WithGuestNameIsBlank_Expect_ValidationError() throws Exception{

        restaurantTableRepository.save(validRestaurantTable);

        validReservation.setGuestName("     ");

        performPostForInvalidReservationAndAssertValidationError(validReservation);

    }

    @Test
    public void When_ReservationIsSaved_WithStartDateIsNull_Expect_ValidationError() throws Exception{

        restaurantTableRepository.save(validRestaurantTable);

        Reservation reservation = getValidReservation();
        reservation.setStartDateTime(null);

        performPostForInvalidReservationAndAssertValidationError(reservation);

    }

    @Test
    public void When_ReservationIsSaved_WithEndDateIsNull_Expect_ValidationError() throws Exception{

        restaurantTableRepository.save(validRestaurantTable);

        Reservation reservation = getValidReservation();
        reservation.setEndDateTime(null);

        performPostForInvalidReservationAndAssertValidationError(reservation);

    }

    @Test
    public void When_ReservationIsSaved_WithEndDateEqualToStartDate_Expect_ValidationError() throws Exception{

        restaurantTableRepository.save(validRestaurantTable);

        Reservation reservation = getValidReservation();
        reservation.setEndDateTime(reservation.getStartDateTime());

        performPostForInvalidReservationAndAssertValidationError(reservation);

    }

    @Test
    public void When_ReservationIsSaved_WithEndDateBeforeStartDate_Expect_ValidationError() throws Exception{

        restaurantTableRepository.save(validRestaurantTable);

        Reservation reservation = getValidReservation();
        LocalDateTime startDateTime = reservation.getStartDateTime();
        reservation.setEndDateTime(startDateTime.minusSeconds(1));

        performPostForInvalidReservationAndAssertValidationError(reservation);

    }

    @Test
    public void When_ReservationIsSaved_WithStartDateInThePast_Expect_ValidationError() throws Exception{

        restaurantTableRepository.save(validRestaurantTable);

        Reservation reservation = getValidReservation();
        LocalDateTime startDateTime = LocalDateTime.now();
        reservation.setStartDateTime(startDateTime.minusSeconds(1));

        performPostForInvalidReservationAndAssertValidationError(reservation);

    }

    @Test
    public void When_ReservationIsSaved_WithEndDateOnNextDay_Expect_ValidationError() throws Exception{

        restaurantTableRepository.save(validRestaurantTable);

        Reservation reservation = getValidReservation();
        LocalDateTime startDateTime = reservation.getStartDateTime();
        reservation.setStartDateTime(startDateTime.plusDays(1));

        performPostForInvalidReservationAndAssertValidationError(reservation);

    }

    @Test
    public void When_ReservationIsSaved_WithTablesAreNull_Expect_ValidationError() throws Exception{

        restaurantTableRepository.save(validRestaurantTable);

        Reservation reservation = getValidReservation();
        reservation.setRestaurantTables(null);

        performPostForInvalidReservationAndAssertValidationError(reservation);
        // TODO check error message

    }

    @Test
    public void When_ReservationIsSaved_WithoutTables_Expect_ValidationError() throws Exception{

        restaurantTableRepository.save(validRestaurantTable);

        Reservation reservation = getValidReservation();
        reservation.setRestaurantTables(new HashSet<RestaurantTable>());

        performPostForInvalidReservationAndAssertValidationError(reservation);
        // TODO check error message
    }

    @Test
    public void When_ReservationIsSaved_ContainingNonExistingTable_Expect_ValidationError() throws Exception{

        restaurantTableRepository.save(validRestaurantTable);

        RestaurantTable invalidTable = RestaurantTable.RestaurantTableBuilder.aTable()
            .withId(9999L)
            .withTableNum(101L)
            .withPosDescription("Invalid ID")
            .withSeatCount(TEST_TABLE_CAPACITY_FOR_VALID_RESERVATION)
            .withActive(false)
            .build();

        Reservation reservation = getValidReservation();
        Set<RestaurantTable> tablesToBook = new HashSet<RestaurantTable>();
        tablesToBook.add(invalidTable);

        reservation.setRestaurantTables(tablesToBook);

        performPostForInvalidReservationAndAssertValidationError(reservation);
        // TODO check error message
    }

    @Test
    public void When_ReservationIsSaved_ContainingDisabledTable_Expect_ValidationError() throws Exception{

        // TODO refactor:
        final String VALIDATION_ERROR_PREFIX = "Validation error occured:\n";
        final String TABLE_IS_NOT_AVAILABLE_MESSAGE = "Table {} is not available.";

        restaurantTableRepository.save(validRestaurantTable);

        RestaurantTable disabledTable = RestaurantTable.RestaurantTableBuilder.aTable()
            .withTableNum(102L)
            .withPosDescription("Disabled table")
            .withSeatCount(TEST_TABLE_CAPACITY_FOR_VALID_RESERVATION)
            .withActive(false)
            .build();

        RestaurantTable tableToBook = restaurantTableRepository.save(disabledTable);

        Reservation reservation = getValidReservation();
        Set<RestaurantTable> tablesToBook = new HashSet<RestaurantTable>();
        tablesToBook.add(tableToBook);

        reservation.setRestaurantTables(tablesToBook);

        String errorMessage = performPostForInvalidReservationAndAssertValidationError(reservation);
        assertEquals(VALIDATION_ERROR_PREFIX + "Table " + tableToBook.getTableNum() + " is not available.", errorMessage);

    }


    @Test
    public void givenNothing_whenFindAll_thenEmptyList() throws Exception {

        MockHttpServletResponse response = performFindAllAndAssertOkWithCorrectMediaType();

        List<ReservationDto> reservationDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            ReservationDto[].class));

        assertEquals(0, reservationDtos.size());
    }

    @Test
    public void  givenOneReservation_whenFindAll_thenListWithSizeOneAndReservationWithAllProperties() throws Exception {

        // https://stackoverflow.com/questions/48117059/could-not-write-json-failed-to-lazily-initialize-a-collection-of-role
        // TODO the "ignore = true" in the mapper wasn't sufficient. But with @JsonIgnoreProperties(value= {"reservations"}) it worked

        restaurantTableRepository.save(validRestaurantTable);
        reservationRepository.save(validReservation);

        MockHttpServletResponse response = performFindAllAndAssertOkWithCorrectMediaType();


        List<ReservationDto> reservationDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            ReservationDto[].class));

        assertEquals(1, reservationDtos.size());
        ReservationDto reservationDto = reservationDtos.get(0);
        assertAll(
            () -> assertEquals(TEST_GUEST_FOR_VALID_RESERVATION, reservationDto.getGuestName()),
            () -> assertEquals(TEST_START_DATE_TIME_FOR_VALID_RESERVATION, reservationDto.getStartDateTime()),
            () -> assertEquals(TEST_END_DATE_TIME_FOR_VALID_RESERVATION, reservationDto.getEndDateTime()),
            () -> assertEquals(validReservedRestaurantTables.toString(), reservationDto.getRestaurantTables().toString())  // TODO: check why equals fails for Set
        );
    }

    private Reservation getVariationOfValidReservation(String guestName, Integer numberOfGuests, LocalDateTime startDateTime, Integer durationInHours){
        Reservation b = Reservation.ReservationBuilder.aReservation()
            .withGuestName(guestName)
            .withNumberOfGuests(numberOfGuests)
            .withStartDateTime(startDateTime)
            .withEndDateTime(startDateTime.plusHours(durationInHours))
            .withTables(validReservedRestaurantTables)
            .build();
        return b;
    }


    @Test
    public void givenReservationsWithArbitraryStartDates_whenFindAll_thenListWithStartDatesInAscendingOrder() throws  Exception{

        restaurantTableRepository.save(validRestaurantTable);

        // Info: this test is written very verbous by intention, to show correct values explicitly

        LocalDateTime baseDate = TEST_START_DATE_TIME_FOR_VALID_RESERVATION;
        LocalDateTime dateTime1 = baseDate.plusDays(4);
        LocalDateTime dateTime2 = baseDate;
        LocalDateTime dateTime3 = baseDate.minusDays(2);
        LocalDateTime dateTime4 = baseDate.plusDays(1);


        Reservation b1 = getVariationOfValidReservation("Guest 1", 3, dateTime1, 3);

        Reservation b2 = getVariationOfValidReservation("Guest 2", 4, dateTime2, 4);

        Reservation b3 = getVariationOfValidReservation("Guest 3", 2,dateTime3, 1);

        Reservation b4 = getVariationOfValidReservation("Guest 4", 6, dateTime4, 2);



        reservationRepository.save(b1);
        reservationRepository.save(b2);
        reservationRepository.save(b3);
        reservationRepository.save(b4);


        MockHttpServletResponse response = performFindAllAndAssertOkWithCorrectMediaType();


        List<ReservationDto> reservationDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            ReservationDto[].class));

        assertEquals(4, reservationDtos.size());
        ReservationDto reservationDto = reservationDtos.get(0);
        assertAll(
            () -> assertEquals("Guest 3", reservationDto.getGuestName()),
            () -> assertEquals(dateTime3, reservationDto.getStartDateTime()),
            () -> assertEquals(dateTime3.plusHours(1), reservationDto.getEndDateTime()),
            () -> assertEquals(validReservedRestaurantTables.toString(), reservationDto.getRestaurantTables().toString()) // TODO: check why equals fails for Set
        );



        ReservationDto reservationDto1 = reservationDtos.get(1);
        assertAll(
            () -> assertEquals("Guest 2", reservationDto1.getGuestName()),
            () -> assertEquals(dateTime2, reservationDto1.getStartDateTime()),
            () -> assertEquals(dateTime2.plusHours(4), reservationDto1.getEndDateTime()),
            () -> assertEquals(validReservedRestaurantTables.toString(), reservationDto1.getRestaurantTables().toString()) // TODO: check why equals fails for Set
        );

        ReservationDto reservationDto2 = reservationDtos.get(2);
        assertAll(
            () -> assertEquals("Guest 4", reservationDto2.getGuestName()),
            () -> assertEquals(dateTime4, reservationDto2.getStartDateTime()),
            () -> assertEquals(dateTime4.plusHours(2), reservationDto2.getEndDateTime()),
            () -> assertEquals(validReservedRestaurantTables.toString(), reservationDto2.getRestaurantTables().toString()) // TODO: check why equals fails for Set
        );

        ReservationDto reservationDto3 = reservationDtos.get(3);
        assertAll(
            () -> assertEquals("Guest 1", reservationDto3.getGuestName()),
            () -> assertEquals(dateTime1, reservationDto3.getStartDateTime()),
            () -> assertEquals(dateTime1.plusHours(3), reservationDto3.getEndDateTime()),
            () -> assertEquals(validReservedRestaurantTables.toString(), reservationDto3.getRestaurantTables().toString()) // // TODO: check why equals fails for Set
        );


    }

    @Test
    public void givenFourReservations_WhenFirstAndThirdAreDeleted_findAllReturnsOnlySecondAndThirdReservation() throws Exception{

        restaurantTableRepository.save(validRestaurantTable);

        // Info: this test is written very verbous by intention, to show correct values explicitly

        LocalDateTime baseDate = TEST_START_DATE_TIME_FOR_VALID_RESERVATION;
        LocalDateTime dateTime1 = baseDate.plusDays(4);
        LocalDateTime dateTime2 = baseDate;
        LocalDateTime dateTime3 = baseDate.minusDays(2);
        LocalDateTime dateTime4 = baseDate.plusDays(1);


        Reservation b1 = getVariationOfValidReservation("Guest 1", 2, dateTime1, 3);

        Reservation b2 = getVariationOfValidReservation("Guest 2", 1, dateTime2, 4);

        Reservation b3 = getVariationOfValidReservation("Guest 3", 4, dateTime3, 1);

        Reservation b4 = getVariationOfValidReservation("Guest 4", 6, dateTime4, 2);



        reservationRepository.save(b1);
        reservationRepository.save(b2);
        reservationRepository.save(b3);
        reservationRepository.save(b4);


        MockHttpServletResponse response = performFindAllAndAssertOkWithCorrectMediaType();


        List<ReservationDto> reservationDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            ReservationDto[].class));

        assertEquals(4, reservationDtos.size());

        Long id1 = reservationDtos.get(0).getId();
        Long id4 = reservationDtos.get(3).getId();


        MvcResult mvcResult = this.mockMvc.perform(delete(RESERVATION_BASE_URI + "/" + id1)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        mvcResult = this.mockMvc.perform(delete(RESERVATION_BASE_URI + "/" + id4)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        response = performFindAllAndAssertOkWithCorrectMediaType();


        reservationDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            ReservationDto[].class));

        assertEquals(2, reservationDtos.size());

        ReservationDto reservationDto1 = reservationDtos.get(0);
        assertAll(
            () -> assertEquals("Guest 2", reservationDto1.getGuestName()),
            () -> assertEquals(dateTime2, reservationDto1.getStartDateTime()),
            () -> assertEquals(dateTime2.plusHours(4), reservationDto1.getEndDateTime()),
            () -> assertEquals(validReservedRestaurantTables.toString(), reservationDto1.getRestaurantTables().toString())  // TODO: check why equals fails for Set
        );

        ReservationDto reservationDto2 = reservationDtos.get(1);
        assertAll(
            () -> assertEquals("Guest 4", reservationDto2.getGuestName()),
            () -> assertEquals(dateTime4, reservationDto2.getStartDateTime()),
            () -> assertEquals(dateTime4.plusHours(2), reservationDto2.getEndDateTime()),
            () -> assertEquals(validReservedRestaurantTables.toString(), reservationDto2.getRestaurantTables().toString()) //  // TODO: check why equals fails for Set
        );

    }

    @Test
    public void Given_ReservationsAtDifferentTimes_WithDifferentTables_When_FindByStartAndEndDateTime_Expect_AppropriateReservations() throws Exception{

        final int NUMBER_OF_RESERVATIONS_TO_GENERATE = 3;
        final String TEST_GUEST = "TEST GUEST";
        final LocalDateTime TEST_START_DATE_TIME = LocalDateTime.of(2099,5, 1, 12,0,0);
        final LocalDateTime TEST_END_DATE_TIME = LocalDateTime.of(2099,5, 1, 13,0,0);
        List<RestaurantTable> tablesToCreate;
        List<RestaurantTable> createdTables;
        Set<RestaurantTable> tablesToBeReserved;

        RestaurantTable table1 = RestaurantTable.RestaurantTableBuilder.aTable()
            .withTableNum(103L)
            .withPosDescription("In ReservationEndpointTest")
            .withSeatCount(6)
            .withActive(true)
            .build();

        RestaurantTable table2 = RestaurantTable.RestaurantTableBuilder.aTable()
            .withTableNum(104L)
            .withPosDescription("In ReservationEndpointTest")
            .withSeatCount(4)
            .withActive(true)
            .build();

        RestaurantTable table3 = RestaurantTable.RestaurantTableBuilder.aTable()
            .withTableNum(105L)
            .withPosDescription("In ReservationEndpointTest")
            .withSeatCount(2)
            .withActive(true)
            .build();


        tablesToCreate = new ArrayList<RestaurantTable>();
        tablesToCreate.add(table1);
        tablesToCreate.add(table2);
        tablesToCreate.add(table3);

        createdTables = new ArrayList<RestaurantTable>();

        for(RestaurantTable t: tablesToCreate){
            RestaurantTable createdTable = restaurantTableRepository.save(t);
            createdTables.add(createdTable);
        }

        tablesToBeReserved = new HashSet<RestaurantTable>();

        for (int i = 0; i < NUMBER_OF_RESERVATIONS_TO_GENERATE; i++) {
            tablesToBeReserved.add(createdTables.get(i));

            Reservation reservation = Reservation.ReservationBuilder.aReservation()
                .withGuestName(TEST_GUEST + " - " + i)
                .withNumberOfGuests(i+1)
                .withStartDateTime(TEST_START_DATE_TIME.plusHours(2*i))
                .withEndDateTime(TEST_END_DATE_TIME.plusHours(2*i))
                .withTables(tablesToBeReserved)
                .build();
            reservationRepository.save(reservation);
        }

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusHours(2);

        // TEST 1: no reservations found:
        MockHttpServletResponse response = performFindByStartAndDateTimeAndAssertOk(startDate, endDate);

        List<ReservationDto> reservationDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            ReservationDto[].class));

        assertEquals(0, reservationDtos.size());


        // TEST 2: one reservation found with one table:
        response = performFindByStartAndDateTimeAndAssertOk(TEST_START_DATE_TIME, TEST_END_DATE_TIME);

        reservationDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            ReservationDto[].class));

        assertEquals(1, reservationDtos.size());
        assertEquals(TEST_START_DATE_TIME, reservationDtos.get(0).getStartDateTime());
        assertEquals(TEST_END_DATE_TIME, reservationDtos.get(0).getEndDateTime());
        assertEquals(1, reservationDtos.get(0).getRestaurantTables().size());


        // TEST 3: one reservation found with two tables:
        LocalDateTime startDate3 = TEST_START_DATE_TIME.plusHours(2);
        LocalDateTime endDate3 = TEST_END_DATE_TIME.plusHours(2);
        response = performFindByStartAndDateTimeAndAssertOk(startDate3, endDate3);

        reservationDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            ReservationDto[].class));

        assertEquals(1, reservationDtos.size());
        assertEquals(TEST_START_DATE_TIME.plusHours(2), reservationDtos.get(0).getStartDateTime());
        assertEquals(TEST_END_DATE_TIME.plusHours(2), reservationDtos.get(0).getEndDateTime());
        assertEquals(2, reservationDtos.get(0).getRestaurantTables().size());

        // TEST 4: one reservation found with three tables:
        LocalDateTime startDate4 = TEST_START_DATE_TIME.plusHours(4);
        LocalDateTime endDate4 = TEST_END_DATE_TIME.plusHours(4);
        response = performFindByStartAndDateTimeAndAssertOk(startDate4, endDate4);

        reservationDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            ReservationDto[].class));

        assertEquals(1, reservationDtos.size());
        assertEquals(TEST_START_DATE_TIME.plusHours(4), reservationDtos.get(0).getStartDateTime());
        assertEquals(TEST_END_DATE_TIME.plusHours(4), reservationDtos.get(0).getEndDateTime());
        assertEquals(3, reservationDtos.get(0).getRestaurantTables().size());

        // TEST 5: three reservations found with different numbers of reserved tables.

        response = performFindByStartAndDateTimeAndAssertOk(TEST_START_DATE_TIME, endDate4);

        reservationDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            ReservationDto[].class));

        assertEquals(3, reservationDtos.size());

        assertEquals(TEST_START_DATE_TIME, reservationDtos.get(0).getStartDateTime());
        assertEquals(TEST_END_DATE_TIME, reservationDtos.get(0).getEndDateTime());
        assertEquals(1, reservationDtos.get(0).getRestaurantTables().size());


        assertEquals(TEST_START_DATE_TIME.plusHours(2), reservationDtos.get(1).getStartDateTime());
        assertEquals(TEST_END_DATE_TIME.plusHours(2), reservationDtos.get(1).getEndDateTime());
        assertEquals(2, reservationDtos.get(1).getRestaurantTables().size());

        assertEquals(TEST_START_DATE_TIME.plusHours(4), reservationDtos.get(2).getStartDateTime());
        assertEquals(TEST_END_DATE_TIME.plusHours(4), reservationDtos.get(2).getEndDateTime());
        assertEquals(3, reservationDtos.get(2).getRestaurantTables().size());

    }


    @Test
    public void When_DeletingAReservation_Expect_Status200_And_ReservationIsDeleted() throws  Exception{
        restaurantTableRepository.save(validRestaurantTable);

        MockHttpServletResponse response = performPostForValidReservationAndAssertCreated(validReservation);


        ReservationDto createdReservationDto = objectMapper.readValue(response.getContentAsString(),
            ReservationDto.class);

        assertAll(
            () -> assertEquals(TEST_GUEST_FOR_VALID_RESERVATION, createdReservationDto.getGuestName()),
            () -> assertEquals(TEST_START_DATE_TIME_FOR_VALID_RESERVATION, createdReservationDto.getStartDateTime()),
            () -> assertEquals(TEST_END_DATE_TIME_FOR_VALID_RESERVATION, createdReservationDto.getEndDateTime()),
            () -> assertEquals(validReservedRestaurantTables.toString(), createdReservationDto.getRestaurantTables().toString())  // TODO: check why equals fails for Set
        );

        //Set generated properties to null to make the response comparable with the original input
        assertTrue(isNowPlusMinusSeconds(createdReservationDto.getCreatedAt(), 1));
        createdReservationDto.setCreatedAt(null);

        validReservation.setId(createdReservationDto.getId());
        validReservation.setCreatedAt(createdReservationDto.getCreatedAt());
        assertEquals(validReservation.toString(), reservationMapper.reservationDtoToReservation(createdReservationDto).toString()); // TODO: check why equals fails for Set

        // Check if findAll() returns the same result:
        response = performFindAllAndAssertOkWithCorrectMediaType();

        List<ReservationDto> reservationDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            ReservationDto[].class));

        assertEquals(1, reservationDtos.size());

        response = pefromDeleteForReservationWithId(createdReservationDto.getId());


        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(null, response.getContentType()); // TODO ensure no content returned is correct behaviour

        // Check if findAll() returns 0 reservations:
        response = performFindAllAndAssertOkWithCorrectMediaType();

        reservationDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            ReservationDto[].class));

        assertEquals(0, reservationDtos.size());

    }

    @Test
    public void When_DeletingAReservation_With_NonExistingId_Expect_NotFoundException() throws Exception{
        MockHttpServletResponse response = pefromDeleteForReservationWithId(9999L);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void givenOneReservation_whenFindByNonExistingId_then404() throws Exception {

        restaurantTableRepository.save(validRestaurantTable);

        MockHttpServletResponse response = performPostForValidReservationAndAssertCreated(validReservation);

        MvcResult mvcResult = this.mockMvc.perform(get(MESSAGE_BASE_URI + "/{id}", -1)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }



    @Test
    public void When_UpdatingAllValues_OfAnExistingReservation_Expect_Status200_And_AllValuesUpdated() throws Exception{

        reservationRepository.deleteAll();;
        restaurantTableRepository.deleteAll();

        // =================================================
        // Create tables:

        RestaurantTable table1 = new RestaurantTable(validRestaurantTable);
        table1.setTableNum(1001L);
        table1.setSeatCount(4);
        RestaurantTable table2 = new RestaurantTable(validRestaurantTable);
        table2.setTableNum(1002L);
        table2.setSeatCount(2);
        RestaurantTable table3 = new RestaurantTable(validRestaurantTable);
        table3.setTableNum(1003L);
        table3.setSeatCount(1);

        restaurantTableRepository.save(table1);
        restaurantTableRepository.save(table2);
        restaurantTableRepository.save(table3);


        // =================================================
        // Create a reservation which will be updated later:
        Set<RestaurantTable> tablesToBook = new HashSet<RestaurantTable>();
        tablesToBook.add(table1);
        tablesToBook.add(table2);

        Reservation reservation1 = getVariationOfValidReservation("Guest 1", 6, TEST_START_DATE_TIME_FOR_VALID_RESERVATION, 3);
        reservation1.setRestaurantTables(tablesToBook);

        ReservationDto reservationToBeCreatedDto = reservationMapper.reservationToReservationDto(reservation1);

        MockHttpServletResponse response = performPostForValidReservationAndAssertCreated(reservation1);


        ReservationDto createdReservationDto = objectMapper.readValue(response.getContentAsString(),
            ReservationDto.class);

        reservationToBeCreatedDto.setId(createdReservationDto.getId());
        reservationToBeCreatedDto.setCreatedAt(createdReservationDto.getCreatedAt());

        assertEquals(reservationToBeCreatedDto, createdReservationDto);
        // =================================================


        // =================================================
        // FindById returns the expected reservation
        response = performFindByIdAssertOkWithCorrectMediaType(createdReservationDto.getId());

        ReservationDto foundReservationBeforeUpdate = objectMapper.readValue(response.getContentAsString(),
            ReservationDto.class);
        foundReservationBeforeUpdate.setCreatedAt(reservationToBeCreatedDto.getCreatedAt()); // TODO understand the problem.
        assertEquals(reservationToBeCreatedDto, foundReservationBeforeUpdate);
        assertEquals(true, foundReservationBeforeUpdate.getRestaurantTables().contains(table1));
        assertEquals(true, foundReservationBeforeUpdate.getRestaurantTables().contains(table2));
        assertEquals(false, foundReservationBeforeUpdate.getRestaurantTables().contains(table3));
        // =================================================

        // =================================================
        // Perform update with changing all values
        Reservation reservationWithUpdatedData = Reservation.ReservationBuilder.aReservation()
            .withId(createdReservationDto.getId())
            .withGuestName("Updated guest name")
            .withNumberOfGuests(5)
            .withContactInformation("+43 updated...")
            .withComment("updated comment")
            .withStartDateTime(LocalDateTime.of(3999,5,1,9,30))
            .withEndDateTime(LocalDateTime.of(3999,5,1,10,30))
            .build();

        Set<RestaurantTable> tablesToBookUpdated = new HashSet<RestaurantTable>();
        tablesToBookUpdated.add(table1);
        tablesToBookUpdated.add(table3);

        reservationWithUpdatedData.setRestaurantTables(tablesToBookUpdated);
        ReservationDto reservationWithUpdatedDataDto = reservationMapper.reservationToReservationDto(reservationWithUpdatedData);

        response = performUpdateForValidReservationAndAssertCreated(reservationWithUpdatedData);

        ReservationDto updatedReservation = objectMapper.readValue(response.getContentAsString(),
            ReservationDto.class);
        assertEquals(reservationWithUpdatedDataDto, updatedReservation);
        // =================================================

        // =================================================
        // FindById returns the updated reservation
        response = performFindByIdAssertOkWithCorrectMediaType(createdReservationDto.getId());

        ReservationDto foundReservationAfterUpdate = objectMapper.readValue(response.getContentAsString(),
            ReservationDto.class);
        assertEquals(reservationWithUpdatedDataDto, foundReservationAfterUpdate);

        assertAll(
            () -> assertEquals(createdReservationDto.getId(), foundReservationAfterUpdate.getId()),
            () -> assertEquals("Updated guest name", foundReservationAfterUpdate.getGuestName()),
            () -> assertEquals("+43 updated...", foundReservationAfterUpdate.getContactInformation()),
            () -> assertEquals("updated comment", foundReservationAfterUpdate.getComment()),
            () -> assertEquals(LocalDateTime.of(3999,5,1,9,30), foundReservationAfterUpdate.getStartDateTime()),
            () -> assertEquals(LocalDateTime.of(3999,5,1,10,30), foundReservationAfterUpdate.getEndDateTime()),
            () -> assertEquals(true, foundReservationAfterUpdate.getRestaurantTables().contains(table1)),
            () -> assertEquals(false, foundReservationAfterUpdate.getRestaurantTables().contains(table2)),
            () -> assertEquals(true, foundReservationAfterUpdate.getRestaurantTables().contains(table3))
        );

        // =================================================
    }

    @Test
    public void whenSeatCapacityAtSelectedTablesIsNotSufficient_Expect_ValidationError() throws Exception{
        RestaurantTable table1 = new RestaurantTable(validRestaurantTable);
        table1.setTableNum(3001L);
        table1.setSeatCount(4);
        RestaurantTable table2 = new RestaurantTable(validRestaurantTable);
        table2.setTableNum(3002L);
        table2.setSeatCount(2);

        restaurantTableRepository.save(table1);
        restaurantTableRepository.save(table2);

        Set<RestaurantTable> tablesToBook = new HashSet<RestaurantTable>();
        tablesToBook.add(table1);
        tablesToBook.add(table2);


        Reservation reservation = getVariationOfValidReservation("Guest 1", 7, TEST_START_DATE_TIME_FOR_VALID_RESERVATION, 3);
        reservation.setRestaurantTables(tablesToBook);

        String errorMessage = performPostForInvalidReservationAndAssertValidationError(reservation);
        assertTrue(errorMessage.contains("There are not enough seats at the selected tables for the given number of guests."));

    }


    @DirtiesContext( methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    public void whenSeatCapacityAtSelectedTablesIsSufficient_Expect_Created() throws Exception{
        RestaurantTable table1 = new RestaurantTable(validRestaurantTable);
        table1.setTableNum(1L);
        table1.setSeatCount(4);
        RestaurantTable table2 = new RestaurantTable(validRestaurantTable);
        table2.setTableNum(2L);
        table2.setSeatCount(2);
        RestaurantTable table3 = new RestaurantTable(validRestaurantTable);
        table3.setTableNum(3L);
        table3.setSeatCount(1);

        restaurantTableRepository.save(table1);
        restaurantTableRepository.save(table2);
        restaurantTableRepository.save(table3);

        Set<RestaurantTable> tablesToBook = new HashSet<RestaurantTable>();
        tablesToBook.add(table1);
        tablesToBook.add(table2);
        tablesToBook.add(table3);

        Reservation reservation = getVariationOfValidReservation("Guest 1", 7, TEST_START_DATE_TIME_FOR_VALID_RESERVATION, 3);
        reservation.setRestaurantTables(tablesToBook);

        ReservationDto reservationToBeCreatedDto = reservationMapper.reservationToReservationDto(reservation);

        MockHttpServletResponse response = performPostForValidReservationAndAssertCreated(reservation);


        ReservationDto createdReservationDto = objectMapper.readValue(response.getContentAsString(),
            ReservationDto.class);

        reservationToBeCreatedDto.setId(createdReservationDto.getId());
        reservationToBeCreatedDto.setCreatedAt(createdReservationDto.getCreatedAt());

        assertEquals(reservationToBeCreatedDto, createdReservationDto);

    }



    private boolean isNowPlusMinusSeconds(LocalDateTime dateTime, int allowedDifferenceInSeconds) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lowerLimit = now.minusSeconds(allowedDifferenceInSeconds);
        LocalDateTime upperLimit = now.plusSeconds(allowedDifferenceInSeconds);
        return dateTime.isAfter(lowerLimit) && dateTime.isBefore(upperLimit);
    }




}
