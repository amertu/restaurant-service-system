package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DishDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.DishMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Dish;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class DishMappingTest implements TestData {

    private final Dish dish = Dish.DishBuilder.aDish()
        .withId(ID)
        .withName(TEST_DISH_NAME)
        .withPrice(TEST_DISH_PRICE)
        .build();

    private final DishDto dishDto = DishDto.DishDtoBuilder.aDishDto()
        .withId(ID)
        .withName(TEST_DISH_NAME)
        .withPrice(TEST_DISH_PRICE)
        .build();

    @Autowired
    private DishMapper dishMapper;

    @Test
    public void givenNothing_whenDishEntityToDto_thenDtoHasAllProperties() {
        DishDto dishDto = dishMapper.dishEntityToDto(dish);
        assertAll(
            () -> assertEquals(ID, dishDto.getId()),
            () -> assertEquals(TEST_DISH_NAME, dishDto.getName()),
            () -> assertEquals(TEST_DISH_PRICE, dishDto.getPrice())
        );
    }

    @Test
    public void givenNothing_whenDishDtoToEntity_thenEntityHasAllProperties() {
        Dish dish = dishMapper.dishDtoToEntity(dishDto);
        assertAll(
            () -> assertEquals(ID, dish.getId()),
            () -> assertEquals(TEST_DISH_NAME, dish.getName()),
            () -> assertEquals(TEST_DISH_PRICE, dish.getPrice())
        );
    }

    @Test
    public void givenNothing_whenListWithTwoDishesEntitiesToDto_thenGetListWithSizeTwoAndAllProperties() {
        List<Dish> dishes = new ArrayList<>();
        dishes.add(dish);
        dishes.add(dish);

        List<DishDto> dishDtos = dishMapper.dishEntityToDto(dishes);
        assertEquals(2, dishDtos.size());
        assertAll(
            () -> assertEquals(ID, dishDtos.get(0).getId()),
            () -> assertEquals(TEST_DISH_NAME, dishDtos.get(0).getName()),
            () -> assertEquals(TEST_DISH_PRICE, dishDtos.get(0).getPrice()),
            () -> assertEquals(ID, dishDtos.get(1).getId()),
            () -> assertEquals(TEST_DISH_NAME, dishDtos.get(1).getName()),
            () -> assertEquals(TEST_DISH_PRICE, dishDtos.get(1).getPrice())
        );
    }
}
