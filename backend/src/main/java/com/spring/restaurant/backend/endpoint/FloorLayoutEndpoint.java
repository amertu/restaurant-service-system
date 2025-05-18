package com.spring.restaurant.backend.endpoint;

import com.spring.restaurant.backend.endpoint.dto.FloorLayoutDto;
import com.spring.restaurant.backend.endpoint.mapper.FloorLayoutMapper;
import com.spring.restaurant.backend.service.FloorLayoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/layout")
@Tag(name = "FloorLayout")
@Secured("ROLE_ADMIN")
@Slf4j
public class FloorLayoutEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String PATH = "/api/v1/layout";
    private final FloorLayoutService floorLayoutService;
    private final FloorLayoutMapper floorLayoutMapper;

    @Autowired
    public FloorLayoutEndpoint(FloorLayoutService floorLayoutService, FloorLayoutMapper floorLayoutMapper) {
        this.floorLayoutService = floorLayoutService;
        this.floorLayoutMapper = floorLayoutMapper;
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Get layout with ID")
    public FloorLayoutDto findOne(@PathVariable Long id) {
        LOGGER.info("GET " + PATH + "/{}", id);
        return floorLayoutMapper.floorLayoutEntityToFloorLayoutDto(floorLayoutService.findLayout(id));
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    @Operation(summary = "Add a new layout")
    public FloorLayoutDto add(@Valid @RequestBody FloorLayoutDto floorLayoutDto) {
        LOGGER.info("POST " + PATH);
        return floorLayoutMapper.floorLayoutEntityToFloorLayoutDto(
            floorLayoutService.add(floorLayoutMapper.floorLayoutDtoToFloorLayoutEntity(floorLayoutDto))
        );
    }
    @Secured("ROLE_USER")
    @PatchMapping
    @Operation(summary = "update layout")
    public FloorLayoutDto update(@Valid @RequestBody FloorLayoutDto floorLayoutDto) {
        LOGGER.info("PATCH " + PATH +" with id {}", floorLayoutDto.getId());
        return floorLayoutMapper.floorLayoutEntityToFloorLayoutDto(
            floorLayoutService.update(floorLayoutMapper.floorLayoutDtoToFloorLayoutEntity(floorLayoutDto))
        );
    }
}
