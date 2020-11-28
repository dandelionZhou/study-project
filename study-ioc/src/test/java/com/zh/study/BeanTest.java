package com.zh.study;

import com.zh.study.bean.User;
import com.zh.study.config.MainConfig;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BeanTest {

    AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig.class);

    @Test
    public void testBeanProcessor() {
        User user = applicationContext.getBean(User.class);
        System.out.println(user);
    }

    @Test
    public void testBean() {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String name : beanNames) {
            System.out.println(name);
        }

    }


}
