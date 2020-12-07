package com.zh.study.thread.base.create;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 实现Callable接口创建线程，并使用FutureTask获取返回值
 * @date 2020-12-07
 */
public class CreateByImplCallable {
    static class ThreadTest implements Callable {

        /**
         * Computes a result, or throws an exception if unable to do so.
         *
         * @return computed result
         * @throws Exception if unable to compute a result
         */
        @Override
        public Object call() throws Exception {
            System.out.println(Thread.currentThread().getName() + ": coming...");
            return 10 * 10;
        }
    }

    public static void main(String[] args) {
        System.out.println(Thread.currentThread().getName() + ": main start");
        FutureTask futureTask = new FutureTask(new ThreadTest());
        Thread thread = new Thread(futureTask);
        thread.start();
        try {
            System.out.println(futureTask.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
