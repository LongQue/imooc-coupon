package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * <h1>优惠券分类</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/14
 **/
@Getter
@AllArgsConstructor
public enum CouponCategory {

    /**
     * 英语不好表达用拼音
     */
    MANJIAN("满减券", "001"), ZHEKOU("折扣券", "002"), LIJIAN("立减券", "003");

    /**
     * 描述信息
     */
    private String description;

    /**
     * 分类编码
     */
    private String code;

    public static CouponCategory of(String code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                     .filter(bean -> bean.code.equals(code))
                     .findAny()
                     .orElseThrow(() -> new IllegalArgumentException(code + " no exist"));
    }
}
