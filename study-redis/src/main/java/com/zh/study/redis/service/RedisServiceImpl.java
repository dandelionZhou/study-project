package com.zh.study.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redisAPI 公共服务类
 *  redis基本的数据类型：
 *  # 字符串 String
 *  # 哈希表 Hashes
 *  # 有序列表 List
 *  # 去重的集合 Set
 *  # 有序去重的集合 Sorted Set
 *
 * @date 2020/12/31
 */
@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    /**
     * String: 保存属性,设置过期时间
     *
     * @param key
     * @param value
     * @param time
     */
    @Override
    public void set(String key, Object value, long time) {
        redisTemplate.opsForValue().set(key, value, time);
    }

    /**
     * String: 保存属性
     *
     * @param key
     * @param value
     */
    @Override
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * String: 获取属性
     *
     * @param key
     */
    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除属性
     *
     * @param key
     */
    @Override
    public Boolean del(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 批量删除属性
     *
     * @param keys
     */
    @Override
    public Long del(List<String> keys) {
        return redisTemplate.delete(keys);
    }

    /**
     * 设置过期时间
     *
     * @param key
     * @param time
     */
    @Override
    public Boolean expire(String key, long time) {
        return redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    /**
     * 获取过期时间
     *
     * @param key
     */
    @Override
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 判断是否有该属性
     *
     * @param key
     */
    @Override
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 按delta递增
     *
     * @param key
     * @param delta
     */
    @Override
    public Long incr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 按delta递减
     *
     * @param key
     * @param delta
     */
    @Override
    public Long decr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    /**
     * 获取Hash结构中的属性
     *
     * @param key
     * @param hashKey
     */
    @Override
    public Object hGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 向Hash结构中放入一个属性
     *
     * @param key
     * @param hashKey
     * @param value
     * @param time
     */
    @Override
    public Boolean hSet(String key, String hashKey, Object value, long time) {
        redisTemplate.opsForHash().put(key, hashKey, value);
        return expire(key, time);
    }

    /**
     * 向Hash结构中放入一个属性
     *
     * @param key
     * @param hashKey
     * @param value
     */
    @Override
    public void hSet(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 直接获取整个Hash结构
     *
     * @param key
     */
    @Override
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 直接设置整个Hash结构
     *
     * @param key
     * @param map
     * @param time
     */
    @Override
    public Boolean hSetAll(String key, Map<String, Object> map, long time) {
        redisTemplate.opsForHash().putAll(key, map);
        return expire(key, time);
    }

    /**
     * 直接设置整个Hash结构
     *
     * @param key
     * @param map
     */
    @Override
    public void hSetAll(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 删除Hash结构中的属性
     *
     * @param key
     * @param hashKey
     */
    @Override
    public void hDel(String key, Object... hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    /**
     * 判断Hash结构中是否有该属性
     *
     * @param key
     * @param hashKey
     */
    @Override
    public Boolean hHasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /**
     * Hash结构中属性递增
     *
     * @param key
     * @param hashKey
     * @param delta
     */
    @Override
    public Long hIncr(String key, String hashKey, Long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    /**
     * Hash结构中属性递减
     *
     * @param key
     * @param hashKey
     * @param delta
     */
    @Override
    public Long hDecr(String key, String hashKey, Long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, -delta);
    }

    /**
     * 获取Set结构
     *
     * @param key
     */
    @Override
    public Set<Object> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 向Set结构中添加属性
     *
     * @param key
     * @param values
     */
    @Override
    public Long sAdd(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 向Set结构中添加属性
     *
     * @param key
     * @param time
     * @param values
     */
    @Override
    public Long sAdd(String key, long time, Object... values) {
        return  redisTemplate.opsForSet().add(key, values, time);
    }

    /**
     * 是否为Set中的属性
     *
     * @param key
     * @param value
     */
    @Override
    public Boolean sIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 获取Set结构的长度
     *
     * @param key
     */
    @Override
    public Long sSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 删除Set结构中的属性
     *
     * @param key
     * @param values
     */
    @Override
    public Long sRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 获取List结构中的属性
     *
     * @param key
     * @param start
     * @param end
     */
    @Override
    public List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 获取List结构的长度
     *
     * @param key
     */
    @Override
    public Long lSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 根据索引获取List中的属性
     *
     * @param key
     * @param index
     */
    @Override
    public Object lIndex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    /**
     * 向List结构中添加属性
     *
     * @param key
     * @param value
     */
    @Override
    public Long lPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 向List结构中添加属性
     *
     * @param key
     * @param value
     * @param time
     */
    @Override
    public Long lPush(String key, Object value, long time) {
        Long index = redisTemplate.opsForList().rightPush(key, value);
        expire(key, time);
        return index;
    }

    /**
     * 向List结构中批量添加属性
     *
     * @param key
     * @param values
     */
    @Override
    public Long lPushAll(String key, Object... values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    /**
     * 向List结构中批量添加属性
     *
     * @param key
     * @param time
     * @param values
     */
    @Override
    public Long lPushAll(String key, Long time, Object... values) {
        Long index = redisTemplate.opsForList().rightPushAll(key, values);
        expire(key, time);
        return index;
    }

    /**
     * 从List结构中移除属性
     *
     * @param key
     * @param count
     * @param value
     */
    @Override
    public Long lRemove(String key, long count, Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }
}
