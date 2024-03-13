package com.moriaty.vuitton;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.moriaty.vuitton.dao.mapper")
public class VuittonPlusApplication {

    public static void main(String[] args) {
        SpringApplication.run(VuittonPlusApplication.class, args);
    }

}
