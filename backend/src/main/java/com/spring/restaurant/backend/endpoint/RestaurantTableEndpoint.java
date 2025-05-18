package com.spring.restaurant.backend.endpoint;

import com.spring.restaurant.backend.endpoint.dto.RestaurantTableCoordinatesDto;
import com.spring.restaurant.backend.endpoint.dto.RestaurantTableDto;
import com.spring.restaurant.backend.endpoint.dto.RestaurantTableStatusDto;
import com.spring.restaurant.backend.endpoint.mapper.RestaurantTableMapper;
import com.spring.restaurant.backend.entity.RestaurantTable;
import com.spring.restaurant.backend.exception.ValidationException;
import com.spring.restaurant.backend.service.RestaurantTableService;
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
@RequestMapping(value = "/api/v1/tables")
@Tag(name = "Tables")
@Secured("ROLE_ADMIN")
@Slf4j
public class RestaurantTableEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String PATH = "/api/v1/tables";
    private final RestaurantTableService restaurantTableService;
    private final RestaurantTableMapper restaurantTableMapper;

    @Autowired
    public RestaurantTableEndpoint(RestaurantTableService restaurantTableService, RestaurantTableMapper restaurantTableMapper) {
        this.restaurantTableService = restaurantTableService;
        this.restaurantTableMapper = restaurantTableMapper;
    }

    @GetMapping
    @Operation(summary = "Get list of tables")
    public List<RestaurantTableDto> findAll() {
        LOGGER.info("GET " + PATH);
        return restaurantTableMapper.restaurantTableEntityToDto(restaurantTableService.findAll());
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Get a table by ID")
    public RestaurantTableDto findOne(@PathVariable Long id) {
        LOGGER.info("GET " + PATH + "/{}", id);
        return restaurantTableMapper.restaurantTableEntityToDto(restaurantTableService.findOne(id));
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    @Operation(summary = "Add a new table")
    public RestaurantTableDto add(@Valid @RequestBody RestaurantTableDto tableDto) {
        LOGGER.info("POST " + PATH + " message body: {}", tableDto);
        return restaurantTableMapper.restaurantTableEntityToDto(restaurantTableService.add(restaurantTableMapper.restaurantTableDtoToEntity(tableDto)));
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/clone/{id}")
    @Operation(summary = "Create a cloned version of table identified by supplied id and set its tableNum to the next available tableNum")
    public RestaurantTableDto clone(@PathVariable Long id) {
        LOGGER.info("POST " + PATH + "/clone/{}", id);
        return restaurantTableMapper.restaurantTableEntityToDto(restaurantTableService.clone(id));
    }

    @Secured("ROLE_ADMIN")
    @PutMapping
    @Operation(summary = "Update an existing table")
    public RestaurantTableDto update(@Valid @RequestBody RestaurantTableDto tableDto) {
        LOGGER.info("PUT " + PATH + " message body: {}", tableDto);
        return restaurantTableMapper.restaurantTableEntityToDto(restaurantTableService.update(restaurantTableMapper.restaurantTableDtoToEntity(tableDto)));
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete a table by id")
    public void delete(@PathVariable Long id) {
        LOGGER.info("DELETE " + PATH + "/{}", id);
        restaurantTableService.delete(id);
    }

    @Secured("ROLE_USER")
    @PatchMapping
    @Operation(summary = "update a tables active status")//TODO: change value maybe?
    public RestaurantTableDto setActive(@Valid @RequestBody RestaurantTableStatusDto partialUpdate) {
        LOGGER.info("PATCH " + PATH + "message body: {}", partialUpdate);
        return restaurantTableMapper.restaurantTableEntityToDto(
            restaurantTableService.setActive(restaurantTableMapper.singleFieldRestaurantTableDtoToEntity(partialUpdate)));
    }

    @Secured("ROLE_ADMIN")
    @PatchMapping(value="/coordinates")
    @Operation(summary = "Get all waiters")
    public RestaurantTableDto setCoordinates(@Valid @RequestBody RestaurantTableCoordinatesDto partialUpdate) {
        LOGGER.info("PATCH " + PATH + "/coordinates message body: {}", partialUpdate);
        return restaurantTableMapper.restaurantTableEntityToDto(
            restaurantTableService.setCoordinates(restaurantTableMapper.restaurantTableCoordinatesDtoToEntity(partialUpdate)));
    }

    @Secured("ROLE_USER")
    @RequestMapping(
        params = { "numberOfGuests", "idOfReservationToIgnore", "startDateTime", "endDateTime" },
        method = GET
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all waiters")
    public List<RestaurantTableDto> findTableSuggestion(@RequestParam(value = "numberOfGuests") Integer numberOfGuests, @RequestParam(value="idOfReservationToIgnore", required = false) Long idOfReservationToIgnore, @RequestParam(value="startDateTime")  String startDateTime, @RequestParam(value="endDateTime")  String endDateTime){

        LOGGER.info("GET "+ PATH + "/");
        LOGGER.info("findTableSuggestion(.)");
        LOGGER.info("idOfReservationToIgnore{}", idOfReservationToIgnore);
        LOGGER.info("numberOfGuests: {}", numberOfGuests);
        LOGGER.info("startDateTime: {}", startDateTime);
        LOGGER.info("endDateTime: {}", endDateTime);

        LocalDateTime start;
        LocalDateTime end;

        try{
            start = LocalDateTime.parse(startDateTime, DateTimeFormatter.ISO_DATE_TIME);
            end = LocalDateTime.parse(endDateTime, DateTimeFormatter.ISO_DATE_TIME);

        } catch( Exception ex){
            throw new ValidationException("Failed to parse DateTime.", ex);
        }

        List<RestaurantTable> suggestedTables = restaurantTableService.findTableSuggestion(numberOfGuests, idOfReservationToIgnore, start, end);
        return restaurantTableMapper.restaurantTableEntityToDto(suggestedTables);
    }



}
