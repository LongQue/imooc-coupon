package com.imooc.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.Constant;
import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <h1>Redis  相关操作服务接口实现</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/19
 */
@Slf4j
@Service
public class RedisServiceImpl implements IRedisService {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * <h2>根据 userId 和状态找到缓存的优惠券列表数据</h2>
     *
     * @param userId 用户id
     * @param status {@link com.imooc.coupon.constant.CouponStatus}
     * @return {@link Coupon}s,注意，可能返回null，代表从没有记录
     */
    @Override
    public List<Coupon> getCachedCoupons(Long userId, Integer status) {
        log.info("Get Coupon From Cache: {} ,{}", userId, status);
        String redisKey = status2RedisKey(status, userId);
        //返回为Object，转String，可能为空
        List<String> couponStatus = redisTemplate.opsForHash().values(redisKey)
                .stream()
                .map(o -> Objects.toString(o, null))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(couponStatus)) {
            saveEmptyCouponListToCache(userId, Collections.singletonList(status));
            return Collections.emptyList();
        }
        return couponStatus.stream()
                .map(cs -> JSON.parseObject(cs, Coupon.class))
                .collect(Collectors.toList());
    }

    /**
     * <h2>保存空的优惠券列表到缓存中</h2>
     * 目的：避免缓存穿透
     *
     * @param userId 用户 id
     * @param status 优惠券状态列表
     */
    @Override
    @SuppressWarnings("all")
    public void saveEmptyCouponListToCache(Long userId, List<Integer> status) {
        log.info("Save Empty List To Cache For User: {}, Status: {}", userId, JSON.toJSONString(status));

        // <coupon_id, json_coupon>
        Map<String, String> invalidCouponMap = new HashMap<>();

        invalidCouponMap.put("-1", JSON.toJSONString(Coupon.invalidCoupon()));

        //使用 SessionCallback 把数据命令放入到 Redis 的 pipeline
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                status.forEach(s -> {
                    String redisKey = status2RedisKey(s, userId);
                    operations.opsForHash().putAll(redisKey, invalidCouponMap);
                });
                return null;
            }
        };
        log.info("Pipeline Exe Result: {}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
    }

    /**
     * <h2>尝试从 Cache 中获取一个优惠券码</h2>
     *
     * @param templateId 优惠券模板主键
     * @return 优惠券码
     */
    @Override
    public String tryToAcquireCouponCodeFromCache(Integer templateId) {
        return null;
    }

    /**
     * <h2>将优惠券保存到 Cache 中</h2>
     *
     * @param userId  用户id
     * @param coupons {@link Coupon}s
     * @param status  优惠券状态
     * @return 保存成功的个数
     */
    @Override
    public Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status) throws CouponException {
        return null;
    }

    /**
     * <h2>根据 status 获取到对应的 redis key</h2>
     */
    private String status2RedisKey(Integer status, Long userId) {
        String redisKey = null;

        CouponStatus couponStatus = CouponStatus.of(status);

        switch (couponStatus) {
            case USABLE:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_USABLE);
                break;
            case USED:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_USED);
                break;
            case EXPIRED:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_EXPIRED);
                break;
        }
        return redisKey;
    }
}
