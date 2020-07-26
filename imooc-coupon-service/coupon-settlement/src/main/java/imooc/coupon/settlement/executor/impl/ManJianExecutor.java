package imooc.coupon.settlement.executor.impl;

import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.SettlementInfo;
import imooc.coupon.settlement.constant.RuleFlag;
import imooc.coupon.settlement.executor.AbstractExecutor;
import imooc.coupon.settlement.executor.RuleExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * <h1>满减优惠券结算规则执行器</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/26
 */
@Slf4j
@Component
public class ManJianExecutor extends AbstractExecutor implements RuleExecutor {

    /**
     * <h2>规则类型标记</h2>
     *
     * @return {@link RuleFlag}
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.MANJIAN;
    }

    /**
     * <h2>优惠券规则计算</h2>
     *
     * @param settlement {@link SettlementInfo} 包含了选择的优惠券
     * @return {@link SettlementInfo} 修正过的结算信息
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {
        double goodsSum = retain2Decimals(
                goodsCostSum(settlement.getGoodsInfos())
        );
        //判断优惠券类型是否与商品类型匹配，如果匹配返回null，如果不匹配设置原总价并清空优惠券
        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settlement, goodsSum
        );

        if (null != probability) {
            log.debug("ManJian Template Is Not Match To GoodsType!");
            return probability;
        }

        //判断满减是否符合折扣标准
        CouponTemplateSDK templateSDK = settlement.getCouponAndTemplateInfos().get(0).getTemplate();

        //满减卷未达到价格
        double base = templateSDK.getRule().getDiscount().getBase();
        double quota = templateSDK.getRule().getDiscount().getQuota();

        //如果不符合标准则直接返回商品总价
        if (goodsSum < base) {
            log.debug("Current Goods Cost Sum < ManJian Coupon Base!");
            //返回原价
            settlement.setCost(goodsSum);
            settlement.setCouponAndTemplateInfos(Collections.emptyList());
            return settlement;
        }

        //计算使用优惠券之后的价格
        settlement.setCost(retain2Decimals(goodsSum - quota) > minCost() ? (goodsSum - quota) : minCost());
        log.debug("Use ManJian Coupon Make Goods Cost From {} To {}", goodsSum, settlement.getCost());
        return settlement;
    }
}
