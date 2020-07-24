package imooc.coupon.settlement.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <h1>规则类型枚举定义</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/24
 */
@Getter
@AllArgsConstructor
public enum  RuleFlag {

    //单类别优惠券定义
    MANJIAN("满减券的计算规则"),
    ZHEKOU("折扣券的计算规则"),
    LIJIAN("立减券和计算规则"),

    //多类别优惠券定义
    MANJIAN_ZHEKOU("满减券+折扣券的计算规则");

    // TODO 更多优惠券类别组合
    /**
     * 规则的描述
     */
    private String desc;

}
