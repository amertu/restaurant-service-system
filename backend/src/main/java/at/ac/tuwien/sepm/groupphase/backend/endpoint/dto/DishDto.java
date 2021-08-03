package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class DishDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name must not be null")
    private String name;

    @Min(value = 0, message="Price should not be negative")
    private Long price;

    private String category;

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DishDto dishDto = (DishDto) o;
        return Objects.equals(id, dishDto.id) &&
            Objects.equals(name, dishDto.name) &&
            Objects.equals(price, dishDto.price) &&
            Objects.equals(category, dishDto.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, category);
    }

    @Override
    public String toString() {
        return "DishDto{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", price=" + price +
            ", category='" + category + '\'' +
            '}';
    }

    public static final class DishDtoBuilder {
        private Long id;
        private String name;
        private Long price;
        private String category;

        private DishDtoBuilder() {
        }

        public static DishDtoBuilder aDishDto() {
            return new DishDtoBuilder();
        }

        public DishDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public DishDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public DishDtoBuilder withPrice(Long price) {
            this.price = price;
            return this;
        }

        public DishDtoBuilder withCategory(String category) {
            this.category = category;
            return this;
        }

        public DishDto build() {
            DishDto dish = new DishDto();
            dish.setId(id);
            dish.setName(name);
            dish.setPrice(price);
            dish.setCategory(category);
            return dish;
        }
    }
}
