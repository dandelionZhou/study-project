package com.zh.study.ribbon;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RoundRobinRule;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @date 2020/12/14
 */
@Configuration
@RibbonClient(name = "study-consul-provider")
public class MyRule {
    @Bean
    public IRule ribbonRule() {
        return new RoundRobinRule();
    }
}
