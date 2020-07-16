package com.imooc.coupon.service.impl;

import com.google.common.base.Stopwatch;
import com.imooc.coupon.constant.Constant;
import com.imooc.coupon.dao.CouponTemplateDao;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.service.IAsyncService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <h1>异步服务接口实现</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/16
 */
@Slf4j
@Service
public class AsyncServiceImpl implements IAsyncService {


    private final CouponTemplateDao templateDao;

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public AsyncServiceImpl(CouponTemplateDao templateDao, StringRedisTemplate redisTemplate) {
        this.templateDao = templateDao;
        this.redisTemplate = redisTemplate;
    }

    /**
     * <h2>根据模板异步的创建优惠券码</h2>
     *
     * @param template {@link CouponTemplate} 优惠券模板实体
     */
    @Override
    @Async("getAsyncExecutor")
    @SuppressWarnings("all")
    public void asyncConstructCouponByTemplate(CouponTemplate template) {

        Stopwatch watch = Stopwatch.createStarted();

        Set<String> couponCodes = buildCouponbCode(template);

        String redisKey = String.format("%s%s", Constant.RedisPrefix.COUPON_TEMPLATE, template.getId().toString());

        log.info("Push CouponCode To Redis: {}", redisTemplate.opsForList().rightPushAll(redisKey, couponCodes));
        //已生成可使用
        template.setAvailable(true);
        templateDao.save(template);

        watch.stop();
        log.info("Construct CouponCode By Template Cost: {}ms", watch.elapsed(TimeUnit.MILLISECONDS));


        //TODO 发送短信or邮件通知优惠券模板可用
        log.info("CouponTemplate({}) is available!", template.getId());
    }


    /**
     * <h2>构造优惠券码</h2>
     * 优惠券码(对应于每一张优惠券，18位)
     * 前四位：产品线+类型
     * 中间6位日期 随机排
     * 后8位 0-9，随机数构成
     *
     * @param template {@link CouponTemplate} 优惠券模板
     * @return Set<String> 与template.count 相同个数的优惠券码
     */
    @SuppressWarnings("all")
    private Set<String> buildCouponbCode(CouponTemplate template) {
        Stopwatch watch = Stopwatch.createStarted();
        Set<String> result = new HashSet<>(template.getCount());

        //前四位
        String prefix4 = template.getProductLine().getCode().toString()
                + template.getCategory().getCode().toString();

        String date = new SimpleDateFormat("yyMMdd")
                .format(template.getCreateTime());

        for (int i = 0; i != template.getCount(); i++) {
            //Set避免重复
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }

        while (result.size() < template.getCount()) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }

        assert result.size() == template.getCount();
        watch.stop();
        log.info("build Coupon Code Cost: {}ms", watch.elapsed(TimeUnit.MILLISECONDS));

        return result;
    }

    /**
     * <h2>构造优惠券的后14位</h2>
     *
     * @param date 创建优惠券的日期
     * @return 14位优惠券码
     */
    private String buildCouponCodeSuffix14(String date) {

        //不希望第一个是0
        char[] bases = new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9'};
        //中间6位
        List<Character> chars = date.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
        //随机打乱
        Collections.shuffle(chars);
        //转String然后join拼接
        String mid6 = chars.stream().map(Object::toString).collect(Collectors.joining());

        //后八位,从char随机出第一位，后7位从0-9随机
        String suffix8 = RandomStringUtils.random(1, bases) + RandomStringUtils.randomNumeric(7);
        return mid6 + suffix8;
    }

}
