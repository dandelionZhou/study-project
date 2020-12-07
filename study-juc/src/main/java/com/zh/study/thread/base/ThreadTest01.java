package com.zh.study.thread.base;

public class ThreadTest01  {

    class TestThread extends Thread {
        @Override
        public void run() {
            System.out.println("test base therad create");
        }
    }
}
