package com.spring.restaurant.backend.util;

import com.spring.restaurant.backend.entity.RestaurantTable;

import java.util.List;

public class Util {

    public static int calculateTotalNumberOfSeats(List<RestaurantTable> tables){

        int totalNumberOfSeats = 0;

        for(RestaurantTable t:tables){
            totalNumberOfSeats += t.getSeatCount();
        }

        return totalNumberOfSeats;
    }
}
