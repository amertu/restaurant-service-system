package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Dish;

import java.util.List;

public interface DishService {

    List<Dish> findAll();

    Dish findOne(Long id);

    Dish add(Dish dish);

    Dish update(Dish dish);

    void delete(Long id);

}
