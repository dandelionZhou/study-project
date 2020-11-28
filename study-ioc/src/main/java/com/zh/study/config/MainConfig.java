package com.zh.study.config;

import com.zh.study.bean.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.zh.study")
public class MainConfig {

    @Bean
    public User user() {
        return new User();
    }
}
