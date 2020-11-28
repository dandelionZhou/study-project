package com.zh.study.config;

import com.zh.study.bean.User;
import com.zh.study.importSelector.BallImportBeanDefinitionRegistrar;
import com.zh.study.importSelector.BallImportSelector;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan("com.zh.study")
@Import({BallImportSelector.class, BallImportBeanDefinitionRegistrar.class})
public class MainConfig {

    @Bean
    @Lazy
    @Scope(value = "")
    public User user() {
        return new User();
    }
}
