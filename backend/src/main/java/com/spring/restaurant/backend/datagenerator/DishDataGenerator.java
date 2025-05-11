package com.spring.restaurant.backend.datagenerator;

import com.spring.restaurant.backend.entity.Category;
import com.spring.restaurant.backend.entity.Dish;
import com.spring.restaurant.backend.repository.DishRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;

@Profile("generateData")
@Component
public class DishDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_DISHES_TO_GENERATE = 15;

    private final DishRepository dishRepository;

    public DishDataGenerator(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    @PostConstruct
    private void generateDish() {
        if (!dishRepository.findAll().isEmpty()) {
            LOGGER.debug("dishes already generated");
        } else {
            LOGGER.debug("generating {} entries for dishes", NUMBER_OF_DISHES_TO_GENERATE);
            for (int i = 0; i < NUMBER_OF_DISHES_TO_GENERATE; i++) {
                Dish dish = Dish.DishBuilder.aDish()
                    .withName(generateName(i))
                    .withPrice(generatePrice(i))
                    .withCategory(generateCategory(i))
                    .build();
                LOGGER.debug("saving dish {} to dishes", dish);
                dishRepository.save(dish);
            }
        }
    }

    private String generateName(int i) {
        switch (i) {
            case 0:
                return "Noodle Soup";
            case 1:
                return "Green Salad";
            case 2:
                return "Cheese Sandwich";
            case 3:
                return "Toast";
            case 4:
                return "Spaghetti";
            case 5:
                return "Goulash with Bread";
            case 6:
                return "Hamburger";
            case 7:
                return "French Fries";
            case 8:
                return "Ketchup";
            case 9:
                return "Menu 1 (Noodle Soup + Cheese Sandwich)";
            case 10:
                return "Menu 2 (2x Toast + French Fries + Ketchup)";
            case 11:
                return "Slice of Ring Cake";
            case 12:
                return "Water";
            case 13:
                return "Cola 0.5l";
            case 14:
                return "Wine 0.25l";
            default:
                return "Random Food";
        }
    }

    private Long generatePrice(int i) {
        switch (i) {
            case 0:
                return 220L;
            case 1:
                return 160L;
            case 2:
                return 290L;
            case 3:
                return 320L;
            case 4:
                return 500L;
            case 5:
                return 540L;
            case 6:
                return 660L;
            case 7:
                return 350L;
            case 8:
                return 40L;
            case 9:
                return 490L;
            case 10:
                return 880L;
            case 11:
                return 280L;
            case 12:
                return 0L;
            case 13:
                return 180L;
            case 14:
                return 450L;
            default:
                return 1000L;
        }
    }

    private Category generateCategory(int i) {
        switch (i) {
            case 12:
            case 13:
            case 14:
                return Category.valueOf("DRINK");
            default:
                return Category.valueOf("FOOD");
        }
    }
}
