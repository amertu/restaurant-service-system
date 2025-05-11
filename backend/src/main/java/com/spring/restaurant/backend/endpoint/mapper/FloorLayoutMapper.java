package com.spring.restaurant.backend.endpoint.mapper;

import com.spring.restaurant.backend.endpoint.dto.FloorLayoutDto;
import com.spring.restaurant.backend.entity.FloorLayout;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FloorLayoutMapper {

    /**
     * This method converts a FloorLayout Entity in a FloorLayout DTO.
     *
     * @param floorLayout the entity which has to be converted.
     * @return the converted DTO.
     */
    FloorLayoutDto floorLayoutEntityToFloorLayoutDto(FloorLayout floorLayout);

    /**
     * This method converts a FloorLayout DTO in a FloorLayout Entity.
     *
     * @param floorLayoutDto the DTO which has to be converted.
     * @return the converted Entity.
     */
    FloorLayout floorLayoutDtoToFloorLayoutEntity(FloorLayoutDto  floorLayoutDto);

}
