package com.oa;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * OA System Application Entry
 */
@SpringBootApplication
@MapperScan("com.oa.system.mapper")
public class OaAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(OaAdminApplication.class, args);
    }

}
