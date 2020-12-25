package com.zh.study.threadsync;

import com.zh.study.threadpool.MyThreadPool;

import java.util.concurrent.*;

/**
 * 线程同步器，可以等待所有子线程执行完毕后再进行汇总
 * 基于AQS实现的共享锁, state表示子线程数量
 *  初始化时指定state的值，即子线程的个数
 *  子线程完成后，调用countDown()方法，CAS将state的值减1
 *  主线程调用await()方法将自己阻塞挂起，直至state的值为0或线程被中断，释放阻塞队列中主线程
 * @date 2020/12/25
 */
public class CountDownLatchTest {
    static volatile CountDownLatch countDownLatch = new CountDownLatch(2);

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        //test1();
        testCall();
    }

    /**
     * 测试线程池多个线程执行任务时，线程同步器等待所有子线程执行完并获取返回值
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private static void testCall() throws InterruptedException, ExecutionException {
        //调用自定义的线程池
        ExecutorService pool = MyThreadPool.createMyThreadPool();
        Future<Integer> futureA = pool.submit(new Callable<Integer>() {
            @Override
            public Integer call() {
                int result = 0;
                try {
                    TimeUnit.SECONDS.sleep(1);
                    result = 1 + 9*8 - 25;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
                return result;
            }
        });

        Future<Integer> futureB = pool.submit(new Callable<Integer>() {
            @Override
            public Integer call() {
                int result = 0;
                try {
                    TimeUnit.SECONDS.sleep(1);
                    result = 1 + 123*8 - 25;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
                return result;
            }
        });

        System.out.println("wait ...");
        countDownLatch.await();
        System.out.println(futureA.get());
        System.out.println(futureB.get());
        pool.shutdown();
        System.out.println("over...");
    }

    /**
     * 简单测试两个线程同步
     * @throws InterruptedException
     */
    private static void test1() throws InterruptedException {
        Thread threadA = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                System.out.println("A 任务完成了");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }
        }, "ThreadA");

        Thread threadB = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                System.out.println("B 任务完成了");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }
        }, "ThreadB");

        threadA.start();
        threadB.start();

        System.out.println("wait A/B 任务跑完");
        countDownLatch.await();
        System.out.println("over...");
    }
}
