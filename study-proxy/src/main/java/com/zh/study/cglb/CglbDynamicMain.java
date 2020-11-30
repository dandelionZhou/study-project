package com.zh.study.cglb;

import net.sf.cglib.proxy.Enhancer;

public class CglbDynamicMain {
    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(RegisterController.class);
        enhancer.setCallback(new LogInterceptor());
        RegisterController registerController = (RegisterController) enhancer.create();
        registerController.register();
    }
}
