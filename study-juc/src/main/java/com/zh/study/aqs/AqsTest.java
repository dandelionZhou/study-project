package com.zh.study.aqs;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;

/**
 * @date 2020/12/16
 */
public class AqsTest {
    public static void main(String[] args) {
        //System.out.println(Integer.MAX_VALUE);
        int num = 4;
        // 1 << 4
        System.out.println(9 & (1 << 16) - 1);
    }
}
