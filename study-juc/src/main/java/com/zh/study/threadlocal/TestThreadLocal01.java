package com.zh.study.threadlocal;

/**
 * ThreadLocal: 每个线程访问该变量时访问的其实是自己线程内存里的该变量的副本。
 * @date 2020/12/8
 */
public class TestThreadLocal01 {

    //static ThreadLocal<String> local = new ThreadLocal();
    //使用InheritableThreadLocal可以在子线程访问父线程变量
    static ThreadLocal<String> local = new InheritableThreadLocal<>();

    public static void main(String[] args) throws InterruptedException {
        //主线程将该变量值set
        local.set("main set.");

        Thread threadA = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + ":start.");
            //当线程A调用get方法时，获取的实际是线程自己内存中的Local副本
            System.out.println("get: " + local.get());
            //那么我们怎么在子线程中获取到父线程set的值呢？
            local.set("thread set...");
            System.out.println("after set: " + local.get());
            local.remove();
            System.out.println("after remove:" + local.get());
        }, "ThreadA");

        threadA.start();
        Thread.sleep(1000);
        System.out.println(local.get());
        local.remove();
        System.out.println("main over...");
    }
}
