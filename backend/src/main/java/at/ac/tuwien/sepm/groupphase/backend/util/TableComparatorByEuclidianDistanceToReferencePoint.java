package at.ac.tuwien.sepm.groupphase.backend.util;

import at.ac.tuwien.sepm.groupphase.backend.entity.Point;
import at.ac.tuwien.sepm.groupphase.backend.entity.RestaurantTable;

import java.util.Comparator;

public class TableComparatorByEuclidianDistanceToReferencePoint implements Comparator<RestaurantTable> {

    private final double THRESHOLD = .0001;
    private Point referencePoint;

    public TableComparatorByEuclidianDistanceToReferencePoint(Point referencePoint){
        this.referencePoint = referencePoint;
    }

    @Override
    public int compare(RestaurantTable table1, RestaurantTable table2) {

        double table1ToReferencePoint = referencePoint.getEuclidianDistanceTo(table1.getCenterCoordinates());
        double table2ToReferencePoint = referencePoint.getEuclidianDistanceTo(table2.getCenterCoordinates());

        if( Math.abs(table1ToReferencePoint - table2ToReferencePoint) < THRESHOLD){
            // The distances are considered as equal.
            return 0;
        }

        if( table1ToReferencePoint < table2ToReferencePoint){
            return  -1;
        }

        return 1;
    }
}
