package com.spring.restaurant.backend.endpoint.dto;

import javax.validation.constraints.NotNull;
import java.util.Objects;

//used for updates of active field of table entity, so that not all fields of table have to be updated
public class RestaurantTableStatusDto {
    private Long id;

    @NotNull(message = "must be specified")
    private Boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantTableStatusDto that = (RestaurantTableStatusDto) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(active, that.active);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, active);
    }

    @Override
    public String toString() {
        return "RestaurantTableStatusDto{" +
            "id=" + id +
            ", active=" + active +
            '}';
    }
}
