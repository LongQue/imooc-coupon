package com.imooc.coupon.dao;

import com.imooc.coupon.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * <h1>CouponTemplate Dao</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/14
 **/
public interface CouponTemplateDao extends JpaRepository<CouponTemplate, Integer> {

    /**
     * <h2>根据模板名称查询模板</h2>
     * @param name 模板名称
     * @return
     */
    CouponTemplate findByName(String name);

    /**
     * <h2>根据 available和expired查找模板记录</h2>
     * @param available
     * @param expired
     * @return
     */
    List<CouponTemplate> findAllByAvailableAndExpired(Boolean available, Boolean expired);

    /**
     * <h2>根据 expired 查找模板记录</h2>
     * @param expired
     * @return
     */
    List<CouponTemplate> findAllByExAndExpired(Boolean expired);
}
