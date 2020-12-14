package com.zh.study.studyconsulprovider.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @date 2020/12/14
 */
@RestController
public class ProviderController {
    @Value("${server.port}")
    private String serverPort;

    @RequestMapping(method = RequestMethod.GET, path = "/send")
    public String send() {
        return "success " + serverPort;
    }
}
