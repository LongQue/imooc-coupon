package com.imooc.coupon.vo;

import lombok.Data;

/**
 * <h1>接口权限信息组装类定义</h1>
 *
 * @author ZhouFufeng
 * @since 2020/8/1
 */
@Data
public class PermissionInfo {

    /**
     * Controller Url
     */
    private String url;

    /**
     * 方法类型
     */
    private String method;

    /**
     * 是否只读
     */
    private Boolean isRead;

    /**
     * 描述信息
     */
    private String desc;

    /**
     * 扩展属性
     */
    private String extra;

    @Override
    public String toString() {
        return "url = " + url
                + ", method = " + method
                + ", isRead = " + isRead
                + ", desc = " + desc;
    }
}
