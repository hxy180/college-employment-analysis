package com.shixun;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.shixun.dao")  // 一定要写上你的 Mapper 包路径
public class EmploymentApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmploymentApiApplication.class, args);
    }

}
