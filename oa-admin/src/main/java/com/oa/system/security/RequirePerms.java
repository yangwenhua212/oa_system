package com.oa.system.security;

import java.lang.annotation.*;

/**
 * 权限校验注解
 * 标注在 Controller 方法上，表示该接口需要指定权限
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePerms {
    /**
     * 需要的权限标识，多个时满足一个即可
     */
    String[] value();
}
