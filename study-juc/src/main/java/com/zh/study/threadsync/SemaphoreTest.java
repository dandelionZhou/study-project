package com.zh.study.threadsync;

import com.zh.study.threadpool.MyThreadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

/**
 * 信号量
 *  基于AQS实现的线程同步器，state表示信号量个数.
 *  与CountDownLatch,CyclicBarrier不同的是，它内部的计数器是递增的，并且在一开始时初始化
 *  Semaphore时可以指定一个初始值，但是不需要知道需要同步的线程个数，而是在需要同步的地方调用
 *  acquire方法时指定需要同步的线程个数
 * @date 2020/12/25
 */
public class SemaphoreTest {

    static ExecutorService pool = MyThreadPool.createMyThreadPool();
    static Semaphore semaphore = new Semaphore(0);

    public static void main(String[] args) throws InterruptedException {
        pool.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName());
                semaphore.release();
            }
        });

        pool.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName());
                semaphore.release();
            }
        });

        semaphore.acquire(2);
        System.out.println("all child thread over!");

        pool.shutdown();
    }
}
