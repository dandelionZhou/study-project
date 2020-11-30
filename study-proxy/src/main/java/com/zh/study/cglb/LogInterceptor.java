package com.zh.study.cglb;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class LogInterceptor implements MethodInterceptor {
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("Register before write log...");
        //methodProxy.invokeSuper(o, objects);
        //methodProxy.invoke(o, objects); 调用invoke会导致死循环
        System.out.println("Register after write log...");
        return null;
    }
}
