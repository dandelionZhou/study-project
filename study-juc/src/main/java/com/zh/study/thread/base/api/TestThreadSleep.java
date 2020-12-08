package com.zh.study.thread.base.api;

/**
 * Thread: sleep() 让线程睡眠，线程会暂时让出指定时间的CPU调度，但是不会释放锁。
 * @date 2020/12/8
 *
 * Causes the currently executing thread to sleep (temporarily cease
 * execution) for the specified number of milliseconds, subject to
 * the precision and accuracy of system timers and schedulers. The thread
 * does not lose ownership of any monitors.
 *
 * @param @millis
 *         the length of time to sleep in milliseconds
 *
 * 当传入负数的时间单位时，会抛出非法参数异常
 * @throws  IllegalArgumentException
 *          if the value of {@code millis} is negative
 *
 * 当线程调用sleep()进入Waiting状态时，如果其他线程调用该线程的interrupt()方法，则该线程被中断
 * @throws  InterruptedException
 *          if any thread has interrupted the current thread. The
 *          <i>interrupted status</i> of the current thread is
 *          cleared when this exception is thrown.
 */
public class TestThreadSleep {

    private static Object object = new Object();
    public static void main(String[] args) throws InterruptedException {
        Thread threadA = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + ": start...");
            synchronized(object) {
                try {
                    Thread.sleep(200000);
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                    System.err.println(Thread.currentThread().getName() + ": InterruptedException...");
                }
            }
            System.out.println(Thread.currentThread().getName() + ": end...");
        }, "ThreadA");

        Thread threadB = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + ": start...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.err.println(Thread.currentThread().getName() + ": InterruptedException...");
                //e.printStackTrace();
            }

            synchronized (object) {
                System.out.println(Thread.currentThread().getName() + ": get object monitor...");
            }
            System.out.println(Thread.currentThread().getName() + ": end...");
        }, "ThreadB");

        threadA.start();
        threadB.start();
        //throw IllegalArgumentException
        //Thread.sleep(-1000);
        Thread.sleep(1000);
        System.out.println(Thread.currentThread().getName() + ": interrupt()...");
        threadA.interrupt();
    }
}
