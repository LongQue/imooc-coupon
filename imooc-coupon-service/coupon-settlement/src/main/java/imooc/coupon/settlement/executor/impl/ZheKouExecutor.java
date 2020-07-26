package imooc.coupon.settlement.executor.impl;

import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.SettlementInfo;
import imooc.coupon.settlement.constant.RuleFlag;
import imooc.coupon.settlement.executor.AbstractExecutor;
import imooc.coupon.settlement.executor.RuleExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <h1>折扣优惠券结算规则执行器</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/26
 */
@Slf4j
@Component
public class ZheKouExecutor extends AbstractExecutor implements RuleExecutor {
    /**
     * <h2>规则类型标记</h2>
     *
     * @return {@link RuleFlag}
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.ZHEKOU;
    }

    /**
     * <h2>优惠券规则计算</h2>
     *
     * @param settlement {@link SettlementInfo} 包含了选择的优惠券
     * @return {@link SettlementInfo} 修正过的结算信息
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {

        double goodsSum = retain2Decimals(goodsCostSum(settlement.getGoodsInfos()));

        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settlement, goodsSum
        );
        if (null != probability) {
            log.debug("ZheKou Template Is Not Match GoodsType!");
            return null;
        }

        //折扣优惠券可以直接使用，没有门槛
        CouponTemplateSDK templateSDK = settlement.getCouponAndTemplateInfos().get(0).getTemplate();

        double quota = templateSDK.getRule().getDiscount().getQuota();

        settlement.setCost(
                Math.max(retain2Decimals((goodsSum * (quota * 1.0 / 100))), minCost())
        );

        log.debug("Use ZheKou Coupon Make Goods Cost From {} To {}", goodsSum, settlement.getCost());
        return settlement;

    }
}
