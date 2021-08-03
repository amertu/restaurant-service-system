package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PointDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Point;

public interface PointMapper {

    PointDto pointEntityToDto(Point point);

    Point pointDtoToEntity(PointDto pointDto);
}
