package com.spring.restaurant.backend.endpoint.dto;

import java.util.Objects;

//used for updates of centerCoordinates field of table entity, so that not all fields of table have to be updated
public class RestaurantTableCoordinatesDto {
    private Long id;

    private PointDto centerCoordinates;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PointDto getCenterCoordinates() {
        return centerCoordinates;
    }

    public void setCenterCoordinates(PointDto centerCoordinates) {
        this.centerCoordinates = centerCoordinates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantTableCoordinatesDto that = (RestaurantTableCoordinatesDto) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(centerCoordinates, that.centerCoordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, centerCoordinates);
    }

    @Override
    public String toString() {
        return "RestaurantTableCoordinatesDto{" +
            "id=" + id +
            ", centerCoordinates=" + centerCoordinates +
            '}';
    }
}
