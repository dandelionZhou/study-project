package com.zh.study.thread.base.api;

/**
 * Thread: yield
 * @date 2020/12/8
 */
public class TestThreadYield {
    public static void main(String[] args) throws InterruptedException {

        System.out.println(Thread.currentThread().getName() + ": main start...");
        Thread threadA = new Thread(() -> {

            for (int i = 0; i < 5; i++) {
                System.out.println(Thread.currentThread().getName() + ":" + i);
                if (i % 2 == 0) {
                    //线程A不调用yield()，它出现的频率应该是最高的
                    //Thread.yield();
                }
            }
        }, "ThreadA");

        Thread threadB = new Thread(() -> {

            for (int i = 0; i < 5; i++) {
                System.out.println(Thread.currentThread().getName() + ":" + i);
                if (i % 2 == 0) {
                    Thread.yield();
                }
            }
        }, "ThreadB");

        Thread threadC = new Thread(() -> {

            for (int i = 0; i < 5; i++) {
                System.out.println(Thread.currentThread().getName() + ":" + i);
                if (i % 2 == 0) {
                    Thread.yield();
                }
            }
        }, "ThreadC");

        threadA.start();
        threadB.start();
        threadC.start();
        Thread.sleep(1000);
        System.out.println(Thread.currentThread().getName() + ": main over...");
    }
}
