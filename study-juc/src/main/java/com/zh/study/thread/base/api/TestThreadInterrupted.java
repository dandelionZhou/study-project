package com.zh.study.thread.base.api;

/**
 * 线程中断是线程间的协作模式，通过设置线程的中断标志并不能直接终止该线程的执行，而是被中断的线程根据中断状态自行处理。
 * @date 2020/12/8
 */
public class TestThreadInterrupted {
    public static void main(String[] args) throws InterruptedException {

        Thread threadA = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println(Thread.currentThread().getName() + ": live.");
            }
        }, "ThreadA");

        threadA.start();

        Thread.sleep(1000);

        System.out.println("Main interrupt threadA");
        threadA.interrupt();

        threadA.join();
        System.out.println("main is over...");
    }
}
