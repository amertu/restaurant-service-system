package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.FloorLayoutDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.FloorLayout;
import org.mapstruct.Mapper;

@Mapper
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
