package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RestaurantTableCoordinatesDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RestaurantTableDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RestaurantTableStatusDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.RestaurantTable;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface RestaurantTableMapper {
    RestaurantTableDto restaurantTableEntityToDto(RestaurantTable restaurantTable);

    List<RestaurantTableDto> restaurantTableEntityToDto(List<RestaurantTable> restaurantTable);

    RestaurantTable restaurantTableDtoToEntity(RestaurantTableDto restaurantTableDto);

    List<RestaurantTable> restaurantTableDtoToEntity(List<RestaurantTableDto> restaurantTableDto);

    RestaurantTable singleFieldRestaurantTableDtoToEntity(RestaurantTableStatusDto restaurantTableStatusDto);

    RestaurantTable restaurantTableCoordinatesDtoToEntity(RestaurantTableCoordinatesDto restaurantTableCoordinatesDto);
}
