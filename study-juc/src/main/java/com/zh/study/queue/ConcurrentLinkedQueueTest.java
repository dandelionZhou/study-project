package com.zh.study.queue;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.LongAdder;

/**
 * @date 2020/12/29
 */
public class ConcurrentLinkedQueueTest {
    public static void main(String[] args) {
        LongAdder longAdder = new LongAdder();
        ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();
        queue.add(8);
        queue.add(9);
        queue.add(1);
        queue.add(2);
        queue.add(3);
        System.out.println(queue.poll());
        System.out.println(queue);
    }
}
