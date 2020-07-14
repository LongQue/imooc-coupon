package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * <h1>分发目标,对单个用户的发券，对多个用户发券（一般是推送，不用领取）</h1>
 * @author ZhouFufeng
 * @since 2020/7/14
 **/
@Getter
@AllArgsConstructor
public enum  DistributeTarget {

    SINGLE("单用户",1), MULTI("多用户", 2);

    /**
     * 分发目标描述
     */
    private String description;

    /**
     * 分发目标编码
     */
    private Integer code;

    public static DistributeTarget of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                     .filter(bean -> bean.code.equals(code))
                     .findAny()
                     .orElseThrow(() -> new IllegalArgumentException(code + " no exist"));
    }
}
