package com.zh.study.bean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class Ball {
    public Ball() {}
    @PostConstruct
    @PreDestroy
    public void init() {
        System.out.println("Ball -> postConstruct");
    }
}
