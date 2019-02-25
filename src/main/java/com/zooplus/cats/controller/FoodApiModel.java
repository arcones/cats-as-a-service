package com.zooplus.cats.controller;

import com.google.gson.Gson;
import com.zooplus.cats.model.Food;

class FoodApiModel {

    private static final Gson gson = new Gson();

    private final int id;
    private final int catId;
    private final String brand;

    FoodApiModel(Food food) {
        this.id = food.getId();
        this.catId = food.getCatId();
        this.brand = food.getBrand();
    }
    
    @Override
    public String toString() {
        return gson.toJson(this);
    }
}