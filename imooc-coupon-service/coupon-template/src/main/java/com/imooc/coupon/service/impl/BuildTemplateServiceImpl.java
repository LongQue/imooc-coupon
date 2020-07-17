package com.imooc.coupon.service.impl;

import com.imooc.coupon.dao.CouponTemplateDao;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.IAsyncService;
import com.imooc.coupon.service.IBuildTemplateService;
import com.imooc.coupon.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <h1>构建优惠券模板接口实现</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/17
 */
@Slf4j
@Service
public class BuildTemplateServiceImpl implements IBuildTemplateService {
    private final IAsyncService asyncService;
    private final CouponTemplateDao templateDao;

    public BuildTemplateServiceImpl(IAsyncService asyncService, CouponTemplateDao templateDao) {
        this.asyncService = asyncService;
        this.templateDao = templateDao;
    }

    /**
     * <h2> 创建优惠券模板</h2>
     *
     * @param request {@link TemplateRequest} 模板信息请求对象
     * @return {@link CouponTemplate} 优惠券模板实体
     */
    @Override
    public CouponTemplate buildTemplate(TemplateRequest request) throws CouponException {
        //参数校验
        if (!request.validate()) {
            throw new CouponException("BuildTemplate Param Is Not Valid!");
        }
        //判断同名
        if (null != templateDao.findByName(request.getName())) {
            throw new CouponException("Exist Same Name Template!");
        }
        //构造CouponTemplate并保存数据库
        CouponTemplate template = requestToTemplate(request);
        template = templateDao.save(template);

        //根据优惠券模板异步生成优惠券码
        asyncService.asyncConstructCouponByTemplate(template);

        return template;
    }

    /**
     * <h2>将TemplateRequest转化为优惠券实体</h2>
     */
    private CouponTemplate requestToTemplate(TemplateRequest request) {
        return new CouponTemplate(
                request.getName(),
                request.getLogo(),
                request.getDesc(),
                request.getCategory(),
                request.getProductLine(),
                request.getCount(),
                request.getUserId(),
                request.getTarget(),
                request.getRule()
        );
    }
}
