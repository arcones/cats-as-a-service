package com.zooplus.cats.controller;

import com.google.gson.Gson;
import com.zooplus.cats.model.Cat;

class CatApiModel {

    private static final Gson gson = new Gson();

    final int id;
    private final String name;
    private final int age;
    private final int parentId;

    CatApiModel(Cat cat) {
        this.id = cat.getId();
        this.name = cat.getName();
        this.age = cat.getAge();
        this.parentId = cat.getParentId();
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}