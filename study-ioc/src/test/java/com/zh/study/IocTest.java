package com.zh.study;

import com.zh.study.bean.Apple;
import com.zh.study.config.AppleConfig;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @date 2020/12/29
 */
public class IocTest {

    @Test
    public void test() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppleConfig.class);
        System.out.println(context.getBean(Apple.class).getTree());
    }
}
