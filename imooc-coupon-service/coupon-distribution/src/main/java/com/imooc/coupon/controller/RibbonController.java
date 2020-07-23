package com.imooc.coupon.controller;

import com.imooc.coupon.annotation.IgnoreResponseAdvice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * <h1>Ribbon 应用 Controller</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/23
 */
@Slf4j
@RestController
public class RibbonController {

    private final RestTemplate restTemplate;

    @Autowired
    public RibbonController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * <h2>通过Ribbon组件调用模板微服务</h2>
     * /coupon-distribution/info
     */
    @GetMapping("/info")
    @IgnoreResponseAdvice
    public TemplateInfo getTemplateInfo() {
        String infoUrl = "http://eureka-client-coupon-template/coupon-template/info";

        return restTemplate.getForEntity(
                infoUrl, TemplateInfo.class
        ).getBody();
    }
    /**
     * <h2>模板微服务的原信息</h2>
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class TemplateInfo {

        private Integer code;
        private String message;
        private List<Map<String,Object>> data;
    }
}
