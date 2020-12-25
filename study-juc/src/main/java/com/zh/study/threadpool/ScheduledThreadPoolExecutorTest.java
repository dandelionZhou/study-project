package com.zh.study.threadpool;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ScheduledThreadPoolExecutor
 * @date 2020/12/24
 */
public class ScheduledThreadPoolExecutorTest {
    static ScheduledThreadPoolExecutor poolExecutor = new ScheduledThreadPoolExecutor(10);
    public static void main(String[] args) {

        poolExecutor.setContinueExistingPeriodicTasksAfterShutdownPolicy(true);

        //System.out.println(System.currentTimeMillis());
        //以固定频率执行任务
        poolExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName());
            }
        }, 3, 2, TimeUnit.SECONDS);

        poolExecutor.shutdown();
    }

}
