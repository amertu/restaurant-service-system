package at.ac.tuwien.sepm.groupphase.backend.util;

import at.ac.tuwien.sepm.groupphase.backend.entity.RestaurantTable;

import java.util.List;

public interface TableSuggestionStrategy {

    /**
     * PRECONDITION: The total numbers of free tables is sufficient for the number of guests.
     * Returns a suggestion of tables to select for the guests.
     * @param numberOfGuests the number of guests.
     * @param freeTables the available tables.
     * @return A suggestion of tables to select for the guests.
     */
    List<RestaurantTable> getSuggestedTables(Integer numberOfGuests, List<RestaurantTable> freeTables);

}
