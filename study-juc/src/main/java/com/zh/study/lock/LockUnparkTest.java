package com.zh.study.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * LockSupport.unpark(thread);
 * 给thread与LockSupport关联的许可证，如果线程之前被park()阻塞挂起，那么该线程会被唤醒.
 * @date 2020/12/18
 */
public class LockUnparkTest {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("main start...");

        Thread thread = new Thread(() -> {
            System.out.println("threadA start...");
            //主线程休眠3秒，因此该线程此时因该没有获得与LockSupport关联的许可证
            //因此会阻塞挂起
            LockSupport.park();
        }, "ThreadA");

        // LockSupport.unpark(thread);
        thread.start();
        //TimeUnit.SECONDS.sleep(3);
        //此时调用
        LockSupport.unpark(thread);
        System.out.println("main over");
        Thread.interrupted();
    }
}
