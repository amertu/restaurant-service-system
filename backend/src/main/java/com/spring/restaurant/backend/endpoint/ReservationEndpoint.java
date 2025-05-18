package com.spring.restaurant.backend.endpoint;

import com.spring.restaurant.backend.endpoint.dto.ReservationDto;
import com.spring.restaurant.backend.endpoint.mapper.ReservationMapper;
import com.spring.restaurant.backend.entity.Reservation;
import com.spring.restaurant.backend.exception.ValidationException;
import com.spring.restaurant.backend.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(ReservationEndpoint.BASE_URL)
@Tag(name = "Reservation")
@Secured("ROLE_ADMIN")
@Slf4j
public class ReservationEndpoint {

    static final String BASE_URL = "/api/v1/reservations";

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;

    @Autowired
    public ReservationEndpoint(ReservationService reservationService, ReservationMapper reservationMapper) {
        this.reservationService = reservationService;
        this.reservationMapper = reservationMapper;
    }


    @Secured("ROLE_USER")
    @GetMapping
    @Operation(summary = "Get list of reservations without details")
    @ResponseStatus(HttpStatus.OK)
    public List<ReservationDto> findAll() {
        LOGGER.info("GET {}", BASE_URL);
        return reservationMapper.reservationToReservationDto(reservationService.findAll());
    }



    @Secured("ROLE_USER")
    @RequestMapping(
        params = { "startDateTime", "endDateTime" },
        method = GET
    )
    @Operation(summary = "Get list of reservations within given time range")
    @ResponseStatus(HttpStatus.OK)
    public List<ReservationDto> findByStartAndEndDateTime(@RequestParam(value="startDateTime")  String startDateTime, @RequestParam(value="endDateTime")  String endDateTime) {

        LOGGER.info("GET "+ BASE_URL);
        LOGGER.info("findByStartAndEndDateTime(.)");
        LOGGER.info("startDateTime: {}", startDateTime);
        LOGGER.info("endDateTIme: {}", endDateTime);

        LocalDateTime start = null;
        LocalDateTime end = null;

        try{
            start = LocalDateTime.parse(startDateTime, DateTimeFormatter.ISO_DATE_TIME);
            end = LocalDateTime.parse(endDateTime, DateTimeFormatter.ISO_DATE_TIME);

        } catch( Exception ex){
            throw new ValidationException("Failed to parse DateTime.", ex);
        }

        return reservationMapper.reservationToReservationDto(reservationService.findByStartAndEndDateTime(start, end));
    }


    @Secured("ROLE_USER")
    @GetMapping(value = "/{id}")
    @Operation(summary = "et a reservation by ID")
    public ReservationDto findOne(@PathVariable Long id) {
        LOGGER.info("GET " + BASE_URL + "/{}", id);
        return reservationMapper.reservationToReservationDto(reservationService.findOne(id));
    }


    @Secured("ROLE_USER")
    @PutMapping
    @Operation(summary = "Update an existing reservation")
    public ReservationDto update(@Valid @RequestBody ReservationDto reservationDto) {
        LOGGER.info("PUT " + BASE_URL + " message body: {}", reservationDto);
        Reservation reservation = reservationMapper.reservationDtoToReservation(reservationDto);
        return reservationMapper.reservationToReservationDto(reservationService.updateReservation(reservation));
    }

    @Secured("ROLE_USER")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Post a reservation")
    public ReservationDto createReservation(@RequestBody ReservationDto reservationDto) {

        LOGGER.info("POST " + BASE_URL);
        LOGGER.info(reservationDto.toString());
        Reservation reservationToCreate = reservationMapper.reservationDtoToReservation(reservationDto);
        Reservation createdReservation;

        createdReservation = reservationService.createReservation(reservationToCreate);
        return reservationMapper.reservationToReservationDto(createdReservation);
    }

    @Secured("ROLE_USER")
    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete a reservation by id")
    public void deleteReservationById(@PathVariable("id") Long id) {
        LOGGER.info("DELETE " + BASE_URL + "/{}", id);
        reservationService.deleteReservationById(id);
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/filter")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Filter reservations by params")
    public List<ReservationDto> search(@RequestParam(required = false) String guestName,
                                       @RequestParam(required = false) String startDateTime,
                                       @RequestParam(required = false) String endDateTime,
                                       @RequestParam(required = false) Long tableNum) {
        LOGGER.info("GET " + BASE_URL + "/filter"
            + "?guestName=" + guestName
            + "&startDateTime=" + startDateTime
            + "&endDateTime=" + endDateTime
            + "&tableNum=" + tableNum);

        List<Reservation> filteredReservations = reservationService.search(guestName, startDateTime, endDateTime, tableNum);
        return reservationMapper.reservationToReservationDto(filteredReservations);
    }
}
