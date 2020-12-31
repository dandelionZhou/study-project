package com.zh.study.thread.base;

import sun.misc.Unsafe;

import java.util.concurrent.locks.LockSupport;

public class ThreadTest01  {
    private static boolean ready;
    private static int num;
    public static void main(String[] args) {
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if (ready) {
                    System.out.println(num
                    );
                }
            }
        }).start();

        new Thread(() -> {

        }).start();
    }
}
