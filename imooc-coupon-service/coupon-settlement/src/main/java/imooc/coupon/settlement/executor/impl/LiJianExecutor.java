package imooc.coupon.settlement.executor.impl;

import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.SettlementInfo;
import imooc.coupon.settlement.constant.RuleFlag;
import imooc.coupon.settlement.executor.AbstractExecutor;
import imooc.coupon.settlement.executor.RuleExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <h1>立减优惠券结算规则执行器</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/26
 */
@Slf4j
@Component
public class LiJianExecutor extends AbstractExecutor implements RuleExecutor {
    /**
     * <h2>规则类型标记</h2>
     *
     * @return {@link RuleFlag}
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.LIJIAN;
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
            log.debug("LiJian Template Is Not Match To GoodsType!");
            return probability;
        }

        //立减优惠券直接使用，没有门槛
        CouponTemplateSDK templateSDK = settlement.getCouponAndTemplateInfos().get(0).getTemplate();
        double quota = templateSDK.getRule().getDiscount().getQuota();

        //计算使用优惠券之后的价格
        settlement.setCost(Math.max(retain2Decimals(goodsSum - quota), minCost()));

        log.debug("Use LiJian Coupon Make Goods Cost From {} To {}", goodsSum, settlement.getCost());

        return settlement;
    }
}
