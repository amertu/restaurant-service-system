package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

public class FloorLayoutDto {
    private Long id;

    @NotNull(message = "must be specified")
    @Size(max = 100000)
    private String serializedLayout;

    public void setId(Long id) {this.id = id;}

    public Long getId() {return id;}

    public void setSerializedLayout(String serializedLayout) {this.serializedLayout = serializedLayout;}

    public String getSerializedLayout() {return serializedLayout;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FloorLayoutDto that = (FloorLayoutDto) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(serializedLayout, that.serializedLayout);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, serializedLayout);
    }

    @Override
    public String toString() {
        return "FloorLayoutDto{" +
            "id=" + id +
            ", serializedLayout='" + serializedLayout + '\'' +
            '}';
    }
}
