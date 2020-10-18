package com.imooc.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.annotation.IgnorePermission;
import com.imooc.coupon.annotation.ImoocCouponPermission;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.IBuildTemplateService;
import com.imooc.coupon.service.ITemplateBaseService;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <h1>优惠券模板相关的功能控制器</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/18
 */
@Slf4j
@RestController
public class CouponTemplateController {

    /**
     * 优惠券模板服务
     */
    private final IBuildTemplateService buildTemplateService;

    /**
     * 优惠券模板基础服务
     */
    private final ITemplateBaseService templateBaseService;

    @Autowired
    public CouponTemplateController(IBuildTemplateService buildTemplateService, ITemplateBaseService templateBaseService) {
        this.buildTemplateService = buildTemplateService;
        this.templateBaseService = templateBaseService;
    }

    /**
     * <h2>构建优惠券模板</h2>
     */
    @PostMapping("/template/build")
    @ImoocCouponPermission(desc = "buildTemplate",readOnly = false)
    public CouponTemplate buildTemplate(@RequestBody TemplateRequest request) throws CouponException {
        log.info("Build Template: {}", JSON.toJSONString(request));
        return buildTemplateService.buildTemplate(request);
    }

    /**
     * <h2>根据优惠券模板id获取模板信息</h2>
     */
    @GetMapping("/template/info")
    @ImoocCouponPermission(desc = "buildTemplateInfo")
    public CouponTemplate buildTemplateInfo(@RequestParam("id") Integer id) throws CouponException {
        log.info("Build Template Into For: {}", id);
        return templateBaseService.buildTemplateInfo(id);
    }

    /**
     * <h2>查找所有可用的优惠券模板</h2>
     */
    @GetMapping("/template/sdk/all")
    @IgnorePermission
    public List<CouponTemplateSDK> findAllUsableTemplate() {
        log.info("Find All Usable Template.");
        return templateBaseService.findAllUsableTemplate();
    }

    /**
     * <h2>根据ids查看所有的优惠券模板</h2>
     */
    @GetMapping("/template/sdk/infos")
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(@RequestParam("ids") Collection<Integer> ids) {
        log.info("FindIds2TemplateSDK: {}", JSON.toJSONString(ids));
        return templateBaseService.findIds2TemplateSDK(ids);
    }
}
