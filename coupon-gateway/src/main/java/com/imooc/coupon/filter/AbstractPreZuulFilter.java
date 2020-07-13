package com.imooc.coupon.filter;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

/**
 * @author ZhouFufeng
 * @since 2020/7/9
 **/
public abstract class AbstractPreZuulFilter extends AbstractZuulFilter {

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }
}
