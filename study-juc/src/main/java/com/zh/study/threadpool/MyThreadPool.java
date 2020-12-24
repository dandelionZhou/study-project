package com.zh.study.threadpool;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @date 2020/12/24
 * ThreadPoolExecutor创建自定义的线程池：
 * public ThreadPoolExecutor(int corePoolSize,
 *                               int maximumPoolSize,
 *                               long keepAliveTime,
 *                               TimeUnit unit,
 *                               BlockingQueue<Runnable> workQueue,
 *                               ThreadFactory threadFactory,
 *                               RejectedExecutionHandler handler)
 * corePoolSize: 核心线程个数
 * maximumPoolSize: 最大线程个数
 * keepAliveTime: 当前线程个数超过核心线程个数时，多出来的线程处于空闲状态的将在多少时间内回收
 * unit: keepAliveTime的时间单位
 * workQueue：存储等待执行工作的阻塞队列
 * threadFactory: 工作线程创建工厂
 * handler: 拒绝策略
 *  ThreadExecutor中实现了四种，也可以自己实现RejectedExecutionHandler接口自定义拒绝策略
 *      # AbortPolicy : 直接抛出RejectedExecutionException异常
 *      # CallerRunsPolicy : 让当前线程自己执行该任务，从哪来回哪去
 *      # DiscardPolicy : 不管这个任务，默默的抛弃它
 *      # DiscardOldestPolicy : 调用poll丢弃阻塞队列中的任务，执行当前任务
 */
public class MyThreadPool {
    /**
     * 自定义的ThreadFactory，给每个创建的Thread命个名
     */
    static class ThreadNameFactory implements ThreadFactory {
        private static final AtomicInteger name = new AtomicInteger(0);
        /**
         * Constructs a new {@code Thread}.  Implementations may also initialize
         * priority, name, daemon status, {@code ThreadGroup}, etc.
         *
         * @param r a runnable to be executed by new thread instance
         * @return constructed thread, or {@code null} if the request to
         * create a thread is rejected
         */
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Thread" + name.getAndIncrement());
        }
    }

    /**
     * 创建自定义线程池，其中参数如下：
     * corePoolSize: 核心线程数5个，通常为
     * maximumPoolSize: 最大线程数5个
     * @return
     */
    static ExecutorService createMyThreadPool() {

        return new ThreadPoolExecutor(
                5,
                5,
                20l,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new ThreadNameFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );

    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService pool = createMyThreadPool();
        for (int i = 0; i < 5; i++) {

            /*Future futureTask = pool.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        System.out.println(Thread.currentThread().getName());

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });*/
            Future<String> future = pool.submit(new Callable<String>() {

                /**
                 * Computes a result, or throws an exception if unable to do so.
                 *
                 * @return computed result
                 * @throws Exception if unable to compute a result
                 */
                @Override
                public String call() throws Exception {
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println(Thread.currentThread().getName());
                    return Thread.currentThread().getName() + ": success return";
                }
            });

            System.out.println(future.get());
        }
    }
}
