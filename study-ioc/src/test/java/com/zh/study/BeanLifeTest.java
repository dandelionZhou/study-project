package com.zh.study;

import com.zh.study.bean.BallFactoryBean;
import com.zh.study.config.MainConfig;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * Bean的生命周期：
 * 1.AbstractAutowireCapableBeanFactory.resolveBeforeInstantiation()
 *   判断容器中是否有InstantiationAwareBeanPostProcessors 的实例若有则调用applyBeanPostProcessorsBeforeInstantiation()
 * 2.AbstractAutowireCapableBeanFactory.createBean()
 * 3.AbstractAutowireCapableBeanFactory.doCreateBean()
 *  3.1.实例化Bean doCreateBean() -> createBeanInstance().getWrappedInstance()
 *  3.2applyMergedBeanDefinitionPostProcessors()
 *  Allow post-processors to modify the merged bean definition.
 *  3.3.populateBean() 设置bean的属性
 *   此方法中在设置属性之前，会调用容器中InstantiationAwareBeanPostProcessor.postProcessAfterInstantiation()方法
 *  3.4 initializeBean() 初始化bean
 *      3.4.1 invokeAwareMethods() 如果bean实现了BeanNameAware，BeanClassLoaderAware，BeanFactoryAware接口 则调用set*()方法
 *      3.4.2 applyBeanPostProcessorsBeforeInitialization()
 *          循环遍历容器中所有的BeanPostProcessor实例 并调用processor.postProcessBeforeInitialization(result, beanName);
 *      3.4.3 invokeInitMethods()
 *          3.4.3.1 如果bean实现了InitializingBean接口则调用InitializingBean.afterPropertiesSet方法
 *          3.4.3.2 调用指定的初始化方法@Bean(initMethod = *) 或 xml 中指定的init-method方法
 *      3.4.4 applyBeanPostProcessorsAfterInitialization()
 *          循环遍历容器中所有的BeanPostProcessor实例 并调用processor.postProcessAfterInitialization(result, beanName);
 */

public class BeanLifeTest {

    private AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig.class);
    @Test
    public void testBeanLife() {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String name : beanNames) {
            System.out.println(name);
        }

        Object ballBean = applicationContext.getBean("ballFactoryBean");
        System.out.println(ballBean);

        Object ballFactoryBean = applicationContext.getBean("&ballFactoryBean");
        System.out.println(ballFactoryBean);
    }
}
