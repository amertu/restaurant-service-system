package com.spring.restaurant.backend.integrationtest;

import com.spring.restaurant.backend.basetest.TestData;
import com.spring.restaurant.backend.config.properties.SecurityProperties;
import com.spring.restaurant.backend.endpoint.dto.PointDto;
import com.spring.restaurant.backend.endpoint.dto.RestaurantTableCoordinatesDto;
import com.spring.restaurant.backend.endpoint.dto.RestaurantTableDto;
import com.spring.restaurant.backend.endpoint.dto.RestaurantTableStatusDto;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)//restart Spring Context so that tests won't fail because of data from other test classes
class RestaurantTableEndpointTest implements TestData {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestaurantTableRepository tableRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestaurantTableMapper tableMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private RestaurantTable table = RestaurantTable.RestaurantTableBuilder.aTable()
        .withPosDescription(TEST_TABLE_POS_DESCRIPTION)
        .withActive(TEST_TABLE_ACTIVE)
        .withSeatCount(TEST_TABLE_SEAT_COUNT)
        .build();

    @BeforeEach
    void setUp() {
        tableRepository.deleteAll();
        table = RestaurantTable.RestaurantTableBuilder.aTable()
            .withTableNum(TEST_TABLE_TABLE_NUM)
            .withPosDescription(TEST_TABLE_POS_DESCRIPTION)
            .withActive(TEST_TABLE_ACTIVE)
            .withSeatCount(TEST_TABLE_SEAT_COUNT)
            .build();
    }

    @Test
    void givenNothing_whenFindAll_thenEmptyList() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(TABLE_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<RestaurantTableDto> tableDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            RestaurantTableDto[].class));

        assertEquals(0, tableDtos.size());
    }

    @Test
    void givenOneTable_whenFindById_thenTableWithAllProperties() throws Exception {

        RestaurantTable savedTable = tableRepository.save(table);
        table.setId(savedTable.getId());//doing this so I can compare given and saved table with equals()

        MvcResult mvcResult = mockMvc.perform(get(TABLE_BASE_URI + "/" + savedTable.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        RestaurantTableDto tableDto = objectMapper.readValue(response.getContentAsString(), RestaurantTableDto.class);

        assertEquals(tableMapper.restaurantTableEntityToDto(table), tableDto);
    }

    @Test
    void givenNothing_whenPost_thenTableWithAllSetPropertiesPlusId() throws Exception {
        RestaurantTableDto inputTableDto = tableMapper.restaurantTableEntityToDto(table);
        String body = objectMapper.writeValueAsString(inputTableDto);

        MvcResult mvcResult = mockMvc.perform(post(TABLE_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)//IMPORTANT, else unsupported media type error
            .content(body)//forgot that too in first place
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        RestaurantTableDto outputTableDto = objectMapper.readValue(response.getContentAsString(), RestaurantTableDto.class);

        assertAll(
            () -> assertEquals(inputTableDto.getTableNum(), outputTableDto.getTableNum()),
            () -> assertEquals(inputTableDto.getActive(), outputTableDto.getActive()),
            () -> assertEquals(inputTableDto.getPosDescription(), outputTableDto.getPosDescription()),
            () -> assertEquals(inputTableDto.getSeatCount(), outputTableDto.getSeatCount())
        );
    }

    @Test
    void givenOneTable_whenDeleteByIdAsAdmin_thenRepositoryEmpty() throws Exception {

        RestaurantTable savedTable = tableRepository.save(table);

        MvcResult mvcResult = mockMvc.perform(delete(TABLE_BASE_URI + "/" + savedTable.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        assertEquals(0, tableRepository.findAll().size());
    }

    @Test
    void givenOneTable_whenDeleteByIdAsUser_thenForbiddenErrorResponse() throws Exception {

        RestaurantTable savedTable = tableRepository.save(table);

        MvcResult mvcResult = mockMvc.perform(delete(TABLE_BASE_URI + "/" + savedTable.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());

        assertEquals(1, tableRepository.findAll().size());
    }

    /*//TODO: delete this test as soon as we've decided, if we want to have a "safe" delete and regular delete
    @Test
    void givenOneTableWithReservationInFuture_whenDeleteByIdAsAdmin_thenNothingDeletedConflictResponseCodeAndAppropriateMessage() throws Exception {
        RestaurantTable savedTable = tableRepository.save(table);
        Reservation reservation = Reservation.ReservationBuilder.aReservation()
            .withGuestName(TEST_GUEST_FOR_VALID_RESERVATION)
            .withNumberOfGuests(table.getSeatCount())
            .withStartDateTime(TEST_START_DATE_TIME_FOR_VALID_RESERVATION)
            .withEndDateTime(TEST_END_DATE_TIME_FOR_VALID_RESERVATION)
            .withTables(new HashSet<>(Arrays.asList(table)))
            .build();
        Reservation savedReservation = reservationRepository.save(reservation);

        MvcResult mvcResult = mockMvc.perform(delete(TABLE_BASE_URI + "/" + savedTable.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("There are still bookings for this table in the future!"));
        assertEquals(1, tableRepository.findAll().size());
        reservationRepository.deleteAll();
    }*/

    @Test
    void givenOneTableWithReservationInFuture_whenSetActiveFalseAsUser_thenTableRepositoryUnchangedAndReservationRepositoryEmptyAndTableDeactivated() throws Exception {
        RestaurantTable savedTable = tableRepository.save(table);
        Reservation reservation = Reservation.ReservationBuilder.aReservation()
            .withGuestName(TEST_GUEST_FOR_VALID_RESERVATION)
            .withNumberOfGuests(table.getSeatCount())
            .withStartDateTime(TEST_START_DATE_TIME_FOR_VALID_RESERVATION)
            .withEndDateTime(TEST_END_DATE_TIME_FOR_VALID_RESERVATION)
            .withTables(new HashSet<>(Arrays.asList(table)))
            .build();
        reservationRepository.save(reservation);
        RestaurantTableStatusDto disabledTableDto = new RestaurantTableStatusDto();
        disabledTableDto.setActive(false);
        disabledTableDto.setId(savedTable.getId());
        String body = objectMapper.writeValueAsString(disabledTableDto);

        MvcResult mvcResult = mockMvc.perform(patch(TABLE_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)//IMPORTANT, else unsupported media type error
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        RestaurantTableDto returnedTableDto = objectMapper.readValue(response.getContentAsString(), RestaurantTableDto.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(1, tableRepository.findAll().size());
        assertEquals(0, reservationRepository.findAll().size());
        assertAll(
            () -> assertEquals(savedTable.getId(), returnedTableDto.getId()),
            () -> assertEquals(savedTable.getTableNum(), returnedTableDto.getTableNum()),
            () -> assertEquals(false, returnedTableDto.getActive()),
            () -> assertEquals(savedTable.getPosDescription(), returnedTableDto.getPosDescription()),
            () -> assertEquals(savedTable.getSeatCount(), returnedTableDto.getSeatCount())
        );

        reservationRepository.deleteAll();//cleanup
    }

    @Test
    void givenOneTableWithReservationInFuture_whenSetActiveFalseAsAdmin_thenTableRepositoryUnchangedAndReservationRepositoryEmptyAndTableDeactivated() throws Exception {
        RestaurantTable savedTable = tableRepository.save(table);
        Reservation reservation = Reservation.ReservationBuilder.aReservation()
            .withGuestName(TEST_GUEST_FOR_VALID_RESERVATION)
            .withNumberOfGuests(table.getSeatCount())
            .withStartDateTime(TEST_START_DATE_TIME_FOR_VALID_RESERVATION)
            .withEndDateTime(TEST_END_DATE_TIME_FOR_VALID_RESERVATION)
            .withTables(new HashSet<>(Arrays.asList(table)))
            .build();
        reservationRepository.save(reservation);
        RestaurantTableStatusDto disabledTableDto = new RestaurantTableStatusDto();
        disabledTableDto.setActive(false);
        disabledTableDto.setId(savedTable.getId());
        String body = objectMapper.writeValueAsString(disabledTableDto);

        MvcResult mvcResult = mockMvc.perform(patch(TABLE_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)//IMPORTANT, else unsupported media type error
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        RestaurantTableDto returnedTableDto = objectMapper.readValue(response.getContentAsString(), RestaurantTableDto.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(1, tableRepository.findAll().size());
        assertEquals(0, reservationRepository.findAll().size());
        assertAll(
            () -> assertEquals(savedTable.getId(), returnedTableDto.getId()),
            () -> assertEquals(savedTable.getTableNum(), returnedTableDto.getTableNum()),
            () -> assertEquals(false, returnedTableDto.getActive()),
            () -> assertEquals(savedTable.getPosDescription(), returnedTableDto.getPosDescription()),
            () -> assertEquals(savedTable.getSeatCount(), returnedTableDto.getSeatCount())
        );

        reservationRepository.deleteAll();//cleanup
    }

    @Test
    void givenTwoTablesWithOneReservationInFutureEach_whenSetActiveFalseForOneAsAdmin_thenTableRepositoryUnchangedAndReservationRepositorySizeOneAndOneTableDeactivated() throws Exception {
        RestaurantTable savedTable = tableRepository.save(table);
        Reservation reservation = Reservation.ReservationBuilder.aReservation()
            .withGuestName(TEST_GUEST_FOR_VALID_RESERVATION)
            .withNumberOfGuests(table.getSeatCount())
            .withStartDateTime(TEST_START_DATE_TIME_FOR_VALID_RESERVATION)
            .withEndDateTime(TEST_END_DATE_TIME_FOR_VALID_RESERVATION)
            .withTables(new HashSet<>(Arrays.asList(table)))
            .build();
        reservationRepository.save(reservation);
        RestaurantTableStatusDto disabledTableDto = new RestaurantTableStatusDto();
        disabledTableDto.setActive(false);
        disabledTableDto.setId(savedTable.getId());
        String body = objectMapper.writeValueAsString(disabledTableDto);

        RestaurantTable table2 = RestaurantTable.RestaurantTableBuilder.aTable()
            .withActive(true)
            .withPosDescription("lol")
            .withSeatCount(4)
            .withTableNum(567L)
            .build();
        RestaurantTable savedTable2 = tableRepository.save(table2);
        Reservation reservation2 = Reservation.ReservationBuilder.aReservation()
            .withGuestName(TEST_GUEST_FOR_VALID_RESERVATION)
            .withNumberOfGuests(table.getSeatCount())
            .withStartDateTime(TEST_START_DATE_TIME_FOR_VALID_RESERVATION)
            .withEndDateTime(TEST_END_DATE_TIME_FOR_VALID_RESERVATION)
            .withTables(new HashSet<>(Arrays.asList(table2)))
            .build();
        reservationRepository.save(reservation2);

        MvcResult mvcResult = mockMvc.perform(patch(TABLE_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)//IMPORTANT, else unsupported media type error
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        RestaurantTableDto returnedTableDto = objectMapper.readValue(response.getContentAsString(), RestaurantTableDto.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(2, tableRepository.findAll().size());
        assertEquals(1, reservationRepository.findAll().size());
        assertAll(
            () -> assertEquals(savedTable.getId(), returnedTableDto.getId()),
            () -> assertEquals(savedTable.getTableNum(), returnedTableDto.getTableNum()),
            () -> assertEquals(false, returnedTableDto.getActive()),
            () -> assertEquals(savedTable.getPosDescription(), returnedTableDto.getPosDescription()),
            () -> assertEquals(savedTable.getSeatCount(), returnedTableDto.getSeatCount())
        );

        reservationRepository.deleteAll();//cleanup
    }

    @Test
    void givenOneTableWithReservationInPast_whenSetActiveFalseAsAdmin_thenRestaurantTableRepositoryAndReservationRepositoryUnchangedAndTableDeactivated() throws Exception {
        RestaurantTable savedTable = tableRepository.save(table);
        Reservation reservation = Reservation.ReservationBuilder.aReservation()
            .withGuestName(TEST_GUEST_FOR_VALID_RESERVATION)
            .withNumberOfGuests(4)
            .withStartDateTime(LocalDateTime.now().plusNanos(100000000L))
            .withEndDateTime(LocalDateTime.now().plusNanos(200000000L))
            .withTables(new HashSet<>(Arrays.asList(table)))
            .build();//create (very short) reservation
        reservationRepository.save(reservation);
        Thread.sleep(201L);// wait until reservation is (only just) in the past
        RestaurantTableStatusDto disabledTableDto = new RestaurantTableStatusDto();
        disabledTableDto.setActive(false);
        disabledTableDto.setId(savedTable.getId());
        String body = objectMapper.writeValueAsString(disabledTableDto);

        MvcResult mvcResult = mockMvc.perform(patch(TABLE_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)//IMPORTANT, else unsupported media type error
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        RestaurantTableDto returnedTableDto = objectMapper.readValue(response.getContentAsString(), RestaurantTableDto.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(1, tableRepository.findAll().size());
        assertEquals(1, reservationRepository.findAll().size());
        assertAll(
            () -> assertEquals(savedTable.getId(), returnedTableDto.getId()),
            () -> assertEquals(savedTable.getTableNum(), returnedTableDto.getTableNum()),
            () -> assertEquals(false, returnedTableDto.getActive()),
            () -> assertEquals(savedTable.getPosDescription(), returnedTableDto.getPosDescription()),
            () -> assertEquals(savedTable.getSeatCount(), returnedTableDto.getSeatCount())
        );

        reservationRepository.deleteAll();//cleanup
    }

    @Test
    void givenOneTableWithReservationInPast_whenDeleteByIdAsAdmin_thenRestaurantTableRepositoryAndReservationRepositoryEmpty() throws Exception {
        RestaurantTable savedTable = tableRepository.save(table);
        Reservation reservation = Reservation.ReservationBuilder.aReservation()
            .withGuestName(TEST_GUEST_FOR_VALID_RESERVATION)
            .withNumberOfGuests(4)
            .withStartDateTime(LocalDateTime.now().plusNanos(100000000L))
            .withEndDateTime(LocalDateTime.now().plusNanos(200000000L))
            .withTables(new HashSet<>(Arrays.asList(table)))
            .build();//create (very short) reservation
        reservationRepository.save(reservation);
        Thread.sleep(201L);// wait until reservation is (only just) in the past

        MvcResult mvcResult = mockMvc.perform(delete(TABLE_BASE_URI + "/" + savedTable.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        assertEquals(0, tableRepository.findAll().size());
        assertEquals(0, reservationRepository.findAll().size());

        reservationRepository.deleteAll();//cleanup
    }

    @Test
    void givenOneTable_whenSetActiveFalseAsUser_thenTableActiveFalse() throws Exception {
        RestaurantTable savedTable = tableRepository.save(table);
        RestaurantTableStatusDto disabledTableDto = new RestaurantTableStatusDto();
        disabledTableDto.setActive(false);
        disabledTableDto.setId(savedTable.getId());
        String body = objectMapper.writeValueAsString(disabledTableDto);

        MvcResult mvcResult = mockMvc.perform(patch(TABLE_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)//IMPORTANT, else unsupported media type error
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        RestaurantTableDto returnedTableDto = objectMapper.readValue(response.getContentAsString(), RestaurantTableDto.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(1, tableRepository.findAll().size());
        assertAll(
            () -> assertEquals(savedTable.getId(), returnedTableDto.getId()),
            () -> assertEquals(savedTable.getTableNum(), returnedTableDto.getTableNum()),
            () -> assertEquals(false, returnedTableDto.getActive()),
            () -> assertEquals(savedTable.getPosDescription(), returnedTableDto.getPosDescription()),
            () -> assertEquals(savedTable.getSeatCount(), returnedTableDto.getSeatCount())
        );
    }

    @Test
    void givenOneTable_whenCloneAsAdmin_thenSecondTableSavedAndReturnedWithSamePropertiesExceptIncrementedTableNum() throws Exception {
        RestaurantTable savedTable = tableRepository.save(table);

        MvcResult mvcResult = mockMvc.perform(post(TABLE_BASE_URI + "/clone/" + savedTable.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        RestaurantTableDto returnedTableDto = objectMapper.readValue(response.getContentAsString(), RestaurantTableDto.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(2, tableRepository.findAll().size());
        assertAll(
            () -> assertEquals(savedTable.getTableNum() + 1, returnedTableDto.getTableNum()),
            () -> assertEquals(savedTable.getActive(), returnedTableDto.getActive()),
            () -> assertEquals(savedTable.getPosDescription(), returnedTableDto.getPosDescription()),
            () -> assertEquals(savedTable.getSeatCount(), returnedTableDto.getSeatCount())
        );
    }

    @Test
    void givenTwoTablesWithConsecutiveTableNums_whenCloneAsAdmin_thenThirdTableSavedAndReturnedWithSamePropertiesAsFirstExceptIncrementedTableNumByTwo() throws Exception {
        RestaurantTable savedTable = tableRepository.save(table);
        RestaurantTable table2 = RestaurantTable.RestaurantTableBuilder.aTable()
            .withActive(true)
            .withPosDescription("lol")
            .withSeatCount(4)
            .withTableNum(table.getTableNum()+1)
            .build();
        RestaurantTable savedTable2 = tableRepository.save(table2);

        MvcResult mvcResult = mockMvc.perform(post(TABLE_BASE_URI + "/clone/" + savedTable.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        RestaurantTableDto returnedTableDto = objectMapper.readValue(response.getContentAsString(), RestaurantTableDto.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(3, tableRepository.findAll().size());
        assertAll(
            () -> assertEquals(savedTable.getTableNum() + 2, returnedTableDto.getTableNum()),
            () -> assertEquals(savedTable.getActive(), returnedTableDto.getActive()),
            () -> assertEquals(savedTable.getPosDescription(), returnedTableDto.getPosDescription()),
            () -> assertEquals(savedTable.getSeatCount(), returnedTableDto.getSeatCount())
        );
    }

    @Test
    void whenCloneWithInvalidIdAsAdmin_thenNotFoundException() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post(TABLE_BASE_URI + "/clone/" + 1)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Clone failed: No table with supplied ID"));
    }

    /*@Test
    void givenOneTableWithReservationInFuture_whenSetActiveFalseAsUser_thenNothingDeletedConflictResponseCodeAndAppropriateMessage() throws Exception {
        RestaurantTable savedTable = tableRepository.save(table);
        Reservation reservation = Reservation.ReservationBuilder.aReservation()
            .withGuestName(TEST_GUEST_FOR_VALID_RESERVATION)
            .withNumberOfGuests(table.getSeatCount())
            .withStartDateTime(TEST_START_DATE_TIME_FOR_VALID_RESERVATION)
            .withEndDateTime(TEST_END_DATE_TIME_FOR_VALID_RESERVATION)
            .withTables(new HashSet<>(Arrays.asList(table)))
            .build();
        Reservation savedReservation = reservationRepository.save(reservation);

        RestaurantTableStatusDto disabledTableDto = new RestaurantTableStatusDto();
        disabledTableDto.setActive(false);
        disabledTableDto.setId(savedTable.getId());
        String body = objectMapper.writeValueAsString(disabledTableDto);

        MvcResult mvcResult = mockMvc.perform(patch(TABLE_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)//IMPORTANT, else unsupported media type error
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("There are still bookings for this table in the future!"));
        assertEquals(1, tableRepository.findAll().size());
        assertEquals(savedTable, tableRepository.findById(disabledTableDto.getId()).get());//make sure table hasn't changed

        reservationRepository.deleteAll();//clean up
    }*/

    @Test
    void givenOneTableWithReservationInPast_whenSetActiveFalseAsUser_thenRestaurantTableRepositoryAndReservationRepositoryEmpty() throws Exception {
        RestaurantTable savedTable = tableRepository.save(table);
        Reservation reservation = Reservation.ReservationBuilder.aReservation()
            .withGuestName(TEST_GUEST_FOR_VALID_RESERVATION)
            .withNumberOfGuests(3)
            .withStartDateTime(LocalDateTime.now().plusNanos(100000000L))
            .withEndDateTime(LocalDateTime.now().plusNanos(200000000L))
            .withTables(new HashSet<>(Arrays.asList(table)))
            .build();//create (very short) reservation
        reservationRepository.save(reservation);
        Thread.sleep(201L);// wait until reservation is (only just) in the past

        RestaurantTableStatusDto disabledTableDto = new RestaurantTableStatusDto();
        disabledTableDto.setActive(false);
        disabledTableDto.setId(savedTable.getId());
        String body = objectMapper.writeValueAsString(disabledTableDto);

        MvcResult mvcResult = mockMvc.perform(patch(TABLE_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)//IMPORTANT, else unsupported media type error
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        RestaurantTableDto returnedTableDto = objectMapper.readValue(response.getContentAsString(), RestaurantTableDto.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(1, tableRepository.findAll().size());
        assertEquals(1, reservationRepository.findAll().size());
        assertAll(
            () -> assertEquals(savedTable.getId(), returnedTableDto.getId()),
            () -> assertEquals(savedTable.getTableNum(), returnedTableDto.getTableNum()),
            () -> assertEquals(false, returnedTableDto.getActive()),
            () -> assertEquals(savedTable.getPosDescription(), returnedTableDto.getPosDescription()),
            () -> assertEquals(savedTable.getSeatCount(), returnedTableDto.getSeatCount())
        );

        reservationRepository.deleteAll();//clean up
    }

    @Test
    void givenNothing_whenPostTableWithZeroSeats_thenBadRequestResponseCodeAndAppropriateValidationErrorMessage() throws Exception {
        table.setSeatCount(0);
        RestaurantTableDto inputTableDto = tableMapper.restaurantTableEntityToDto(table);
        String body = objectMapper.writeValueAsString(inputTableDto);

        MvcResult mvcResult = mockMvc.perform(post(TABLE_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)//IMPORTANT, else unsupported media type error
            .content(body)//forgot that too in first place
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("at least 1 required"));
    }

    @Test
    void givenNothing_whenPostTableWithInvalidTableNum_thenBadRequestResponseCodeAndAppropriateValidationErrorMessage() throws Exception {
        table.setTableNum(-69L);
        RestaurantTableDto inputTableDto = tableMapper.restaurantTableEntityToDto(table);
        String body = objectMapper.writeValueAsString(inputTableDto);

        MvcResult mvcResult = mockMvc.perform(post(TABLE_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)//IMPORTANT, else unsupported media type error
            .content(body)//forgot that too in first place
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("must be a positive integer"));
    }

    @Test
    void givenOneTableWithActiveTrue_whenPatchTableWithSameIdAndActiveFalse_thenGivenTableActiveFalse() throws Exception {
        table.setActive(true);
        RestaurantTable givenTable = tableRepository.save(table);

        RestaurantTableStatusDto deactivatedTable = new RestaurantTableStatusDto();
        deactivatedTable.setId(givenTable.getId());
        deactivatedTable.setActive(false);
        String body = objectMapper.writeValueAsString(deactivatedTable);

        MvcResult mvcResult = mockMvc.perform(patch(TABLE_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)//IMPORTANT, else unsupported media type error
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        RestaurantTableDto tableDto = objectMapper.readValue(response.getContentAsString(), RestaurantTableDto.class);

        assertEquals(false, tableDto.getActive());
    }

    @Test
    void givenOneTableWithCoords_whenPatchTableWithSameIdAndOtherCoords_thenTableCoordsChanged() throws Exception {
        table.setActive(true);
        table.setCenterCoordinates(new Point(4.5, 4.5));
        RestaurantTable givenTable = tableRepository.save(table);

        RestaurantTableCoordinatesDto movedTable = new RestaurantTableCoordinatesDto();
        movedTable.setId(givenTable.getId());
        PointDto newPos = new PointDto(6.9, 6.9);
        movedTable.setCenterCoordinates(newPos);
        String body = objectMapper.writeValueAsString(movedTable);

        MvcResult mvcResult = mockMvc.perform(patch(TABLE_BASE_URI + "/coordinates")
            .contentType(MediaType.APPLICATION_JSON)//IMPORTANT, else unsupported media type error
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        RestaurantTableDto tableDto = objectMapper.readValue(response.getContentAsString(), RestaurantTableDto.class);

        assertAll(
            () -> assertEquals(newPos.getX(), tableDto.getCenterCoordinates().getX()),
            () -> assertEquals(newPos.getY(), tableDto.getCenterCoordinates().getY())
        );
    }
}