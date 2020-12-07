package com.zh.study.thread.base.create;

/**
 * 实现Runnable接口创建线程
 * @date 2020-12-07
 */
public class CreateByImplRunnable {

    int count = 0;

    static class ThreadTest implements Runnable {

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + ": comming...");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + ": start...");
        Thread thread = new Thread(new ThreadTest());
        thread.start();
        Thread.sleep(1000);
        System.out.println(Thread.currentThread().getName() + ": end...");
    }
}
