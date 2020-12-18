package com.zh.study.aqs;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @date 2020/12/17
 *
 * 自定义实现不可重入的独占锁：state = 0 表示没有线程持有锁, state = 1 表示锁已被一个线程持有
 *
 */
public class MyNoReentrantLock implements Lock {

    private static class Sync extends AbstractQueuedSynchronizer {
        @Override
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        @Override
        protected boolean tryAcquire(int acquires) {
            assert acquires == 1;
            //CAS尝试将0变成1，即尝试获取锁
            if (compareAndSetState(0, 1)) {
                //若CAS尝试将state更新成1成功，那么设置独占锁为当前线程占有
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            //否则表示锁被其他线程持有
            return false;
        }

        @Override
        protected boolean tryRelease(int release) {
            assert  release == 1;
            if (getState() == 0)
                throw new IllegalMonitorStateException();
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        Condition newCondition() {
            return new ConditionObject();
        }
    }

    private static Sync sync = new Sync();

    @Override
    public void lock() {
        sync.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }


    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }


    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }
}
