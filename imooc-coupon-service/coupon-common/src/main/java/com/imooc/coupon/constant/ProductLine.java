package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * <h1>产品线枚举</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/14
 **/
@Getter
@AllArgsConstructor
public enum ProductLine {

    DAMAO("大猫", 1), DABAO("大宝", 2);

    /**
     * 描述
     */
    private String description;

    /**
     * 产品线编码
     */
    private Integer code;

    public static ProductLine of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                     .filter(bean -> bean.code.equals(code))
                     .findAny()
                     .orElseThrow(() -> new IllegalArgumentException(code + " no exist"));
    }
}
