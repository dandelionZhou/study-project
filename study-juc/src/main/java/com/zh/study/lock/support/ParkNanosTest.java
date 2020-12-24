package com.zh.study.lock.support;

import java.util.concurrent.locks.LockSupport;

/**
 * @date 2020/12/21
 */
public class ParkNanosTest {
    public static void main(String[] args) {
        System.out.println(Thread.currentThread().getName());
        Thread threadA = new Thread(() -> {
            LockSupport.parkNanos(2000000);
            System.out.println(Thread.currentThread().getName());
        }, "ThreadA");

        threadA.start();
        System.out.println(Thread.currentThread().getName());
    }
}
