package com.zooplus.cats.model;

public class Food {

    private final int id;
    private final int catId;
    private final String brand;

    public Food(int id, int catId, String brand) {
        this.id = id;
        this.catId = catId;
        this.brand = brand;
    }

    public int getId() {
        return id;
    }

    public int getCatId() {
        return catId;
    }

    public String getBrand() {
        return brand;
    }

    @Override
    public String toString() {
        return "Food{" + "id=" + id + ", catId=" + catId + ", brand='" + brand + '\'' + '}';
    }
}
