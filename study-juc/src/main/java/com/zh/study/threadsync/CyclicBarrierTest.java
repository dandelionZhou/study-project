package com.zh.study.threadsync;

import com.zh.study.threadpool.MyThreadPool;

import java.util.concurrent.*;

/**
 * 回环屏障CyclicBarrier可以让一组线程全部达到一个状态后再全部同时执行，
 * 相比于CountDownLatch，CyclicBarrier可以重用
 *
 *
 * @date 2020/12/25
 */
public class CyclicBarrierTest {

    static final ExecutorService pool = MyThreadPool.createMyThreadPool();

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {

        CyclicBarrier cyclicBarrier = new CyclicBarrier(2, new Runnable() {
            @Override
            public void run() {
                System.out.println("all over...");
            }
        });

        //让每个子线程阶段性的完成任务并汇总，可重复使用
        pool.submit(new Runnable() {
            @Override
            public void run() {

                try {
                    System.out.println(Thread.currentThread().getName() + "step1 start...");
                    cyclicBarrier.await();

                    System.out.println(Thread.currentThread().getName() + "step2 start...");
                    cyclicBarrier.await();

                    System.out.println(Thread.currentThread().getName() + "step3 start...");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        });

        pool.submit(new Runnable() {
            @Override
            public void run() {

                try {
                    System.out.println(Thread.currentThread().getName() + "step1 start...");
                    cyclicBarrier.await();

                    System.out.println(Thread.currentThread().getName() + "step2 start...");
                    cyclicBarrier.await();

                    System.out.println(Thread.currentThread().getName() + "step3 start...");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        });

        pool.shutdown();
        //singleTest(cyclicBarrier);
    }

    private static void singleTest(CyclicBarrier cyclicBarrier) throws InterruptedException, BrokenBarrierException {
        pool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                TimeUnit.SECONDS.sleep(1);
                System.out.println(Thread.currentThread().getName() + "CyclicBarrier in...");
                cyclicBarrier.await();
                System.out.println(Thread.currentThread().getName() + "CyclicBarrier out...");
                return Thread.currentThread().getName();
            }
        });

        pool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                TimeUnit.SECONDS.sleep(1);
                System.out.println(Thread.currentThread().getName() + "CyclicBarrier in...");
                cyclicBarrier.await();
                System.out.println(Thread.currentThread().getName() +"CyclicBarrier out...");
                return Thread.currentThread().getName();
            }
        });

        pool.shutdown();
    }
}
