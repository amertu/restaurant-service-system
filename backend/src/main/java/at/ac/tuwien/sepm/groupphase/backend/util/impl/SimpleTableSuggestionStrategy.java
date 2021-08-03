package at.ac.tuwien.sepm.groupphase.backend.util.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Point;
import at.ac.tuwien.sepm.groupphase.backend.entity.RestaurantTable;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.util.TableComparatorByEuclidianDistanceToReferencePoint;
import at.ac.tuwien.sepm.groupphase.backend.util.TableSuggestionStrategy;

import java.util.ArrayList;
import java.util.List;



public class SimpleTableSuggestionStrategy implements TableSuggestionStrategy {


    @Override
    public List<RestaurantTable> getSuggestedTables(Integer numberOfGuests, List<RestaurantTable> freeTables) {

        Point startPositionForTableSearch = new Point(0,0);
        Integer remainingGuestsToBeSeated = numberOfGuests;
        List<RestaurantTable> alreadySelectedTables = new ArrayList<RestaurantTable>();
        return suggestedTablesConsidering(remainingGuestsToBeSeated, startPositionForTableSearch, freeTables, alreadySelectedTables);
    }

    private List<RestaurantTable> suggestedTablesConsidering(Integer remainingGuestsToBeSeated, Point startPositionForTableSearch, List<RestaurantTable> freeTables, List<RestaurantTable> alreadySelectedTables) {

        try{ // to suggest a single-table-selection, if possible
            if(noTablesSelectedYet(alreadySelectedTables)){
                // favour smallest table
                RestaurantTable sufficientSingleTable  = findSmallestSingleTableWithSufficientCapacity(remainingGuestsToBeSeated, startPositionForTableSearch, freeTables);
                alreadySelectedTables.add(sufficientSingleTable);
                return alreadySelectedTables;
            }else{
                // favour closest table in multi-table selection (even if this means to lose seats)
                RestaurantTable sufficientSingleTable  = findClosestSingleTableWithSufficientCapacity(remainingGuestsToBeSeated, startPositionForTableSearch, freeTables);
                alreadySelectedTables.add(sufficientSingleTable);
                return alreadySelectedTables;
            }

        }catch (NotFoundException ex){
            // start multi-table-selection with the table with max seat count
            RestaurantTable tableWithMaxSeatCount = findTableWithMaxSeatCount(remainingGuestsToBeSeated, startPositionForTableSearch, freeTables);
            alreadySelectedTables.add(tableWithMaxSeatCount);
            freeTables.remove(tableWithMaxSeatCount);
            remainingGuestsToBeSeated -= tableWithMaxSeatCount.getSeatCount();
            return suggestedTablesConsidering(remainingGuestsToBeSeated, tableWithMaxSeatCount.getCenterCoordinates(), freeTables, alreadySelectedTables);
        }
    }

    private RestaurantTable findClosestSingleTableWithSufficientCapacity(Integer remainingGuestsToBeSeated, Point startPositionForTableSearch, List<RestaurantTable> freeTables) {

        freeTables.sort(new TableComparatorByEuclidianDistanceToReferencePoint(startPositionForTableSearch));

        RestaurantTable selectedTable = null;
        Integer minSeatCount = Integer.MAX_VALUE;

        for(RestaurantTable t:freeTables){
            int seatCount = t.getSeatCount();
            if(noTableWasFoundYet(selectedTable)){

                if(seatCount >= remainingGuestsToBeSeated && seatCount < minSeatCount){
                    selectedTable = t;
                    minSeatCount = selectedTable.getSeatCount();
                }

            }else{

                if( distanceToStartPositionEquals(t, selectedTable, startPositionForTableSearch)
                    && seatCount >= remainingGuestsToBeSeated && seatCount < minSeatCount){
                    return t;
                }else{
                    return  selectedTable;
                }

            }

        }

        if( null == selectedTable){
            throw new NotFoundException();
        }else{
            return selectedTable;
        }
    }

    private boolean distanceToStartPositionEquals(RestaurantTable nextTable, RestaurantTable selectedTable, Point startPositionForTableSearch) {
        TableComparatorByEuclidianDistanceToReferencePoint comparator = new TableComparatorByEuclidianDistanceToReferencePoint(startPositionForTableSearch);
        int comparisonResult = comparator.compare(nextTable, selectedTable);
        return 0 == comparisonResult ? true: false;
    }

    private boolean noTableWasFoundYet(RestaurantTable selectedTable) {
        return null == selectedTable;
    }

    private boolean noTablesSelectedYet(List<RestaurantTable> alreadySelectedTables) {
        return  alreadySelectedTables.isEmpty();
    }

    private RestaurantTable findTableWithMaxSeatCount(Integer remainingGuestsToBeSeated, Point startPositionForTableSearch, List<RestaurantTable> freeTables) {

        freeTables.sort(new TableComparatorByEuclidianDistanceToReferencePoint(startPositionForTableSearch));

        RestaurantTable selectedTable = null;
        Integer maxSeatCount = Integer.MIN_VALUE;

        for(RestaurantTable t:freeTables){
            if(t.getSeatCount() > maxSeatCount){
                selectedTable = t;
                maxSeatCount = selectedTable.getSeatCount();
            }
        }

        if( null == selectedTable){
            throw new NotFoundException("Failure while finding biggest restaurant table.");
        }else{
            return selectedTable;
        }

    }

    private RestaurantTable findSmallestSingleTableWithSufficientCapacity(Integer remainingGuestsToBeSeated, Point startPositionForTableSearch, List<RestaurantTable> freeTables) {

        freeTables.sort(new TableComparatorByEuclidianDistanceToReferencePoint(startPositionForTableSearch));

        RestaurantTable selectedTable = null;
        Integer minSeatCount = Integer.MAX_VALUE;

        for(RestaurantTable t:freeTables){
            int seatCount = t.getSeatCount();
            if(seatCount >= remainingGuestsToBeSeated && seatCount < minSeatCount){
                selectedTable = t;
                minSeatCount = selectedTable.getSeatCount();
            }
        }

        if( null == selectedTable){
            throw new NotFoundException();
        }else{
            return selectedTable;
        }

    }


}

