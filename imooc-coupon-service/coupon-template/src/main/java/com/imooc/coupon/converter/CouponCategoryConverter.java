package com.imooc.coupon.converter;

import com.imooc.coupon.constant.CouponCategory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * <h1>优惠券分类枚举属性转换器</h1>
 * AttributeConverter<X,Y>
 * X，实体类类型，Y数据库类型
 * @author ZhouFufeng
 * @since 2020/7/14
 **/
@Converter
public class CouponCategoryConverter implements AttributeConverter<CouponCategory,String> {

    /**
     * <h2>将实体属性X转换为Y存入数据库中，插入&更新</h2>
     */
    @Override
    public String convertToDatabaseColumn(CouponCategory couponCategory) {
        return couponCategory.getCode();
    }

    /**
     * <h2>将数据库中的字段Y转换为实体的X，查询</h2>
     */
    @Override
    public CouponCategory convertToEntityAttribute(String s) {
        return CouponCategory.of(s);
    }
}
