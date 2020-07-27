package imooc.coupon.settlement.executor;

import com.imooc.coupon.constant.CouponCategory;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.SettlementInfo;
import imooc.coupon.settlement.constant.RuleFlag;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.digester.Rule;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h1>优惠券结算规则执行管理器</h1>
 * 即根据用户的骑牛(SettlementInfo)找到对应的Executor，去做结算
 * BeanPostProcessor:Bean 后置处理器，该类所有bean被spring创建后才调用BeanPostProcessor对应的方法
 * @author ZhouFufeng
 * @since 2020/7/27
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class ExecuteManager implements BeanPostProcessor {

    /**
     * 规则执行期刊映射
     */
    private static Map<RuleFlag, RuleExecutor> executorIndex =
            new HashMap<>(RuleFlag.values().length);

    /**
     * <h2>优惠券结算规则计算入口</h2>
     * 注意：一定要保证传递进来的优惠券个数>=1
     */
    public SettlementInfo computeRule(SettlementInfo settlement) throws CouponException {
        SettlementInfo result = null;

        //单类优惠券
        if (settlement.getCouponAndTemplateInfos().size() == 1) {
            CouponCategory category = CouponCategory.of(
                    settlement.getCouponAndTemplateInfos().get(0).getTemplate().getCategory()
            );

            switch (category) {
                case MANJIAN:
                    result = executorIndex.get(RuleFlag.MANJIAN).computeRule(settlement);
                    break;
                case LIJIAN:
                    result = executorIndex.get(RuleFlag.LIJIAN).computeRule(settlement);
                    break;
                case ZHEKOU:
                    result = executorIndex.get(RuleFlag.ZHEKOU).computeRule(settlement);
                    break;
            }
        }else {
            //多类优惠券
            List<CouponCategory> categorys = new ArrayList<>(settlement.getCouponAndTemplateInfos().size());
            settlement.getCouponAndTemplateInfos().forEach(ct ->
                    categorys.add(CouponCategory.of(
                            ct.getTemplate().getCategory()
                    )));
            if (categorys.size() != 2) {
                throw new CouponException("Not Support For More Template Category");
            } else {
                if (categorys.contains(CouponCategory.MANJIAN) && categorys.contains(CouponCategory.ZHEKOU)) {
                    result = executorIndex.get(RuleFlag.MANJIAN_ZHEKOU).computeRule(settlement);
                } else {
                    throw new CouponException("Not Support For Other Template Catgegory");
                }
            }
        }
        return result;
    }
    /**
     * <h2>在 bean  初始化之前去执行(before)</h2>
     * 创建和初始化是两回事
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof RuleExecutor)) {
            return bean;
        }

        RuleExecutor executor = (RuleExecutor) bean;
        RuleFlag ruleFlag = executor.ruleConfig();
        if (executorIndex.containsKey(ruleFlag)) {
            throw new IllegalStateException("There is already an executor for rule flag: " + ruleFlag);
        }
        log.info("Load executor {} for rule flag {}.", executor.getClass(), ruleFlag);
        executorIndex.put(ruleFlag, executor);
        return null;
    }

    /**
     * <h2>在 bean 初始化之后执行</h2>
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        return bean;
    }
}
