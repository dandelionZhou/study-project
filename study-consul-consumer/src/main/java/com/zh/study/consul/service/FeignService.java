package com.zh.study.consul.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @date 2020/12/14
 */
@FeignClient(value = "study-consul-provider")

public interface FeignService {

    @GetMapping("/send")
    String consumerByFeign();
}
