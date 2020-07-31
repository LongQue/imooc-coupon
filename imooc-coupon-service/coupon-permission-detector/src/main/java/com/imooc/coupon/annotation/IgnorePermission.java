package com.imooc.coupon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <h1>权限忽略注解</h1>
 * 忽略当前标识的 Controller 接口，不注册权限
 *
 * @author ZhouFufeng
 * @since 2020/7/31
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnorePermission {
}
