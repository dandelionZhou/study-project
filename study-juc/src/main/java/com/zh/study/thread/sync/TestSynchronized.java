package com.zh.study.thread.sync;

/**
 * synchronized: 是Java提供的一种原子性内置锁，Java中每个对象都可以把它当成一个同步锁来使用
 *  这些Java内置的使用者看不到的锁称为内置锁，也叫监视器锁。
 *
 *  synchronized产生的内置锁是重量级锁，当一个线程获取到这个锁后，其他线程必须等待该线程释放锁后才能获取
 *
 *  保证原子性
 *  保证可见性
 *  不保证有序性
 * @date 2020/12/8
 */
public class TestSynchronized {

    public static final String SFLAG = "FLAG";

    static String TXT = "2";
    public static void main(String[] args) throws InterruptedException {

        new Thread(() -> {
            synchronized (TestSynchronized.class) {
                System.out.println("com sync...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //System.out.println("sync over...");
            }
        }, "threadA").start();

        TXT = null;
        Thread.sleep(1000);

        System.gc();

        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + ":" + TestSynchronized.SFLAG);
        }, "threadB").start();


        System.out.println(TestSynchronized.SFLAG);
        System.out.println("main is over...");

    }
}
