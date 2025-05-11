package com.spring.restaurant.backend.endpoint;

import com.spring.restaurant.backend.endpoint.dto.DishDto;
import com.spring.restaurant.backend.endpoint.mapper.DishMapper;
import com.spring.restaurant.backend.service.DishService;
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
import java.util.List;

@RestController
@RequestMapping(DishEndpoint.BASE_URL)
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
    @ApiOperation(value = "Get list of dishes", authorizations = {@Authorization(value = "apiKey")})
    public List<DishDto> findAll() {
        LOGGER.info("GET " + BASE_URL);
        return dishMapper.dishEntitiesToDto(dishService.findAll());
    }

    @Secured({"ROLE_ADMIN","ROLE_USER"})
    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Get a dish by ID", authorizations = {@Authorization(value = "apiKey")})
    public DishDto findOne(@PathVariable Long id) {
        LOGGER.info("GET " + BASE_URL + "/{}", id);
        return dishMapper.dishEntityToDto(dishService.findOne(id));
    }

    @Secured({"ROLE_ADMIN","ROLE_USER"})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Add a new dish", authorizations = {@Authorization(value = "apiKey")})
    public DishDto add(@Valid @RequestBody DishDto dishDto) {
        LOGGER.info("POST " + BASE_URL + " message body: {}", dishDto);
        return dishMapper.dishEntityToDto(dishService.add(dishMapper.dishDtoToEntity(dishDto)));
    }

    @Secured({"ROLE_ADMIN","ROLE_USER"})
    @PutMapping
    @ApiOperation(value = "Update an existing dish", authorizations = {@Authorization(value = "apiKey")})
    public DishDto update(@Valid @RequestBody DishDto dishDto) {
        LOGGER.info("PUT " + BASE_URL + " message body: {}", dishDto);
        return dishMapper.dishEntityToDto(dishService.update(dishMapper.dishDtoToEntity(dishDto)));
    }

    @Secured({"ROLE_ADMIN","ROLE_USER"})
    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Delete a dish by id", authorizations = {@Authorization(value = "apiKey")})
    public void delete(@PathVariable Long id) {
        LOGGER.info("DELETE " + BASE_URL + "/{}", id);
        dishService.delete(id);
    }
}
