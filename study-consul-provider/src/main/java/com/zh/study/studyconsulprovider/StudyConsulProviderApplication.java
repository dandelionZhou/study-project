package com.zh.study.studyconsulprovider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class StudyConsulProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyConsulProviderApplication.class, args);
    }

}
