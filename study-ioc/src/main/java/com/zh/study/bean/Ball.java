package com.zh.study.bean;

import javax.annotation.PostConstruct;

public class Ball {
    public Ball() {}
    @PostConstruct
    public void init() {
        System.out.println("Ball -> postConstruct");
    }
}
