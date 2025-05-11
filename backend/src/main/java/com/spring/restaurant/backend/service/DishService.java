package com.spring.restaurant.backend.service;

import com.spring.restaurant.backend.entity.Dish;

import java.util.List;

public interface DishService {

    List<Dish> findAll();

    Dish findOne(Long id);

    Dish add(Dish dish);

    Dish update(Dish dish);

    void delete(Long id);

}
