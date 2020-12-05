package com.zh.study;

import com.zh.study.aspect.UserRegister;
import com.zh.study.config.AopMainConfig;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AopMainConfig.class);


    @Test
    public void testAop() {
        UserRegister bean = (UserRegister) applicationContext.getBean("userRegister");
        bean.register("Spring", 18);
        //System.out.println(applicationContext.getBean("registerAspect"));
       /* String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String name : beanDefinitionNames) {
            System.out.println(applicationContext.getBean(name));
        }*/
    }
}
