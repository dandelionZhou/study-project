package com.zh.study.thread.base.api;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 一个简单的生产者消费者模式展示了wait(),notify()的应用
 *
 * 虚假唤醒：被挂起的线程没有经过其他线程的notify(),notifyAll()唤醒或被中断，等待超时，直接变成了可以运行的状态。
 *
 * @date 2020/12/7
 */
public class TestWaitNotify {
    private static List<String> resourceList = new ArrayList<>();

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {

            new Thread(() -> {
                synchronized (resourceList) {
                    //此处为什么用while而非if？
                    //防止虚假唤醒
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



        System.out.println(Thread.currentThread().getName() + ": main over...");
    }
}
