package com.imooc.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.Constant;
import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
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

        String redisKey = String.format("%s%s", Constant.RedisPrefix.COUPON_TEMPLATE, templateId);

        //不存在顺序关系，left或者right无影响,可能为null
        String couponCode = redisTemplate.opsForList().leftPop(redisKey);

        log.info("Acquire Coupon Code： {}， {}， {}", templateId, redisKey, couponCode);

        return couponCode;
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

        log.info("Add Coupon To Cache: {},{},{}", userId, JSON.toJSONString(coupons), status);

        Integer result = -1;
        CouponStatus couponStatus = CouponStatus.of(status);
        switch (couponStatus) {
            case USABLE:
                result = addCouponToCacheForUsable(userId, coupons);
                break;
            case USED:
                result = addCouponToCacheForUsed(userId, coupons);
                break;
            case EXPIRED:
                result = addCouponToCacheForExpired(userId, coupons);
                break;
        }
        return result;
    }

    /**
     * <h2>根据 status 获取到对应的 redis key</h2>
     */
    private String status2RedisKey(Integer status, Long userId) {
        String redisKey = null;

        CouponStatus couponStatus = CouponStatus.of(status);

        switch (couponStatus) {
            case USABLE:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_USABLE,userId);
                break;
            case USED:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_USED,userId);
                break;
            case EXPIRED:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_EXPIRED,userId);
                break;
        }
        return redisKey;
    }

    /**
     * <h2>获取一个随机的过期时间</h2>
     * 缓存雪崩： key在同一时间失效
     * @param min 最小小时数
     * @param max 最大小时数
     * @return 返回 [min,max] 之间的随机
     */
    private Long getRandomExpirationTime(Integer min, Integer max) {
        return RandomUtils.nextLong(
                min*60*60,
                max*60*60
        );
    }

    /**
     * <h2>新增加优惠券到 Cache 中</h2>
     */
    private Integer addCouponToCacheForUsable(Long userId, List<Coupon> coupons) {
        // 如果 status 是USABLE，代表是新增加的优惠券
        // 只会影响一个 Cache: USER_COUPON_USABLE
        log.debug("Add Coupon To Cache For Usable.");

        Map<String, String> needCachedObject = new HashMap<>();
        coupons.forEach(c->needCachedObject.put(c.getId().toString(),JSON.toJSONString(c)));

        String redisKey = status2RedisKey(CouponStatus.USABLE.getCode(), userId);

        redisTemplate.opsForHash().putAll(redisKey, needCachedObject);
        log.info("Add {} Coupon To Cache : {}, {}", needCachedObject.size(), userId, redisKey);

        //给key设置过期时间
        redisTemplate.expire(redisKey, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);

        return needCachedObject.size();
    }

    /**
     * <h2>将已使用的优惠券加入到 Cache 中</h2>
     */
    @SuppressWarnings("all")
    private Integer addCouponToCacheForUsed(Long userId, List<Coupon> coupons) throws CouponException{
        // 如果 status 是USED，代表用户操作是使用当前的优惠券，影响到两个Cache
        // USABLE可用，USED使用

        log.debug("Add Coupon To Cache For Used.");

        Map<String, String> needCacheForUsed = new HashMap<>();

        String redisKeyForUsable = status2RedisKey(
                CouponStatus.USABLE.getCode(), userId
        );
        String redisKeyForUsed = status2RedisKey(
                CouponStatus.USED.getCode(), userId
        );

        //获取当前用户可用的优惠券
        List<Coupon> curUsableCoupons = getCachedCoupons(userId, CouponStatus.USABLE.getCode());
        //当前可用的优惠券一定是大于1(存在无效优惠券)
        assert curUsableCoupons.size() > coupons.size();

        coupons.forEach(c -> needCacheForUsed.put(c.getId().toString(), JSON.toJSONString(c)));

        //当前的优惠券(redis可用)参数
        List<Integer> curUsableIds = curUsableCoupons.stream().map(Coupon::getId).collect(Collectors.toList());
        //已用优惠券
        List<Integer> paramIds = coupons.stream().map(Coupon::getId).collect(Collectors.toList());
        //(A,B)  判断A是否是B的子集
        if (!CollectionUtils.isSubCollection(paramIds, curUsableIds)) {
            log.error("CurCoupons Is Not Equal ToCache: {}, {}, {}",
                    userId,JSON.toJSONString(curUsableCoupons),
                    JSON.toJSONString(paramIds));
            throw new CouponException("CurCoupon Is Not Equal To Cache");
        }

        List<String> needCleanKey = paramIds.stream().map(i -> i.toString()).collect(Collectors.toList());

        SessionCallback<Objects> sessionCallback = new SessionCallback<Objects>() {
            @Override
            public  Objects execute(RedisOperations operations) throws DataAccessException {
                //1.已使用的优惠券Cache缓存添加
                operations.opsForHash().putAll(redisKeyForUsed, needCacheForUsed);
                //2.可用的优惠券Cache需要清理
                operations.opsForHash().delete(redisKeyForUsable,needCleanKey.toArray());
                //3.重置过期时间
                operations.expire(redisKeyForUsable, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                operations.expire(redisKeyForUsed, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                return null;
            }
        };
        log.info("Pipeline Exe Result: {}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
        return coupons.size();
    }

    @SuppressWarnings("all")
    private Integer addCouponToCacheForExpired(Long userId, List<Coupon> coupons) throws CouponException {
        //status 是 EXPIRED,代表是已有的优惠券过期了，影响到两个Cache
        //USABLE,EXPIRED
        log.debug("Add Coupon To Cache For Expired");

        //最终需要保存的Cache
        Map<String, String> needCacheForExpired = new HashMap<>(coupons.size());

        String redisKeyForUsable = status2RedisKey(
                CouponStatus.USABLE.getCode(), userId
        );
        String redisKeyForExpired = status2RedisKey(
                CouponStatus.EXPIRED.getCode(), userId
        );
        List<Coupon> curUsableCoupons = getCachedCoupons(
                userId,CouponStatus.USABLE.getCode()
        );

        //当前可用的优惠券个数一定是大于1的
        assert curUsableCoupons.size() > 1;

        coupons.forEach(c -> needCacheForExpired.put(c.getId().toString(), JSON.toJSONString(c)));

        //校验当前优惠券参数是否与Cache中的匹配
        List<Integer> curUsableIds = curUsableCoupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        List<Integer> paramId = coupons.stream()
                .map(Coupon::getId)
                .collect(Collectors.toList());

        if (!CollectionUtils.isSubCollection(paramId, curUsableIds)) {
            log.error("CurCoupons Is Not Equal To Cache: {}, {}, {}",
                    userId,JSON.toJSONString(curUsableIds),
                    JSON.toJSONString(paramId));
            throw new CouponException("CurCoupon Is Not Equal To Cache");
        }

        List<String> needCleanKey = paramId.stream()
                .map(i->i.toString()).collect(Collectors.toList());

        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //1.已过期的优惠券Cache缓存
                operations.opsForHash().putAll(
                        redisKeyForExpired, needCacheForExpired
                );

                //2.可用优惠券Cache需要清理
                operations.opsForHash().delete(
                        redisKeyForUsable, needCleanKey.toArray()
                );

                //3.重置过期时间
                operations.expire(
                        redisKeyForUsable,
                        getRandomExpirationTime(1, 2),
                        TimeUnit.SECONDS
                );

                operations.expire(
                        redisKeyForExpired,
                        getRandomExpirationTime(1, 2),
                        TimeUnit.SECONDS
                );

                return null;
            }
        };

        log.info("Pipeline Exe Result: {}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));

        return coupons.size();
    }
}
