package com.zh.study.bean;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component //FactoryBean注册组件
public class BallFactoryBean implements FactoryBean<Ball> {

    @Override
    public Ball getObject() throws Exception {
        return new Ball();
    }

    @Override
    public Class<?> getObjectType() {
        return Ball.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
