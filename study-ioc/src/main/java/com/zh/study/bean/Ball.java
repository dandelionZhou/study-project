package com.zh.study.bean;

import org.springframework.context.Lifecycle;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

public class Ball {
    public Ball() {}

    @PostConstruct
    public void init() {
        System.out.println("Ball -> postConstruct");
    }
}