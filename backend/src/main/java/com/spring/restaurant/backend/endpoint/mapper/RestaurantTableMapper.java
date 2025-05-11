package com.spring.restaurant.backend.endpoint.mapper;


import com.spring.restaurant.backend.endpoint.dto.RestaurantTableCoordinatesDto;
import com.spring.restaurant.backend.endpoint.dto.RestaurantTableDto;
import com.spring.restaurant.backend.endpoint.dto.RestaurantTableStatusDto;
import com.spring.restaurant.backend.entity.RestaurantTable;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RestaurantTableMapper {
    RestaurantTableDto restaurantTableEntityToDto(RestaurantTable restaurantTable);

    List<RestaurantTableDto> restaurantTableEntityToDto(List<RestaurantTable> restaurantTable);

    RestaurantTable restaurantTableDtoToEntity(RestaurantTableDto restaurantTableDto);

    List<RestaurantTable> restaurantTableDtoToEntity(List<RestaurantTableDto> restaurantTableDto);

    RestaurantTable singleFieldRestaurantTableDtoToEntity(RestaurantTableStatusDto restaurantTableStatusDto);

    RestaurantTable restaurantTableCoordinatesDtoToEntity(RestaurantTableCoordinatesDto restaurantTableCoordinatesDto);
}
