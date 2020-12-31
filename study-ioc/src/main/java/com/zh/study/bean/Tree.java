package com.zh.study.bean;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @date 2020/12/29
 */
public class Tree {
    private Apple apple;

    public Apple getApple() {
        return apple;
    }

    @Autowired
    public void setApple(Apple apple) {
        this.apple = apple;
    }
}
