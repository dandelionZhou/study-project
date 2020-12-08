package com.zh.study.thread.base.api;

/**
 * Thread： join() 等待线程执行终止
 * @date 2020/12/8
 */
public class TestThreadJoin {
    public static void main(String[] args) throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + ": main start...");

        Thread threadA = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + ": work start...");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + ": work over...");
        }, "ThreadA");

        Thread threadB = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + ": work start...");
            /**
             * 在threadA调用join()方法后，如果其他线程调用了它的interrupt()方法，则会抛出InterruptedException异常
             */
            //threadA.interrupt();
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + ": work over...");
        }, "ThreadB");

        threadA.start();
        threadB.start();

        /**
         * 未使用join()方法前，可能threadA,threadB还未完成工作，主线程就执行完了
         * main: main start...
         * ThreadA: work start...
         * ThreadB: work start...
         * main: main over...
         * ThreadA: work over...
         * ThreadB: work over...
         */
        threadA.join();
        threadB.join();
        /**
         * main: main start...
         * ThreadA: work start...
         * ThreadB: work start...
         * ThreadA: work over...
         * ThreadB: work over...
         * main: main over...
         * */
        Thread.sleep(1000);

        System.out.println(Thread.currentThread().getName() + ": main over...");
    }
}
