package com.hosea.ochieng.kscps;

public class CarPark {
    private String name;
    private double x, y;
    private String description;

    public CarPark(String name, double x, double y, String description) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "CarPark{" +
                "name='" + name + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", description='" + description + '\'' +
                '}';
    }
}
