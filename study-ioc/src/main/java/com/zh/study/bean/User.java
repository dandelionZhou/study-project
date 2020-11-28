package com.zh.study.bean;

import javax.annotation.PostConstruct;

public class User {
    private long id;
    private String name;

    private String value = "666";

    public User() {
        System.out.println("User -> constructor...");
    }

    /**
     * 在bean初始化完成后populateBean()之后
     */
    @PostConstruct
    public void init() {
        System.out.println("User -> PostConstruct");
    }




    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
