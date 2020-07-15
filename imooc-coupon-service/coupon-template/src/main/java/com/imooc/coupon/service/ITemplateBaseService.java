package com.imooc.coupon.service;

import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.CouponTemplateSDK;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <h1>优惠券模板基础(view,delete...)服务定义</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/15
 **/
public interface ITemplateBaseService {

    /**
     * <h2>根据优惠券模板id获取模板信息</h2>
     *
     * @param id 模板id
     * @return {@link CouponTemplate}优惠券模板实体
     * @throws CouponException
     */
    CouponTemplate buildTemplateInfo(Integer id) throws CouponException;

    /**
     * <h2>查找所有可用的优惠券模板</h2>
     *
     * @return {@link CouponTemplateSDK}s
     */
    List<CouponTemplateSDK> findAllUsableTemplate();

    /**
     * <h2>获取模板ids到CouponTemplateSDK的映射</h2>
     * @param ids 模板ids
     * @return Map<key:模板id，value:CouponTemplateSDK></key:模板id，value:CouponTemplateSDK>
     */
    Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids);

}
