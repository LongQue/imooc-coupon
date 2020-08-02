package com.imooc.coupon;

import com.imooc.coupon.permission.PermissionClient;
import com.imooc.coupon.vo.CommonResponse;
import com.imooc.coupon.vo.CreatePathRequest;
import com.imooc.coupon.vo.PermissionInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>权限注册组件</h1>
 *
 * @author ZhouFufeng
 * @since 2020/8/2
 */
@Slf4j
public class PermissionRegistry {

    /**
     * 权限服务 SDK 客户端
     */
    private PermissionClient permissionClient;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * <h2>构造方法</h2>
     */
    PermissionRegistry(PermissionClient permissionClient, String serviceName) {
        this.permissionClient = permissionClient;
        this.serviceName = serviceName;
    }

    /**
     * <h2>权限注册</h2>
     */
    boolean register(List<PermissionInfo> infoList) {
        if (CollectionUtils.isEmpty(infoList)) {
            return false;
        }

        List<CreatePathRequest.PathInfo> pathInfos = infoList.stream()
                .map(info -> CreatePathRequest.PathInfo.builder()
                        .httpMethod(info.getMethod())
                        .pathName(info.getDesc())
                        .serviceName(serviceName)
                        .opMode(info.getIsRead() ? OpModeEnum.READ.getMode() : OpModeEnum.WRITE.getMode())
                        .build()
                ).collect(Collectors.toList());

        CommonResponse<List<Integer>> response = permissionClient.createPath(new CreatePathRequest(pathInfos));

        if (!CollectionUtils.isEmpty(response.getData())) {
            log.info("register path info: {}", response.getData());
            return true;
        }
        return false;
    }
}
