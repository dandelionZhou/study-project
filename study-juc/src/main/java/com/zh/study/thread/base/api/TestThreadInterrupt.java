package com.zh.study.thread.base.api;

/**
 * @date 2020/12/8
 */
public class TestThreadInterrupt {

    private static volatile Integer count = 0;
    public static void main(String[] args) throws InterruptedException {
        Thread threadA = new Thread(() -> {
            synchronized (count) {
                System.out.println(Thread.currentThread().getName() + ": get count monitor");
                while (count == 0) {
                    try {
                        count.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                //获取到锁后调用wait方法挂起后，抛出InterruptedException后将不会执行以下代码
                System.out.println(Thread.currentThread().getName() + ": over...");
            }
        }, "ThreadA");

        threadA.start();
        //threadA.setDaemon(true);
        //主线程睡眠1s让threadA先执行
        Thread.sleep(1000);
        //这时线程A已处于调用wait方法后的挂起状态，此时调用线程A的interrupt()方法会抛出InterruptedException
        threadA.interrupt();

    }
}
