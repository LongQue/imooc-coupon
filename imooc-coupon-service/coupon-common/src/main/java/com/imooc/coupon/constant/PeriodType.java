package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * <h1>有效期类型枚举</h1>
 * @author ZhouFufeng
 * @since 2020/7/14
 **/
@Getter
@AllArgsConstructor
public enum PeriodType {

    REGULAR("固定的(固定日期)",1),
    SHIFT("变动的(一领取日开始计算)",2);
    private String description;

    /**
     * 产品线编码
     */
    private Integer code;

    public static PeriodType of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                     .filter(bean -> bean.code.equals(code))
                     .findAny()
                     .orElseThrow(() -> new IllegalArgumentException(code + " no exist"));
    }
}
