package com.zh.study.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @date 2020/12/16
 *
 * Thread0 get lock
 * Thread1 get lock
 * Thread2 get lock
 * Thread3 get lock
 * Thread4 get lock
 * Thread0 get lock
 * Thread1 get lock
 * Thread2 get lock
 * Thread3 get lock
 * Thread4 get lock
 *
 *
 */
public class ReentrantLockTest {
    private static ReentrantLock lock = new ReentrantLock();
    static Condition conditionA = lock.newCondition();
    static Condition conditionB = lock.newCondition();
    static int num = 0;

    public static void test() {
        for (int i = 0; i < 1; i++) {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName()+" get lock");
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }
    public static void main(String[] args) {
           /* for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                test();
            }, "Thread"+i).start();
        }*/

           new Thread(() -> {
               lock.lock();
               try {
                   System.out.println(Thread.currentThread().getName() + " come .");
                   while (num == 1) {
                       conditionA.await();
                   }
                   num = 1;
                   conditionB.signal();

               } catch (InterruptedException e) {
                   e.printStackTrace();
               }finally {
                   lock.unlock();
               }
           }, "ThreadA").start();

        new Thread(() -> {
            lock.lock();
            System.out.println(Thread.currentThread().getName());
            conditionA.signal();
        }, "ThreadB").start();


    }
}
