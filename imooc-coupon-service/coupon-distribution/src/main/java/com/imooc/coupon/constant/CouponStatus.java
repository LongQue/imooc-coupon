package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * <h1>优惠优惠券的状态</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/18
 */
@Getter
@AllArgsConstructor
public enum CouponStatus {
    USABLE("可用的", 1),
    USED("已使用的", 2),
    EXPIRED("过期的(未被使用的)", 3);
    /**
     * 优惠券状态描述信息
     */
    private String desc;

    /**
     * 优惠券状态编码
     */
    private Integer code;

    /**
     * <h2>根据code获取到CouponStatus</h2>
     *
     * @param code
     * @return
     */
    public static CouponStatus of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values()).filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " not exists"));
    }

}
