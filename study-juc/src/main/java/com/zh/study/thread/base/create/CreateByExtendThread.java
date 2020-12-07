package com.zh.study.thread.base.create;

/**
 * 继承Thread 创建线程
 * @create 2020-12-07
 */
public class CreateByExtendThread {

    static class TestThread extends Thread {
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + ": created by extends.");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + ": main start");

        Thread thread = new TestThread();
        thread.start();

        Thread.sleep(1000);
        System.out.println(Thread.currentThread().getName() + ": main over");
    }
}
