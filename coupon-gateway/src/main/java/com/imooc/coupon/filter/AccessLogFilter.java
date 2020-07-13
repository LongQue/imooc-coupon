package com.imooc.coupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ZhouFufeng
 * @since 2020/7/9
 **/
@Slf4j
@Component
public class AccessLogFilter extends AbstractPostZuulFilter{

    @Override
    protected Object cRun() {
        HttpServletRequest request = context.getRequest();

        //从PreRequestFilter中获取设置的请求时间戳

        Long startTime = (Long) context.get("startTime");
        String uri = request.getRequestURI();
        long duration = System.currentTimeMillis() - startTime;

        log.info("uri: {}, duration: {}",uri,duration);
        return null;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.SEND_RESPONSE_FILTER_ORDER;
    }
}
