package com.zh.study.redis.controller;

import com.zh.study.redis.bean.User;
import com.zh.study.redis.common.CommonResult;
import com.zh.study.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @date 2020/12/31
 */
@RestController
@RequestMapping("/redis")
public class RedisController {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisService redisService;

    @GetMapping("/testString")
    public CommonResult testString() throws InterruptedException {
        redisTemplate.opsForValue().set("20201231-test-redis-string", "testString", 2l);
        return CommonResult.success(redisTemplate.opsForValue().get("20201231-test-redis-string"));
    }

    @RequestMapping(value = "/testHash", method = RequestMethod.POST)
    public CommonResult testHashSet(@RequestBody User user) {
        redisService.hSet("user", "id", user.getId());
        redisService.hSet("user", "userName", user.getUserName());
        return CommonResult.success(redisService.hGetAll("user"));
    }
}
