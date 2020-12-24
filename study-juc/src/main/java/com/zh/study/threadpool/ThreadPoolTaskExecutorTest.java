package com.zh.study.threadpool;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 使用ThreadPoolTaskExecutor创建线程池
 * ThreadPoolTaskExecutor 装饰者模式，内部维护ThreadPoolExecutor对象
 * @date 2020/12/24
 */
public class ThreadPoolTaskExecutorTest {
    static ThreadPoolTaskExecutor createExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        /**
         * Set the ThreadPoolExecutor's core pool size.
         * Default is 1.
         * <p><b>This setting can be modified at runtime, for example through JMX.</b>
         * 设置核心线程数，默认是1，可以动态设置(即在线程池运行期间也可以动态设置)
         */
        taskExecutor.setCorePoolSize(5);
        /**
         * Set the ThreadPoolExecutor's maximum pool size.
         * Default is {@code Integer.MAX_VALUE}.
         * <p><b>This setting can be modified at runtime, for example through JMX.</b>
         * 设置最大线程数，默认是Integer.MAX_VALUE，可以动态设置
         */
        taskExecutor.setMaxPoolSize(5);
        /**
         * Set the ThreadPoolExecutor's keep-alive seconds.
         * Default is 60.
         * <p><b>This setting can be modified at runtime, for example through JMX.</b>
         * 当线程个数超过核心线程个数时，空闲线程的存活时间，默认是60秒,可以动态设置
         */
        taskExecutor.setKeepAliveSeconds(20);
        //设置线程拒绝策略
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        //设置线程创建工厂
        taskExecutor.setThreadFactory(new MyThreadPool.ThreadNameFactory());
        //设置线程名前缀
        taskExecutor.setThreadNamePrefix("demo ");
        //设置线程池是否等所有任务完成后才shut down
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        //调用初始化方法，初始化线程池
        taskExecutor.initialize();
        return taskExecutor;
    }

    public static void main(String[] args) {
        ThreadPoolTaskExecutor pool = createExecutor();

        for (int i = 1; i <= 10; i++) {
            /*if (i%8 == 0) {
                pool.setCorePoolSize(3);
                pool.setMaxPoolSize(3);
            }*/

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

        }

        System.out.println(pool.getCorePoolSize());
        pool.shutdown();

    }
}
