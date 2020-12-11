package com.zh.study.thread.sync;

import org.apache.logging.log4j.util.Strings;

/**
 * @date 2020/12/10
 */
public class TestVolatileSingleton {

    private static TestVolatileSingleton singleton;
    public static TestVolatileSingleton getInstance() {
        if (singleton == null) {
            synchronized (TestVolatileSingleton.class) {
                if (singleton == null) {
                    singleton = new TestVolatileSingleton();
                }
            }
        }
        return singleton;
    }

    public static void main(String[] args) {
        System.out.println(TestVolatileSingleton.getInstance());
    }
}
