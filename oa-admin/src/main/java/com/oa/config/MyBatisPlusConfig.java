package com.oa.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus Configuration
 */
@Configuration
@MapperScan("com.oa.**.mapper")
public class MyBatisPlusConfig {

    /**
     * MyBatis-Plus 拦截器链
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(DataScopeInterceptor dataScopeInterceptor) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 数据范围拦截器
        interceptor.addInnerInterceptor(dataScopeInterceptor);
        return interceptor;
    }
}
