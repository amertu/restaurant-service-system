package com.spring.restaurant.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Set;

@Entity
public class Dish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name must not be null")
    private String name;

    @Min(value = 0, message = "price should not be negative")
    private Long price;

    @Enumerated(EnumType.STRING)
    private Category category;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "dish")
    private Set<Purchase> purchase;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Set<Purchase> getPurchase() {
        return purchase;
    }

    public void setPurchase(Set<Purchase> purchase) {
        this.purchase = purchase;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return Objects.equals(id, dish.id) &&
            Objects.equals(name, dish.name) &&
            Objects.equals(price, dish.price) &&
            category == dish.category;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, category);
    }

    @Override
    public String toString() {
        return "Dish{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", price=" + price +
            ", category=" + category +
            '}';
    }

    public static final class DishBuilder {
        private Long id;
        private String name;
        private Long price;
        private Category category;

        private DishBuilder() {
        }

        public static DishBuilder aDish() {
            return new DishBuilder();
        }

        public DishBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public DishBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public DishBuilder withPrice(Long price) {
            this.price = price;
            return this;
        }

        public DishBuilder withCategory(Category category) {
            this.category = category;
            return this;
        }

        public Dish build() {
            Dish dish = new Dish();
            dish.setId(id);
            dish.setName(name);
            dish.setPrice(price);
            dish.setCategory(category);
            return dish;
        }
    }
}
