package imooc.coupon.settlement.executor.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.CouponCategory;
import com.imooc.coupon.vo.GoodsInfo;
import com.imooc.coupon.vo.SettlementInfo;
import imooc.coupon.settlement.constant.RuleFlag;
import imooc.coupon.settlement.executor.AbstractExecutor;
import imooc.coupon.settlement.executor.RuleExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>满减+折扣优惠券结算规则执行器</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/26
 */
@Slf4j
@Component
public class ManJianZheKouExecutor extends AbstractExecutor implements RuleExecutor {

    /**
     * <h2>校验商品类型是否与优惠券匹配</h2>
     * 注意：
     * 1、多品类优惠券重载此方法
     * 2、如果想要多个优惠券，则必须所有的商品类型必须包含在内，即差集为空
     *
     * @param settlement {@link SettlementInfo} 用户传递的计算信息
     */
    @Override
    @SuppressWarnings("all")
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlement) {
        log.debug("Check ManJian And ZheKou Is Match Or Not!");
        //获取商品类型一个商品一个类型
        List<Integer> goodsType = settlement.getGoodsInfos().stream()
                .map(GoodsInfo::getType).collect(Collectors.toList());
        List<Integer> templateGoodsType = new ArrayList<>();
        //获取优惠券可用的商品类型
        settlement.getCouponAndTemplateInfos().forEach(ct -> {
            templateGoodsType.addAll(JSON.parseObject(
                    ct.getTemplate().getRule().getUsage().getGoodsType(),
                    List.class
            ));
        });

        //如果想要多个优惠券，则必须所有的商品类型必须包含在内，即差集为空
        //差集 subtract(param1,param2) 属于param1不属于param2的
        return CollectionUtils.isEmpty(CollectionUtils.subtract(
                goodsType, templateGoodsType
        ));
    }

    /**
     * <h2>规则类型标记</h2>
     *
     * @return {@link RuleFlag}
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.MANJIAN_ZHEKOU;
    }

    /**
     * <h2>优惠券规则计算</h2>
     *
     * @param settlement {@link SettlementInfo} 包含了选择的优惠券
     * @return {@link SettlementInfo} 修正过的结算信息
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {
        double goodsSum = retain2Decimals(goodsCostSum(
                settlement.getGoodsInfos()
        ));
        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settlement, goodsSum
        );
        if (null != probability) {
            log.debug("ManJian And ZheKou Template Is Not Match To GoodsType!");
            return probability;
        }
        SettlementInfo.CouponAndTemplateInfo manJian = null;
        SettlementInfo.CouponAndTemplateInfo zheKou = null;

        for (SettlementInfo.CouponAndTemplateInfo ct : settlement.getCouponAndTemplateInfos()) {
            if (CouponCategory.of(ct.getTemplate().getCategory()) == CouponCategory.MANJIAN) {
                manJian = ct;
            } else {
                zheKou = ct;
            }
        }

        assert null != manJian;
        assert null != zheKou;

        //当前的优惠券和满减券如果不能共用(一起使用),清空优惠券，返回商品原价
        if (!isTemplateCanShared(manJian, zheKou)) {
            log.debug("Current ManJian And ZheKou Can Not Shared");
            settlement.setCost(goodsSum);
            settlement.setCouponAndTemplateInfos(Collections.emptyList());
            return settlement;
        }

        List<SettlementInfo.CouponAndTemplateInfo> ctInfos = new ArrayList<>();
        double manJianBase = manJian.getTemplate().getRule()
                .getDiscount().getBase();
        double manJianQuota = manJian.getTemplate().getRule()
                .getDiscount().getQuota();

        //最终价格,先满减再折扣
        double targetSum = goodsSum;
        if (targetSum >= manJianBase) {
            targetSum -= manJianQuota;
            ctInfos.add(manJian);
        }

        //再计算折扣
        double zheKouQuota = zheKou.getTemplate().getRule()
                .getDiscount().getQuota();
        targetSum *= zheKouQuota * 1.0 / 100;
        ctInfos.add(zheKou);

        settlement.setCouponAndTemplateInfos(ctInfos);
        settlement.setCost(retain2Decimals(Math.max(targetSum, minCost())
        ));
        log.debug("Use ManJian And ZheKou Coupon Make Goods Cost From {} To {}", goodsSum, settlement.getCost());
        return null;
    }

    /**
     * <h2>当前的两张优惠券是否可以共用</h2>
     * 即校验 TemplateRule 中的weight是否满足条件
     */
    private boolean isTemplateCanShared(SettlementInfo.CouponAndTemplateInfo manJian,
                                        SettlementInfo.CouponAndTemplateInfo zheKou) {
        //拼接四位id
        String manJianKey = manJian.getTemplate().getKey() +
                String.format("%04d", manJian.getTemplate().getId());

        String zheKouKey = zheKou.getTemplate().getKey() +
                String.format("%04d", zheKou.getTemplate().getId());

        List<String> allSharedKeysForManJian = new ArrayList<>();
        allSharedKeysForManJian.add(manJianKey);
        //weight可与哪些券一起使用
        allSharedKeysForManJian.addAll(JSON.parseObject(
                manJian.getTemplate().getRule().getWeight(),
                List.class
        ));

        List<String> allSharedKeysForZheKou = new ArrayList<>();
        allSharedKeysForZheKou.add(zheKouKey);
        allSharedKeysForZheKou.addAll(JSON.parseObject(
                zheKou.getTemplate().getRule().getWeight(),
                List.class
        ));
        //前者是否是后者自己
        return CollectionUtils.isSubCollection(Arrays.asList(manJianKey, zheKouKey), allSharedKeysForManJian) ||
                CollectionUtils.isSubCollection(Arrays.asList(manJianKey, zheKouKey), allSharedKeysForZheKou);
    }
}
