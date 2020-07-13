package com.imooc.coupon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <h1>忽略统一响应注解定义</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/10
 **/
//可定义在类或方法上面
@Target({ElementType.TYPE, ElementType.METHOD})
//运行时起作用
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreResponseAdvice {

}
