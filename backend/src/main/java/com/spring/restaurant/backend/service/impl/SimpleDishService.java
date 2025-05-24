package com.spring.restaurant.backend.service.impl;

import com.spring.restaurant.backend.entity.Dish;
import com.spring.restaurant.backend.exception.NotFoundException;
import com.spring.restaurant.backend.repository.DishRepository;
import com.spring.restaurant.backend.service.DishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

@Service
public class SimpleDishService implements DishService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final DishRepository dishRepository;

    public SimpleDishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    @Override
    public List<Dish> findAll() {
        LOGGER.debug("Find all dishes");
        return dishRepository.findAll();
    }

    @Override
    public Dish findOne(Long id) {
        LOGGER.debug("Find table with id {}", id);
        Optional<Dish> dish = dishRepository.findById(id);
        if (dish.isPresent()) {
            return dish.get();
        } else {
            throw new NotFoundException(String.format("Could not find dish with id={}", id));
        }
    }

    @Override
    public Dish add(Dish dish) {
        LOGGER.debug("Save dish");
        return dishRepository.save(dish);
    }

    @Override
    @Transactional
    public Dish update(Dish dish) {
        LOGGER.debug("Update dish");
        Optional<Dish> foundDish = dishRepository.findById(dish.getId());
        if (foundDish.isPresent()) {
            return dishRepository.save(dish);
        } else {
            throw new NotFoundException(String.format("Could not find dish with id={}", dish.getId()));
        }
    }

    @Override
    public void delete(Long id) {
        LOGGER.debug("Delete dish with id {}", id);
        Optional<Dish> foundDish = dishRepository.findById(id);
        if (foundDish.isPresent()) {
            dishRepository.deleteById(id);
        } else {
            throw new NotFoundException(String.format("Could not find dish with id={}", id));
        }

    }
}
