package com.imooc.coupon.vo;

import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.constant.PeriodType;
import com.imooc.coupon.entity.Coupon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.time.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <h1>用户优惠券分类，根据优惠券状态</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponClassify {

    /**
     * 可以使用
     */
    private List<Coupon> usable;

    /**
     * 已使用的
     */
    private List<Coupon> used;

    /**
     * 已过期的
     */
    private List<Coupon> expired;

    /**
     * 对当前优惠券进行分类
     */
    public static CouponClassify classify(List<Coupon> coupons) {

        List<Coupon> usable = new ArrayList<>(coupons.size());
        List<Coupon> used = new ArrayList<>(coupons.size());
        List<Coupon> expired = new ArrayList<>(coupons.size());

        coupons.forEach(c->{
            //由于优惠券的过期是定时器延迟过期，所以需要判断优惠券是否过期
            boolean isTempExpired;
            long curTime = new Date().getTime();
            //固定过期时间
            if (c.getTemplateSDK().getRule().getExpiration().getPeriod().equals(
                    PeriodType.REGULAR.getCode()
            )) {
                isTempExpired = c.getTemplateSDK().getRule().getExpiration().getDeadline() < curTime;
            }else {
                //从领取日开始算
                isTempExpired = DateUtils.addDays(
                        c.getAssignTime(), c.getTemplateSDK().getRule().getExpiration().getGap()
                ).getTime() <= curTime;
            }
            if (c.getStatus() == CouponStatus.USED) {
                used.add(c);
            } else if (c.getStatus() == CouponStatus.EXPIRED||isTempExpired) {
                expired.add(c);
            }else{
                usable.add(c);
            }
        });
        return new CouponClassify(usable, used, expired);
    }
}
