package com.zh.study.config;

import com.zh.study.aspect.RegisterAspect;
import com.zh.study.aspect.UserPlay;
import com.zh.study.aspect.UserRegister;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
/**
 * @import AspectJAutoProxyRegistrar 自定义注册
 *  AnnotationAwareAspectJAutoProxyCreator到容器中 beanName internalAutoProyCreator
 *
 */
public class AopMainConfig {

    @Bean
    public RegisterAspect registerAspect() {
        return new RegisterAspect();
    }

    @Bean
    public UserPlay userPlay() {return new UserPlay(); }

    @Bean
    public UserRegister userRegister() {
        return new UserRegister();
    }

}
