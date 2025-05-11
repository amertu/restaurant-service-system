package com.spring.restaurant.backend.endpoint.mapper;


import com.spring.restaurant.backend.endpoint.dto.PointDto;
import com.spring.restaurant.backend.entity.Point;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PointMapper {

    PointDto pointEntityToDto(Point point);

    Point pointDtoToEntity(PointDto pointDto);
}
