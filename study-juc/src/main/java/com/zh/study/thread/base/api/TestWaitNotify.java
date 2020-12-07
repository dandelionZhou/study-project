package com.zh.study.thread.base.api;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 一个简单的生产者消费者模式展示了wait(),notify()的应用
 * @date 2020/12/7
 */
public class TestWaitNotify {
    private static List<String> resourceList = new ArrayList<>();

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {

            new Thread(() -> {
                synchronized (resourceList) {
                    while (resourceList.size() == 1) {
                        try {
                            resourceList.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    System.out.println(Thread.currentThread().getName() + ": Product");
                    resourceList.add("P");
                    resourceList.notify();
                }

            }).start();

        }

        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                synchronized (resourceList) {
                    while (resourceList.size() == 0) {
                        try {
                            resourceList.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    System.out.println(Thread.currentThread().getName() + ": Consumer");
                    resourceList.remove(0);
                    resourceList.notify();
                }
            }).start();
        }
    }
}
