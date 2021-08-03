package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
public class FloorLayout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100000, columnDefinition = "LONGTEXT", nullable = false)
    @Size(max = 100000)
    private String serializedLayout;

    public FloorLayout() {  }

    public FloorLayout(Long id, String serializedLayout){
        this.id = id;
        this.serializedLayout = serializedLayout;
    }

    public void setId(Long id) {this.id = id;}

    public Long getId() {return id;}

    public void setSerializedLayout(String serializedLayout) {this.serializedLayout = serializedLayout;}

    public String getSerializedLayout() {return serializedLayout;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FloorLayout that = (FloorLayout) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(serializedLayout, that.serializedLayout);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, serializedLayout);
    }

    @Override
    public String toString() {
        return "FloorLayout{" +
            "id=" + id +
            ", serializedLayout='" + serializedLayout + '\'' +
            '}';
    }

    public static final class FloorLayoutBuilder {
        private Long id;
        private String serializedLayout;

        public FloorLayoutBuilder() {

        }
        public static FloorLayoutBuilder aLayout() {return new FloorLayoutBuilder();}

        public FloorLayoutBuilder withId(Long id) {
            this.id = id;
            return this;
        }
        public FloorLayoutBuilder withSerializedLayout(String serializedLayout) {
            this.serializedLayout = serializedLayout;
            return this;
        }

        public FloorLayout buildFloorLayout() {
            FloorLayout floorLayout = new FloorLayout();
            floorLayout.setId(id);
            floorLayout.setSerializedLayout(serializedLayout);
            return floorLayout;
        }
    }


}
