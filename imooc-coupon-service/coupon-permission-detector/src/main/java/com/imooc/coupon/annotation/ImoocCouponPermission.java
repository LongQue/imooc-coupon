package com.imooc.coupon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <h1>权限描述注解：定义 Controller 接口的权限</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/31
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ImoocCouponPermission {

    /**
     * <h2>接口描述信息</h2>
     */
    String desc() default "";

    /**
     * <h2>此接口是否为只读，默认true</h2>
     */
    boolean readOnly() default true;

    /**
     * <h2>扩展属性</h2>
     */
    String extra() default "";
}
