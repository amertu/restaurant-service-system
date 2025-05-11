package com.spring.restaurant.backend.entity;


import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.Objects;
import java.util.Set;

@Entity
public class RestaurantTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(1)
    @Column(unique = true, nullable = false)
    private Long tableNum;//number defined by user (NOT the same as ID, which is generated from DB)

    @Min(1)
    @Column(name = "seat_count", nullable = false)
    private Integer seatCount;

    @Column(name = "pos_description")
    private String posDescription;

    @Column(nullable =  false)
    private Boolean active;

    @ManyToMany(mappedBy = "restaurantTables", cascade = CascadeType.REMOVE)
    private Set<Reservation> reservations;

    @OneToOne(cascade = {CascadeType.ALL})
    private Point centerCoordinates;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantTable restaurantTable = (RestaurantTable) o;
        return Objects.equals(id, restaurantTable.id) &&
            Objects.equals(tableNum, restaurantTable.tableNum) &&
            Objects.equals(seatCount, restaurantTable.seatCount) &&
            Objects.equals(posDescription, restaurantTable.posDescription) &&
            Objects.equals(centerCoordinates, restaurantTable.centerCoordinates) &&
            Objects.equals(active, restaurantTable.active);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, tableNum, seatCount, posDescription, active);
    }

    @Override
    public String toString() {
        return "RestaurantTable{" +
            "id=" + id +
            ", tableNum=" + tableNum +
            ", seatCount=" + seatCount +
            ", posDescription='" + posDescription + '\'' +
            ", active=" + active +
            '}';
    }

    public Point getCenterCoordinates() {
        return centerCoordinates;
    }

    public void setCenterCoordinates(Point centerCoordinates) {
        this.centerCoordinates = centerCoordinates;
    }

    public static final class RestaurantTableBuilder {
        private Long id;
        private Long tableNum;
        private Integer seatCount;
        private String posDescription;
        private Boolean active;

        private RestaurantTableBuilder() {
        }

        public static RestaurantTableBuilder aTable() {
            return new RestaurantTableBuilder();
        }

        public RestaurantTableBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public RestaurantTableBuilder withTableNum(Long tableNum) {
            this.tableNum = tableNum;
            return this;
        }

        public RestaurantTableBuilder withSeatCount(Integer seatCount) {
            this.seatCount = seatCount;
            return this;
        }

        public RestaurantTableBuilder withPosDescription(String posDescription) {
            this.posDescription = posDescription;
            return this;
        }

        public RestaurantTableBuilder withActive(Boolean active) {
            this.active = active;
            return this;
        }

        public RestaurantTable build() {
            RestaurantTable restaurantTable = new RestaurantTable();
            restaurantTable.setId(id);
            restaurantTable.setTableNum(tableNum);
            restaurantTable.setSeatCount(seatCount);
            restaurantTable.setPosDescription(posDescription);
            restaurantTable.setActive(active);
            return restaurantTable;
        }
    }

    //as we defined another constructor this one wouldn't exist without declaring it
    public RestaurantTable(){}

    //returns a deep copy of the table provided
    public RestaurantTable(RestaurantTable copiedTable) {
        this.id = copiedTable.id;
        this.tableNum = copiedTable.tableNum;
        this.posDescription = copiedTable.posDescription;
        this.seatCount = copiedTable.seatCount;
        this.active = copiedTable.active;
    }
}
