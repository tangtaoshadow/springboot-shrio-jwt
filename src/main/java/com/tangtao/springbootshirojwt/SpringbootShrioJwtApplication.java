package com.tangtao.springbootshirojwt;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(value = "com.tangtao.springbootshirojwt.mapper")
public class SpringbootShrioJwtApplication {

    public static void main(String[] args) {

        SpringApplication.run(SpringbootShrioJwtApplication.class, args);
    }

}
