package com.kabigon.project;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.kabigon.project.mapper")
@EnableDubbo
public class KabigonApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(KabigonApiApplication.class, args);
    }

}
