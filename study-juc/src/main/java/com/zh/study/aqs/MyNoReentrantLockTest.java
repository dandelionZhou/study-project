package com.zh.study.aqs;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @date 2020/12/17
 */
public class MyNoReentrantLockTest {
    private static final MyNoReentrantLock lock = new MyNoReentrantLock();
    //static final ReentrantLock lock = new ReentrantLock();
    private static final Condition conEmpty = lock.newCondition();
    private static final Condition conFull = lock.newCondition();

    private static final Queue<String> data = new LinkedBlockingQueue<>();
    final static int size = 10;

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                lock.lock();
                try {
                    while (data.size() == size) {
                        conFull.await();
                    }
                    data.add("P");
                    System.out.println(Thread.currentThread().getName() + " pro");
                    conEmpty.signalAll();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }

            }, "Product" + i).start();
        }


        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                lock.lock();
                try {
                    while (0 == data.size()) {
                        conEmpty.await();
                    }
                    System.out.println(Thread.currentThread().getName() +" consumer: "+ data.poll());
                    conFull.signalAll();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }, "Consumer"+i).start();
        }

    }
}
