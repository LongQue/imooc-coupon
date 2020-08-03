package com.imooc.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * <h1>网关启动入口</h1>
 * 1、 @EnableZuulProxy 标识当前的应用是Zuul Server
 * 2、 @SpringCloudApplication 组合Spring应用+服务发现+熔断
 * @author ZhouFufeng
 * @since 2020/7/8
 **/
@EnableZuulProxy
// permission-sdk中写了feign接口
@EnableFeignClients
@SpringCloudApplication
public class ZuulGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZuulGatewayApplication.class, args);
    }
}
