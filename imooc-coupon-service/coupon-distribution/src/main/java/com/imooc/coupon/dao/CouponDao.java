package com.imooc.coupon.dao;

import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * <h1>Coupon Dao 接口定义</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/19
 */
public interface CouponDao extends JpaRepository<Coupon,Integer> {

    /**
     * <h2>根据userId 和 status 查询优化器</h2>
     */
    List<Coupon> findAllByUserIdAAndStatus(Long userId, CouponStatus status);
}
