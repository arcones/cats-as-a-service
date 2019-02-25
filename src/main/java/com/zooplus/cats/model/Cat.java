package com.zooplus.cats.model;

public class Cat {

    private final int id;
    private final String name;
    private final int age;
    private final Integer parentId;

    Cat(int id, String name, int age, Integer parentId) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.parentId = parentId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public Integer getParentId() {
        return parentId;
    }

    @Override
    public String toString() {
        return "Cat{" + "id=" + id + ", name='" + name + '\'' + ", age=" + age + ", parentId=" + parentId + '}';
    }
}
