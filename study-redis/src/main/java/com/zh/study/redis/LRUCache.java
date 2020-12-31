package com.zh.study.redis;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 缓存策略：最近最少使用算法
 * 找出缓存中最近最少使用的数据
 * @date 2020/12/30
 */
public class LRUCache {

    private Map<Integer, Integer> lruMap;
    private int capacity;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        lruMap = new LinkedHashMap<>();
    }

    public int get(int key) {
        if(!lruMap.containsKey(key)) {
            return -1;
        }

        Integer value = lruMap.remove(key);
        lruMap.put(key, value);
        return value;
    }

    public void put(int key, int value) {
        if (lruMap.containsKey(key)) {
            lruMap.remove(key);
            lruMap.put(key, value);
            return;
        }

        lruMap.put(key, value);
        if(lruMap.size() > capacity) {
            lruMap.remove(lruMap.entrySet().iterator().next().getKey());
        }
    }

    @Override
    public String toString() {
        return "LRUCache{" +
                "lruMap=" + lruMap +
                ", capacity=" + capacity +
                '}';
    }

    public static void main(String[] args) {
        LRUCache lruCache = new LRUCache(5);
        lruCache.put(1, 1);
        lruCache.put(2, 2);
        lruCache.put(3, 3);
        lruCache.put(4, 4);
        lruCache.put(5, 5);

        System.out.println(lruCache.get(2));
        System.out.println(lruCache.get(5));
        System.out.println(lruCache.get(1));
        lruCache.put(6,6);
        System.out.println(lruCache.toString());
    }

}
