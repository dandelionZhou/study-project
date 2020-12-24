package com.zh.study.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @date 2020/12/24
 *
 * public static ExecutorService newSingleThreadExecutor() {
 *         return new FinalizableDelegatedExecutorService
 *             (new ThreadPoolExecutor(1, 1,
 *                                     0L, TimeUnit.MILLISECONDS,
 *                                     new LinkedBlockingQueue<Runnable>()));
 * }
 * 从以上源码可知，SingleThreadExecutor的核心线程数和最大线程数都为1，即始终只有一个线程在执行任务
 */
public class SingleThreadPoolTest {
    static ExecutorService single = Executors.newSingleThreadExecutor();
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            single.submit(new Runnable() {
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
        }
        //以上打印的工作线程都是同一个
    }
}
