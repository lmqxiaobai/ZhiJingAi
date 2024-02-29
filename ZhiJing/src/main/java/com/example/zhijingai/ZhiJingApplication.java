package com.example.zhijingai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
// 开启Spring Task,用来定时
@EnableScheduling
public class ZhiJingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhiJingApplication.class, args);
    }

}
