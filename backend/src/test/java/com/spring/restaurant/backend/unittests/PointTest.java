package com.spring.restaurant.backend.unittests;

import com.spring.restaurant.backend.entity.Point;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.Assert.assertEquals;

@ActiveProfiles("test")
public class PointTest {

    @Test
    public void givenMultiplePoints_whenCalculating_EuclidianDistance_ExpectCorrectResults(){

        double epsilon = 0.001;

        // https://www.cut-the-knot.org/pythagoras/DistanceFormula.shtml
        Point point1 = new Point(2, -1);
        Point point2 = new Point(-2, 2);
        assertEquals(5, point1.getEuclidianDistanceTo(point2), epsilon);

        // https://www.calculatorsoup.com/calculators/geometry-plane/distance-two-points.php
        Point point3 = new Point(5, 6);
        Point point4 = new Point(-7,11);
        assertEquals(13, point4.getEuclidianDistanceTo(point3), epsilon);

    }


}
