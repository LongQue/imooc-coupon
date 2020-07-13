package com.imooc.coupon.filter;

import com.netflix.zuul.constants.ZuulConstants;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

/**
 * @author ZhouFufeng
 * @since 2020/7/9
 **/
public abstract class AbstractPostZuulFilter extends AbstractZuulFilter {

    @Override
    public String filterType() {
        return FilterConstants.POST_TYPE;
    }
}
