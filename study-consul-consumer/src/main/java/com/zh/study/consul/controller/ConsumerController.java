package com.zh.study.consul.controller;

import com.zh.study.consul.service.FeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @date 2020/12/14
 */
@RestController
public class ConsumerController {

    private static final String URL = "http://study-consul-provider";
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FeignService feignService;

    @GetMapping("/consumer")
    public String consumer() {
        return restTemplate.getForObject(URL + "/send", String.class);
    }

    @GetMapping("/consumer/feign")
    public String consumerFeign() {
        return feignService.consumerByFeign();
    }
}
