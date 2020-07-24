package imooc.coupon.settlement.executor;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.vo.GoodsInfo;
import com.imooc.coupon.vo.SettlementInfo;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>规则执行器抽象类，定义通用方法</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/24
 */
public abstract class AbstractExecutor {

    /**
     * <h2>校验商品类型是否与优惠券皮诶</h2>
     * 注意：
     * 1、这里实现的但疲累优惠券的校验，多品类优惠券重载此方法
     * 2、商品只需要有一个优惠券要求的商品类型去匹配即可
     */
    @SuppressWarnings("all")
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlement) {
        List<Integer> goodsType = settlement.getGoodsInfos()
                .stream().map(GoodsInfo::getType)
                .collect(Collectors.toList());

        List<Integer> templateGoodsType = JSON.parseObject(
                settlement.getCouponAndTemplateInfos().get(0).getTemplate()
                        .getRule().getUsage().getGoodsType(), List.class);

        //存在交集即可
        return CollectionUtils.isNotEmpty(
                CollectionUtils.intersection(goodsType, templateGoodsType)
        );
    }

    /**
     * <h2>处理商品类型与优惠券限制不匹配的情况</h2>
     * @param settlement {@link SettlementInfo} 用户传递的结算信息
     * @param goodsSum 商品总结
     * @return {@link SettlementInfo} 已经修改过的结算信息
     */
    protected SettlementInfo processGoodsTypeNotSatisfy(
            SettlementInfo settlement, double goodsSum
    ) {
        boolean isGoodsTypeSatisfy = isGoodsTypeSatisfy(settlement);

        //当商品类型不满足时，直接返回总价，并情况优惠券
        if (!isGoodsTypeSatisfy) {
            settlement.setCost(goodsSum);
            settlement.setCouponAndTemplateInfos(Collections.emptyList());
            return settlement;
        }

        return null;
    }

    /**
     * <h2>商品总价</h2>
     */
    protected double goodsCostSum(List<GoodsInfo> goodsInfos) {
        return goodsInfos.stream().mapToDouble(
                g -> g.getPrice() * g.getCount()
        ).sum();
    }

    /**
     * <h2>保留两位小数</h2>
     */
    protected double retain2Decimals(double value) {
        return new BigDecimal(value).setScale(
                2, BigDecimal.ROUND_HALF_UP
        ).doubleValue();
    }

    /**
     * <h2>最小支付费用</h2>
     */
    protected double minCost() {
        return 0.1;
    }
}
