package at.ac.tuwien.sepm.groupphase.backend.util;

import at.ac.tuwien.sepm.groupphase.backend.entity.RestaurantTable;

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
