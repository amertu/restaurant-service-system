package com.spring.restaurant.backend.endpoint.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class RestaurantTableDto {
    private Long id;//id for persistence layer

    @Min(value = 1, message="must be a positive integer")
    @NotNull(message = "must be specified")
    private Long tableNum;//defined by user (NOT the same as ID, which is generated from DB); visible in frontend

    @Min(value = 1, message="at least 1 required")
    @NotNull(message = "must be specified")
    private Integer seatCount;

    private String posDescription;

    @NotNull(message = "must be specified")
    private Boolean active;

    private PointDto centerCoordinates;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(Integer seatCount) {
        this.seatCount = seatCount;
    }

    public String getPosDescription() {
        return posDescription;
    }

    public void setPosDescription(String posDescription) {
        this.posDescription = posDescription;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Long getTableNum() {
        return tableNum;
    }

    public void setTableNum(Long tableNum) {
        this.tableNum = tableNum;
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
        RestaurantTableDto that = (RestaurantTableDto) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(tableNum, that.tableNum) &&
            Objects.equals(seatCount, that.seatCount) &&
            Objects.equals(posDescription, that.posDescription) &&
            Objects.equals(active, that.active) &&
            Objects.equals(centerCoordinates, that.centerCoordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tableNum, seatCount, posDescription, active, centerCoordinates);
    }

    @Override
    public String toString() {
        return "RestaurantTableDto{" +
            "id=" + id +
            ", tableNum=" + tableNum +
            ", seatCount=" + seatCount +
            ", posDescription='" + posDescription + '\'' +
            ", active=" + active +
            ", centerCoordinates=" + centerCoordinates +
            '}';
    }
}
