package com.zh.study.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;

@Aspect
public class RegisterAspect {

    @Pointcut("execution(public void com.zh.study.aspect.UserRegister.*(..))")
    public void pointCut() {}

    @Before("pointCut()")
    public void registerBefore() {
        System.out.println("Register before...");
    }

    @After("pointCut()")
    public void registerAfter() {
        System.out.println("Register after...");
    }

    @AfterReturning("pointCut()")
    public void registerAfterReturn() {
        System.out.println("Register afterReturning...");
    }

    @AfterThrowing("pointCut()")
    public void registerThrow() {
        System.out.println("Register afterThrowing...");
    }

    public void registerAround(JoinPoint joinPoint, Pointcut pointcut) {
        System.out.println("Register around...");
        System.out.println(joinPoint.getArgs());

    }

}
