package com.imooc.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <h1>Http 方法枚举类型</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/29
 */
@Getter
@AllArgsConstructor
public enum HttpMethodEnum {

    GET,
    HEAD,
    POST,
    PUT,
    PATHC,
    DELETE,
    OPTIONS,
    TRACE,
    ALL
}
