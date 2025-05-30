package com.spring.restaurant.backend.unittests;

import com.spring.restaurant.backend.basetest.TestData;
import com.spring.restaurant.backend.entity.Dish;
import com.spring.restaurant.backend.repository.DishRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class DishRepositoryTest implements TestData {

    @Autowired
    private DishRepository dishRepository;

    @Test
    public void givenNothing_whenSaveDish_thenFindListWithOneElementAndFindDishById() {
        Dish dish = Dish.DishBuilder.aDish()
            .withName(TEST_DISH_NAME)
            .withPrice(TEST_DISH_PRICE)
            .build();

        dishRepository.save(dish);

        assertAll(
            () -> assertEquals(1, dishRepository.findAll().size()),
            () -> assertNotNull(dishRepository.findById(dish.getId()))
        );
    }
}
