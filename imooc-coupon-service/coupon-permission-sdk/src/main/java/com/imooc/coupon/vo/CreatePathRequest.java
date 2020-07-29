package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <h1>路径创建请求对象定义</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePathRequest {

    private List<PathInfo> pathInfos;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PathInfo {
        /**
         * 路劲模式
         */
        private String pathPattern;

        /**
         * HTTP 方法类型
         */
        private String httpMethod;

        /**
         * 路径名称
         */
        private String pathName;

        /**
         * 服务名称
         */
        private String serviceName;

        /**
         * 操作模式： READ,WRITE
         */
        private String opMode;
    }

}
