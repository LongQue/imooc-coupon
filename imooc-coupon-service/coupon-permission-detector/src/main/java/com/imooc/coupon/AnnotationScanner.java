package com.imooc.coupon;

import com.imooc.coupon.annotation.IgnorePermission;
import com.imooc.coupon.annotation.ImoocCouponPermission;
import com.imooc.coupon.vo.PermissionInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;
import java.util.*;

/**
 * <h1>接口权限信息扫描器</h1>
 *
 * @author ZhouFufeng
 * @since 2020/8/1
 */
@Slf4j
public class AnnotationScanner {

    private String pathPrefix;

    private static final String IMOOC_COUPON_PKG = "com.imooc.coupon";

    AnnotationScanner(String prefix) {
        this.pathPrefix = prefix;
    }

    /**
     * <h2>构造 Controller 的权限信息</h2>
     *
     * @param mapInfo       {@link RequestMappingInfo} @RequestMapping 对应信息
     * @param handlerMethod {@link HandlerMethod} @RequestMapping 对应方法详情信息，包括方法、类、参数
     */
    private List<PermissionInfo> buildPermission(
            RequestMappingInfo mapInfo, HandlerMethod handlerMethod
    ) {
        Method javaMethod = handlerMethod.getMethod();
        Class<?> baseClass = javaMethod.getDeclaringClass();

        //忽略非 com.imooc.coupon 下的Mapping
        if (!isImoocCouponPackage(baseClass.getName())) {
            log.debug("ignore method: {}", javaMethod.getName());
            return Collections.emptyList();
        }
        //判断你是否需要忽略此方法
        IgnorePermission ignorePermission = javaMethod.getAnnotation(
                IgnorePermission.class
        );
        if (null != ignorePermission) {
            log.debug("ignore method: {}", javaMethod.getName());
        }

        //取出权限注解
        ImoocCouponPermission couponPermission = javaMethod.getAnnotation(
                ImoocCouponPermission.class
        );
        if (null == couponPermission) {
            //如果没有 ImoocCouponPermission 且没有 IgnorePermission，在日志中记录
            log.error("lack @ImoocCouponPermission -> {}#{}",
                    javaMethod.getDeclaringClass().getName(),
                    javaMethod.getName());
            return Collections.emptyList();
        }

        // 说明有ImoocCouponPermission,取出URL
        //@RequestMapping支持多个url映射一个方法
        Set<String> urlSet = mapInfo.getPatternsCondition().getPatterns();

        //取出method
        boolean isAllMethods = false;
        Set<RequestMethod> methodSet = mapInfo.getMethodsCondition().getMethods();
        if (CollectionUtils.isEmpty(methodSet)) {
            isAllMethods = true;
        }

        List<PermissionInfo> infoList = new ArrayList<>();

        for (String url : urlSet) {
            //支持的 http method 为全量
            if (isAllMethods) {
                PermissionInfo info = buildPermissionInfo(
                        HttpMethodEnum.ALL.name(),
                        javaMethod.getName(),
                        this.pathPrefix + url,
                        couponPermission.readOnly(),
                        couponPermission.desc(),
                        couponPermission.extra()
                );
                infoList.add(info);
                continue;
            }
            //支持部分 http method
            for (RequestMethod method : methodSet) {
                PermissionInfo info = buildPermissionInfo(
                        method.name(),
                        javaMethod.getName(),
                        this.pathPrefix + url,
                        couponPermission.readOnly(),
                        couponPermission.desc(),
                        couponPermission.extra()
                );
                infoList.add(info);
                log.info("permission detected: {}", info);
            }
        }
        return infoList;
    }

    /**
     * <h2>构造所有 controller 的权限信息</h2>
     */
    List<PermissionInfo> scanPermission(
            Map<RequestMappingInfo, HandlerMethod> mappingMap
    ) {
        List<PermissionInfo> result = new ArrayList<>();
        mappingMap.forEach((mapInfo, method) ->
                result.addAll(buildPermission(mapInfo, method)));
        return result;
    }

    /**
     * <h2>构造单个接口的权限信息</h2>
     */
    private PermissionInfo buildPermissionInfo(
            String reqMethod,
            String javaMethod,
            String path,
            boolean readOnly,
            String desc,
            String extra) {
        PermissionInfo info = new PermissionInfo();
        info.setMethod(reqMethod);
        info.setUrl(path);
        info.setIsRead(readOnly);
        info.setDesc(desc);
        //如果注解中没有描述则使用方法名
        info.setExtra(StringUtils.isEmpty(desc) ? javaMethod : desc);
        info.setExtra(extra);
        return info;
    }

    /**
     * <h2>判断当前类是否在定义的包中</h2>
     */
    private boolean isImoocCouponPackage(String className) {
        return className.startsWith(IMOOC_COUPON_PKG);
    }

    /**
     * <h2>保证 path 以 / 开头，且不以 / 结尾</h2>
     * 如果 user -> /user , /user/ -> /user
     */
    private String trimPath(String path) {
        if (StringUtils.isEmpty(path)) {
            return "";
        }

        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }
}
