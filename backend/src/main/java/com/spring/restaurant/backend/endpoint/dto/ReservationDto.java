package com.spring.restaurant.backend.endpoint.dto;

import com.spring.restaurant.backend.entity.RestaurantTable;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

public class ReservationDto {

    private Long id;

    private String guestName;

    private Integer numberOfGuests;

    private String contactInformation;

    private String comment;

    public Integer getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(Integer numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public String getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(String contactInformation) {
        this.contactInformation = contactInformation;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    private LocalDateTime createdAt;

    Set<RestaurantTable> restaurantTables;

    public Long getId() {
        return id;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<RestaurantTable> getRestaurantTables() {
        return restaurantTables;
    }

    public void setRestaurantTables(Set<RestaurantTable> restaurantTables) {
        this.restaurantTables = restaurantTables;
    }

    @Override
    public String toString() {
        return "ReservationDto{" +
            "id=" + id +
            ", guestName='" + guestName + '\'' +
            ", numberOfGuests=" + numberOfGuests +
            ", contactInformation='" + contactInformation + '\'' +
            ", comment='" + comment + '\'' +
            ", startDateTime=" + startDateTime +
            ", endDateTime=" + endDateTime +
            ", createdAt=" + createdAt +
            ", restaurantTables=" + restaurantTables +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationDto that = (ReservationDto) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(guestName, that.guestName) &&
            Objects.equals(numberOfGuests, that.numberOfGuests) &&
            Objects.equals(contactInformation, that.contactInformation) &&
            Objects.equals(comment, that.comment) &&
            Objects.equals(startDateTime, that.startDateTime) &&
            Objects.equals(endDateTime, that.endDateTime) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(restaurantTables, that.restaurantTables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, guestName, numberOfGuests, contactInformation, comment, startDateTime, endDateTime, createdAt, restaurantTables);
    }



    public static final class ReservationDtoBuilder {
        private Long id;
        String guestName;

        private Integer numberOfGuests;

        private String contactInformation;

        private String comment;

        LocalDateTime startDateTime;
        LocalDateTime endDateTime;

        Set<RestaurantTable> restaurantTables;

        private ReservationDtoBuilder() {
        }

        public static ReservationDto.ReservationDtoBuilder aReservationDto() {
            return new ReservationDto.ReservationDtoBuilder();
        }

        public ReservationDto.ReservationDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ReservationDto.ReservationDtoBuilder withGuestName(String guestName) {
            this.guestName = guestName;
            return this;
        }

        public ReservationDto.ReservationDtoBuilder withNumberOfGuests(Integer numberOfGuests){
            this.numberOfGuests = numberOfGuests;
            return this;
        }

        public ReservationDto.ReservationDtoBuilder withContactInformation(String contactInformation){
            this.contactInformation = contactInformation;
            return this;
        }

        public ReservationDto.ReservationDtoBuilder withComment(String comment){
            this.comment = comment;
            return this;
        }

        public ReservationDto.ReservationDtoBuilder withStartDateTime(LocalDateTime startDateTime) {
            this.startDateTime = startDateTime;
            return this;
        }

        public ReservationDto.ReservationDtoBuilder withEndDateTime(LocalDateTime endDateTime) {
            this.endDateTime = endDateTime;
            return this;
        }

        public ReservationDto.ReservationDtoBuilder withRestaurantTables(Set<RestaurantTable> restaurantTables){
            this.restaurantTables = restaurantTables;
            return this;
        }

        public ReservationDto build() {
            ReservationDto reservationDto = new ReservationDto();
            reservationDto.setId(id);
            reservationDto.setGuestName(guestName);
            reservationDto.setNumberOfGuests(numberOfGuests);
            reservationDto.setContactInformation(contactInformation);
            reservationDto.setComment(comment);
            reservationDto.setStartDateTime(startDateTime);
            reservationDto.setEndDateTime(endDateTime);
            reservationDto.setRestaurantTables(restaurantTables);
            return reservationDto;
        }
    }

}
