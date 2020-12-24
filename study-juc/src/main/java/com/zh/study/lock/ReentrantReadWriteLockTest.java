package com.zh.study.lock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @date 2020/12/22
 */
public class ReentrantReadWriteLockTest {
    static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    static final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    public static void main(String[] args) {
        new Thread(() -> {
            readLock.unlock();
        }, "Thread").start();
    }
}
