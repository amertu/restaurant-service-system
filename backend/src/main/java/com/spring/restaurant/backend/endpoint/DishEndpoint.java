package com.spring.restaurant.backend.endpoint;

import com.spring.restaurant.backend.endpoint.dto.DishDto;
import com.spring.restaurant.backend.endpoint.mapper.DishMapper;
import com.spring.restaurant.backend.service.DishService;
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
import java.util.List;

@RestController
@RequestMapping(DishEndpoint.BASE_URL)
@Tag(name = "Dishes")
@Secured("ROLE_ADMIN")
@Slf4j
public class DishEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final String BASE_URL = "/api/v1/dishes";
    private final DishService dishService;
    private final DishMapper dishMapper;

    @Autowired
    public DishEndpoint(DishService dishService, DishMapper dishMapper) {
        this.dishService = dishService;
        this.dishMapper = dishMapper;
    }

    @Secured({"ROLE_ADMIN","ROLE_USER"})
    @GetMapping
    @Operation(summary = "Get list of dishes")
    public List<DishDto> findAll() {
        LOGGER.info("GET " + BASE_URL);
        return dishMapper.dishEntitiesToDto(dishService.findAll());
    }

    @Secured({"ROLE_ADMIN","ROLE_USER"})
    @GetMapping(value = "/{id}")
    @Operation(summary = "Get a dish by ID")
    public DishDto findOne(@PathVariable Long id) {
        LOGGER.info("GET " + BASE_URL + "/{}", id);
        return dishMapper.dishEntityToDto(dishService.findOne(id));
    }

    @Secured({"ROLE_ADMIN","ROLE_USER"})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a new dish")
    public DishDto add(@Valid @RequestBody DishDto dishDto) {
        LOGGER.info("POST " + BASE_URL + " message body: {}", dishDto);
        return dishMapper.dishEntityToDto(dishService.add(dishMapper.dishDtoToEntity(dishDto)));
    }

    @Secured({"ROLE_ADMIN","ROLE_USER"})
    @PutMapping
    @Operation(summary = "Update an existing dish")
    public DishDto update(@Valid @RequestBody DishDto dishDto) {
        LOGGER.info("PUT " + BASE_URL + " message body: {}", dishDto);
        return dishMapper.dishEntityToDto(dishService.update(dishMapper.dishDtoToEntity(dishDto)));
    }

    @Secured({"ROLE_ADMIN","ROLE_USER"})
    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete a dish by id")
    public void delete(@PathVariable Long id) {
        LOGGER.info("DELETE " + BASE_URL + "/{}", id);
        dishService.delete(id);
    }
}
