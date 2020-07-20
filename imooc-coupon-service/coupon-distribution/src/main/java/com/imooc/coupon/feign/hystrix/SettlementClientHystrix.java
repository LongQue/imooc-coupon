package com.imooc.coupon.feign.hystrix;

import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.feign.SettlementClient;
import com.imooc.coupon.vo.CommonResponse;
import com.imooc.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <h1>优惠券结算 Fegin 接口的熔断降级策略</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/20
 */
@Slf4j
@Component
public class SettlementClientHystrix implements SettlementClient {

    /**
     * <h2>优惠券规则计算</h2>
     */
    @Override
    public CommonResponse<SettlementInfo> computeRule(SettlementInfo settlement) throws CouponException {
        log.error("[eureka-client-coupon-settlement] computeRule request error");
        settlement.setEmploy(false);
        settlement.setCost(-1.0);
        return new CommonResponse<>(
                -1,
                "[eureka-client-coupon-settlement] computeRule request error",
                settlement
        );
    }
}
