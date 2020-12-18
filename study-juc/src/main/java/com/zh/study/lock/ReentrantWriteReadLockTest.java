package com.zh.study.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ReentrantReadWriteLock(可重入的读写锁)：
 *  ReadLock(读锁)
 *      读锁是共享锁，在写锁没有被其他线程持有的情况下，读锁可由多个线程持有；如果写锁被其他线程持有，则当前线程会阻塞挂起；
 *  WriteLock(写锁)
 *      写锁是独占锁，在读锁和写锁都没有被其他线程持有的情况下，当前线程可以获取锁，且会阻塞其他线程；如果其他线程持有读锁或写锁，则当前线程
 *      会阻塞挂起；
 * @date 2020/12/18
 */
public class ReentrantWriteReadLockTest {
    final static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    final static ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    final static ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    public static void main(String[] args) throws InterruptedException {

        new Thread(() -> {
            readLock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + ": get lock...");
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                readLock.unlock();
            }

        }, "ThreadA").start();

        TimeUnit.SECONDS.sleep(1);

        new Thread(() -> {
            readLock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + ": get lock...");
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                readLock.unlock();
            }
        }, "ThreadB").start();

    }
}
