package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

public class PointDto {

    public PointDto() {}

    public PointDto(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    private Long id;

    private double x;
    private double y;

    @Override
    public String toString() {
        return "PointDto{" +
            "id=" + id +
            ", x=" + x +
            ", y=" + y +
            '}';
    }
}
