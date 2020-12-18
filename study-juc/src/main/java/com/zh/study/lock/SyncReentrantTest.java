package com.zh.study.lock;

/**
 * @date 2020/12/15
 */
public class SyncReentrantTest {

    public synchronized void printA() {
        System.out.println("A...");
    }

    public synchronized void printB() {
        System.out.println("B...");
        printA();
    }

    public static void main(String[] args) throws InterruptedException {
        SyncReentrantTest test = new SyncReentrantTest();
        new Thread(() -> {
            synchronized (SyncReentrantTest.class) {
                test.printB();
            }
        }, "Thread0").start();

        Thread.sleep(1000);
        System.out.println("main over...");
    }
}
