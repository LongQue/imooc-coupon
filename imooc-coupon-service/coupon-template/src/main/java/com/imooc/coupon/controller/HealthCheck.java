package com.imooc.coupon.controller;

import com.imooc.coupon.exception.CouponException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h1>健康检查接口</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/18
 */
@Slf4j
@RestController
public class HealthCheck {
    /**
     * 服务发现客户端
     */
    private final DiscoveryClient client;
    /**
     * 服务注册接口(当前服务)，提供获取服务id的方法
     */
    private final Registration registration;

    @Autowired
    public HealthCheck(DiscoveryClient client, Registration registration) {
        this.client = client;
        this.registration = registration;
    }

    /**
     * <h2>健康检查接口</h2>
     * 127.0.0.1:7001/coupon-template/health
     */
    @GetMapping("/health")
    public String health() {
        log.debug("view health api");
        return "CouponTemplate Is OK!";
    }

    /**
     * <h2>异常测试接口,测试统一响应</h2>
     * 127.0.0.1:7001/coupon-template/exception
     */
    @GetMapping("/exception")
    public String exception() throws CouponException {
        log.debug("view exception api");
        throw new CouponException("CouponTemplate Has Some Problem");
    }

    /**
     * <h2>获取Eureka Server 上的微服务原信息</h2>
     */
    @GetMapping("/info")
    public List<Map<String, Object>> info() {
      //大约需要两分钟时间才能获取到注册信息
        List<ServiceInstance> list = client.getInstances(registration.getServiceId());

        List<Map<String, Object>> result = new ArrayList<>(list.size());

        list.forEach(i->{
            Map<String, Object> info = new HashMap<>();
            info.put("serviceId", i.getServiceId());
            info.put("instanceId", i.getInstanceId());
            info.put("port", i.getPort());

            result.add(info);
        });

        return result;
    }
}
