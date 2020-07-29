package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>权限校验请求对象定义</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckPermissionRequest {

    private Long userId;
    private String uri;
    private String httpMethod;

}
