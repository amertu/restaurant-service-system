package com.spring.restaurant.backend.endpoint.mapper;

import com.spring.restaurant.backend.endpoint.dto.DishDto;
import com.spring.restaurant.backend.entity.Dish;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DishMapper {

    DishDto dishEntityToDto(Dish dish);

    List<DishDto> dishEntitiesToDto(List<Dish> dish);

    Dish dishDtoToEntity(DishDto dishDto);

    /**
     * This method converts a list of dishDtoList in a list of entities
     * @param dishDtoList the list of dishDto
     * @return the list of dishes
     */
    List<Dish> dishesDtoToDishes(List<DishDto> dishDtoList);

}
