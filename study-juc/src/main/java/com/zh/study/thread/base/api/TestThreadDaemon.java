package com.zh.study.thread.base.api;

/**
 * 守护线程：将线程设置为守护线程后，JVM会在用户线程执行结束后直接退出，不会管守护线程执行到哪。
 * 用户线程：JVM进程会等到用户线程执行完毕后才退出。
 * @date 2020/12/8
 */
public class TestThreadDaemon {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //System.out.println(Thread.currentThread().getName() + ": " + i);
            }
            System.out.println(Thread.currentThread().getName() + ": over...");
        }, "ThreadDaemon");


        //未设置为守护线程前，JVM进程会等thread执行完毕后才退出
        //设置为守护线程后，JVM进程将在主线程执行完后直接退出，将不会等thread执行完
        //设置守护线程需要放到start()之前，否则会抛出IllegalThreadStateException
        thread.setDaemon(true);
        thread.start();

        Thread.sleep(1000);
        System.out.println(Thread.currentThread().getName() + ": over.");
    }
}
