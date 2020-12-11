package com.zh.study.thread.sync;

/**
 * @date 2020/12/10
 */
public class TestVolatile {
    static volatile int num = 0;
    public static void main(String[] args) {

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    num++;
                }

            }, "Thread" + i).start();
        }

        System.out.println(num);
    }
}
