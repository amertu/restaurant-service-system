package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DishDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Dish;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface DishMapper {

    DishDto dishEntityToDto(Dish dish);

    List<DishDto> dishEntityToDto(List<Dish> dish);

    Dish dishDtoToEntity(DishDto dishDto);

    /**
     * This method converts a list of dishDtoList in a list of entities
     * @param dishDtoList the list of dishDto
     * @return the list of dishes
     */
    List<Dish> dishesDtoToDishes(List<DishDto> dishDtoList);

}
