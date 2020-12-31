package com.zh.study.config;

import com.zh.study.bean.Apple;
import com.zh.study.bean.Tree;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @date 2020/12/29
 */
@Configuration
public class AppleConfig {
    @Bean
    public Apple apple() {
        return new Apple();
    }

    @Bean
    public Tree tree() {
        return new Tree();
    }
}
