package com.zh.study.aqs;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 基于AQS抽象同步队列实现的不可重入锁，锁只能被一个线程持有，且每次都需要尝试获取资源
 * 其中state的含义：1表示锁被持有，0表示锁未被持有
 * @date 2020/12/22
 */
public class NonReentrantLock implements Lock, Serializable {

    /**
     * 内部帮助类，用于提供不可重入锁的获取与释放逻辑
     * state: 1 表示锁已被持有 0 表示锁未被持有
     */
    private static class Sync extends AbstractQueuedSynchronizer {

        /**
         * state == 1时表示锁被持有
         * @return 锁是否被持有
         */
        @Override
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        /**
         * 以独占方式实现锁的获取
         * @param acquires
         * @return
         */
        @Override
        protected boolean tryAcquire(int acquires) {
            assert acquires == 1;
            //state=0表示锁未被线程持有
            //CAS尝试将state更新为1，若成功则获取锁资源
            if (compareAndSetState(0, 1)) {
                //设置锁的持有者为当前线程
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        /**
         * 以独占方式实现锁的释放
         * @param releases
         * @return
         */
        @Override
        protected boolean tryRelease(int releases) {
            assert releases == 1;
            //只有持有锁的线程才能调用释放锁的方法
            if (getState() == 0) {
                throw new IllegalMonitorStateException();
            }
            //设置锁的当前持有者为null
            setExclusiveOwnerThread(null);
            //设置锁的状态为未持有
            setState(0);
            return true;
        }

        /**
         * 提供ConditionObjec实例用于实现锁的条件变量Condition
         * @return
         */
        Condition newCondition() {
            return new ConditionObject();
        }
    }

    private final Sync sync = new Sync();

    @Override
    public void lock() {
        sync.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }

    private static final NonReentrantLock lock = new NonReentrantLock();
    public static void main(String[] args) {
        for (int i = 0; i < 6; i++) {
            new Thread(() -> {
                lock.lock();
                System.out.println(Thread.currentThread().getName());
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }, "T"+i).start();
        }

    }
}
