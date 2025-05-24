package com.spring.restaurant.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Point {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double x;
    private double y;

    public Point() {}

    public Point(double x, double y){
        this.x = x;
        this.y = y;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Point{" +
            "id=" + id +
            ", x=" + x +
            ", y=" + y +
            '}';
    }

    public double getEuclidianDistanceTo(Point other){
        return Math.sqrt( square(other.x-this.x)  + square(other.y - this.y)  );
    }

    public double square(double number){
        return number*number;
    }
}
