package com.imooc.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <h1>操作模式的枚举定义</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/29
 */
@Getter
@AllArgsConstructor
public enum OpModeEnum {

    READ("读"),
    WRITE("写");
    private String mode;
}
