package com.zh.study.lock;

import sun.misc.Unsafe;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

/**
 * java.util.concurrent.locks.LockSupport:
 * 主要作用是挂起和唤醒线程，用于创建锁和其他同步类的基础。
 *
 *  1.LockSupport.park();调用park方法的线程如果持有许可证，那么会马上返回；如果没有许可证，则调用线程会被禁止参与线程的调度，也就是会被阻塞挂起。
 *
 *  2.LockSupport.unpark(thread); 让线程thread持有许可证，这样thread不会被阻塞
 *
 * @date 2020/12/16
 */
public class LockSupportTest {

    static class FIFOMutex {
        private final AtomicBoolean locked = new AtomicBoolean(false);
        private final Queue<Thread> waiters = new ConcurrentLinkedQueue<>();

        public void lock() {
            boolean isInterrupted = false;
            Thread currentThread = Thread.currentThread();
            waiters.add(currentThread);
            //如果当前线程不是队首的线程且CAS更新失败则阻挂起该线程
            while (waiters.peek() != currentThread || !locked.compareAndSet(false, true)) {
                LockSupport.park();
                //等待状态下无视中断
                if (Thread.interrupted()) {
                    isInterrupted = true;
                }
            }

            waiters.remove();
            if (isInterrupted)
                currentThread.interrupt();
        }

        public void unlock() {
            //设置锁表示为没有线程持有
            locked.set(false);
            //唤醒队列中队首的线程
            LockSupport.unpark(waiters.peek());
        }
    }

    public static void main(String[] args) throws InterruptedException {

        Thread thread = new Thread(() -> {
            System.out.println("start....");
            //调用LockSupport.park()方法阻塞挂起当前线程
            LockSupport.park();
            System.out.println("end......");
        }, "ThreadA");

        thread.start();
        TimeUnit.SECONDS.sleep(2);
        //该线程已被阻塞挂起，调用该线程interrupt()方法设置中断标志,该线程也会被唤醒
        thread.interrupt();
        //LockSupport.unpark(thread);
        TimeUnit.SECONDS.sleep(1);
        //重新park后，线程也需要重新持有许可证才能不被阻塞
        //LockSupport.park(thread);
        System.out.println("main is over......");
    }
}
