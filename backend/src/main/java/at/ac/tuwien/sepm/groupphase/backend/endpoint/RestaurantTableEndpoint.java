package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RestaurantTableCoordinatesDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RestaurantTableDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RestaurantTableStatusDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.RestaurantTableMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.RestaurantTable;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.RestaurantTableService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
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
    @ApiOperation(value = "Get list of tables", authorizations = {@Authorization(value = "apiKey")})
    public List<RestaurantTableDto> findAll() {
        LOGGER.info("GET " + PATH);
        return restaurantTableMapper.restaurantTableEntityToDto(restaurantTableService.findAll());
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Get a table by ID", authorizations = {@Authorization(value = "apiKey")})
    public RestaurantTableDto findOne(@PathVariable Long id) {
        LOGGER.info("GET " + PATH + "/{}", id);
        return restaurantTableMapper.restaurantTableEntityToDto(restaurantTableService.findOne(id));
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    @ApiOperation(value = "Add a new table", authorizations = {@Authorization(value = "apiKey")})
    public RestaurantTableDto add(@Valid @RequestBody RestaurantTableDto tableDto) {
        LOGGER.info("POST " + PATH + " message body: {}", tableDto);
        return restaurantTableMapper.restaurantTableEntityToDto(restaurantTableService.add(restaurantTableMapper.restaurantTableDtoToEntity(tableDto)));
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/clone/{id}")
    @ApiOperation(value = "Create a cloned version of table identified by supplied id and set its tableNum to the next available tableNum", authorizations = {@Authorization(value = "apiKey")})
    public RestaurantTableDto clone(@PathVariable Long id) {
        LOGGER.info("POST " + PATH + "/clone/{}", id);
        return restaurantTableMapper.restaurantTableEntityToDto(restaurantTableService.clone(id));
    }

    @Secured("ROLE_ADMIN")
    @PutMapping
    @ApiOperation(value = "Update an existing table", authorizations = {@Authorization(value = "apiKey")})
    public RestaurantTableDto update(@Valid @RequestBody RestaurantTableDto tableDto) {
        LOGGER.info("PUT " + PATH + " message body: {}", tableDto);
        return restaurantTableMapper.restaurantTableEntityToDto(restaurantTableService.update(restaurantTableMapper.restaurantTableDtoToEntity(tableDto)));
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Delete a table by id", authorizations = {@Authorization(value = "apiKey")})
    public void delete(@PathVariable Long id) {
        LOGGER.info("DELETE " + PATH + "/{}", id);
        restaurantTableService.delete(id);
    }

    @Secured("ROLE_USER")
    @PatchMapping
    @ApiOperation(value = "update a tables active status", authorizations = {@Authorization(value = "apiKey")})//TODO: change value maybe?
    public RestaurantTableDto setActive(@Valid @RequestBody RestaurantTableStatusDto partialUpdate) {
        LOGGER.info("PATCH " + PATH + "message body: " + partialUpdate);
        return restaurantTableMapper.restaurantTableEntityToDto(
            restaurantTableService.setActive(restaurantTableMapper.singleFieldRestaurantTableDtoToEntity(partialUpdate)));
    }

    @Secured("ROLE_ADMIN")
    @PatchMapping(value="/coordinates")
    @ApiOperation(value = "update a tables coordinates", authorizations = {@Authorization(value = "apiKey")})
    public RestaurantTableDto setCoordinates(@Valid @RequestBody RestaurantTableCoordinatesDto partialUpdate) {
        LOGGER.info("PATCH " + PATH + "/coordinates message body: " + partialUpdate);
        return restaurantTableMapper.restaurantTableEntityToDto(
            restaurantTableService.setCoordinates(restaurantTableMapper.restaurantTableCoordinatesDtoToEntity(partialUpdate)));
    }

    @Secured("ROLE_USER")
    @RequestMapping(
        params = { "numberOfGuests", "idOfReservationToIgnore", "startDateTime", "endDateTime" },
        method = GET
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Find tables for number of guests as a suggestion for where to place the guests..", authorizations = {@Authorization(value = "apiKey")})
    public List<RestaurantTableDto> findTableSuggestion(@RequestParam(value = "numberOfGuests") Integer numberOfGuests, @RequestParam(value="idOfReservationToIgnore") Long idOfReservationToIgnore, @RequestParam(value="startDateTime")  String startDateTime, @RequestParam(value="endDateTime")  String endDateTime){

        LOGGER.info("GET "+ PATH + "/");
        LOGGER.info("findTableSuggestion(.)");
        LOGGER.info("idOfReservationToIgnore" + idOfReservationToIgnore);
        LOGGER.info("numberOfGuests: " + numberOfGuests);
        LOGGER.info("startDateTime: " + startDateTime);
        LOGGER.info("endDateTime: " + endDateTime);

        LocalDateTime start = null;
        LocalDateTime end = null;

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
