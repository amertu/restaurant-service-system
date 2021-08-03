package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.entity.Point;
import at.ac.tuwien.sepm.groupphase.backend.entity.RestaurantTable;
import at.ac.tuwien.sepm.groupphase.backend.util.TableComparatorByEuclidianDistanceToReferencePoint;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class TableComparatorByEuclidianDistanceToReferencePointTest {


    @Test
    public void givenMultipleTestInputs_Expect_CorrectResult(){

        // Arrange
        Point referencePoint = new Point(0,0);
        TableComparatorByEuclidianDistanceToReferencePoint comparator = new TableComparatorByEuclidianDistanceToReferencePoint(referencePoint);

        Point p1 = new Point(1,1);
        Point p2 = new Point( 4, 4);
        Point p3 = new Point( 4, 5);

        RestaurantTable t1 = RestaurantTable.RestaurantTableBuilder.aTable()
            .withId(1L)
            .withTableNum(100L)
            .withSeatCount(2)
            .withPosDescription("somewhere")
            .withActive(false)
            .build();

        RestaurantTable t2 = new RestaurantTable(t1);
        RestaurantTable t3 = new RestaurantTable(t1);

        t1.setTableNum(1L);
        t2.setTableNum(2L);
        t3.setTableNum(3L);
        t1.setCenterCoordinates(p1);
        t2.setCenterCoordinates(p2);
        t3.setCenterCoordinates(p3);

        List<RestaurantTable> tables = new ArrayList<RestaurantTable>();
        tables.add(t3);
        tables.add(t1);
        tables.add(t2);

        Assert.assertNotEquals(t1, t2);
        Assert.assertNotEquals(t1, t3);
        Assert.assertNotEquals(t2,t3);

        Assert.assertEquals(t3, tables.get(0));
        Assert.assertEquals(t1, tables.get(1));
        Assert.assertEquals(t2, tables.get(2));

        // Act
        tables.sort(comparator);

        // Assert
        Assert.assertEquals(t1, tables.get(0));
        Assert.assertEquals(t2, tables.get(1));
        Assert.assertEquals(t3, tables.get(2));

        // Arrange
        referencePoint = new Point(4,4);
        comparator = new TableComparatorByEuclidianDistanceToReferencePoint(referencePoint);

        // Act
        tables.sort(comparator);

        // Assert
        Assert.assertEquals(t2, tables.get(0));
        Assert.assertEquals(t3, tables.get(1));
        Assert.assertEquals(t1, tables.get(2));

        // Arrange
        referencePoint = new Point(4,5);
        comparator = new TableComparatorByEuclidianDistanceToReferencePoint(referencePoint);

        // Act
        tables.sort(comparator);

        // Assert
        Assert.assertEquals(t3, tables.get(0));
        Assert.assertEquals(t2, tables.get(1));
        Assert.assertEquals(t1, tables.get(2));


        // Arrange
        referencePoint = new Point(-7,-7);
        comparator = new TableComparatorByEuclidianDistanceToReferencePoint(referencePoint);

        // Act
        tables.sort(comparator);

        // Assert
        Assert.assertEquals(t1, tables.get(0));
        Assert.assertEquals(t2, tables.get(1));
        Assert.assertEquals(t3, tables.get(2));

    }

}
