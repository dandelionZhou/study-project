package com.zh.study.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @date 2020/12/24
 */
public class CachedThreadPoolTest {
    static ExecutorService pool = Executors.newCachedThreadPool();
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        System.out.println(Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

           // pool.shutdown();
        }
    }
}
