package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.FloorLayoutDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.FloorLayoutMapper;
import at.ac.tuwien.sepm.groupphase.backend.service.FloorLayoutService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/layout")
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
    @ApiOperation(value = "Get layout with ID", authorizations = {@Authorization(value = "apiKey")})
    public FloorLayoutDto findOne(@PathVariable Long id) {
        LOGGER.info("GET " + PATH + "/{}", id);
        return floorLayoutMapper.floorLayoutEntityToFloorLayoutDto(floorLayoutService.findLayout(id));
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    @ApiOperation(value = "Add a new layout", authorizations = {@Authorization(value = "apiKey")})
    public FloorLayoutDto add(@Valid @RequestBody FloorLayoutDto floorLayoutDto) {
        LOGGER.info("POST " + PATH);
        return floorLayoutMapper.floorLayoutEntityToFloorLayoutDto(
            floorLayoutService.add(floorLayoutMapper.floorLayoutDtoToFloorLayoutEntity(floorLayoutDto))
        );
    }
    @Secured("ROLE_USER")
    @PatchMapping
    @ApiOperation(value = "update layout", authorizations = {@Authorization(value = "apiKey")})
    public FloorLayoutDto update(@Valid @RequestBody FloorLayoutDto floorLayoutDto) {
        LOGGER.info("PATCH " + PATH +" with id {}", floorLayoutDto.getId());
        return floorLayoutMapper.floorLayoutEntityToFloorLayoutDto(
            floorLayoutService.update(floorLayoutMapper.floorLayoutDtoToFloorLayoutEntity(floorLayoutDto))
        );
    }
}
