package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Dish;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.DishService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class DishServiceTest implements TestData {

    @Autowired
    private DishService dishService;

    @Test
    void givenNothing_whenAddDish_thenFindListWithOneElementAndReturnedDishEqualToInput() {
        Dish dish = Dish.DishBuilder.aDish()
            .withName(TEST_DISH_NAME)
            .withPrice(TEST_DISH_PRICE)
            .build();

        dishService.add(dish);

        assertAll(
            () -> assertEquals(1, dishService.findAll().size()),
            () -> assertEquals(dish.getId(), dishService.findAll().get(0).getId()),
            () -> assertEquals(dish.getName(), dishService.findAll().get(0).getName()),
            () -> assertEquals(dish.getPrice(), dishService.findAll().get(0).getPrice())
        );
    }

    @Test
    void givenNothing_whenDeleteDishWithInvalidId_thenThrowNotFoundException() throws NotFoundException {
        assertThrows(NotFoundException.class, () -> dishService.delete(-1L));
    }


    @Test
    void givenOneDish_whenDelete_thenListSizeSmallerByOneAndFindByOneThrowsNotFoundException() throws NotFoundException {
        Dish dish = Dish.DishBuilder.aDish()
            .withName(TEST_DISH_NAME)
            .withPrice(TEST_DISH_PRICE)
            .build();

        dish = dishService.add(dish);

        int size = dishService.findAll().size();
        long id = dish.getId();

        dishService.delete(id);

        assertAll(
            () -> assertEquals(size-1, dishService.findAll().size()),
            () -> assertThrows(NotFoundException.class, () -> dishService.findOne(id))
        );
    }


    @Test
    void givenNothing_whenUpdateDishWithInvalidId_thenThrowNotFoundException() throws NotFoundException {
        Dish dish = Dish.DishBuilder.aDish()
            .withId(-1L)
            .withName(TEST_DISH_NAME)
            .withPrice(TEST_DISH_PRICE)
            .build();

            assertThrows(NotFoundException.class, () -> dishService.update(dish));
    }


    @Test
    void givenOneDish_whenUpdateWithAllValuesChanged_thenReturnedDishEqualToInput() {
        Dish dish = Dish.DishBuilder.aDish()
            .withName(TEST_DISH_NAME)
            .withPrice(TEST_DISH_PRICE)
            .build();

        dish = dishService.add(dish);

        int size = dishService.findAll().size();

        Dish dishEdit = Dish.DishBuilder.aDish()
            .withId(dish.getId())
            .withName(TEST_DISH_NAME_EDIT)
            .withPrice(TEST_DISH_PRICE_EDIT)
            .build();

        dishService.update(dishEdit);

        assertAll(
            () -> assertEquals(size, dishService.findAll().size()),
            () -> assertEquals(dishEdit.getId(), dishService.findAll().get(size-1).getId()),
            () -> assertEquals(dishEdit.getName(), dishService.findAll().get(size-1).getName()),
            () -> assertEquals(dishEdit.getPrice(), dishService.findAll().get(size-1).getPrice())
        );
    }
}
