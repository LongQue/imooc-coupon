package com.imooc.coupon.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * <h1>url路径信息实体类</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "coupon_path")
public class Path {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id",nullable = false)
    private Integer id;

    /**
     * 路径模式
     */
    @Column(name = "path_pattern",nullable = false)
    private String pathPattern;

    /**
     * Http 方法类型
     */
    @Column(name="http_method",nullable = false)
    private String httpMethod;

    /**
     * 路径名称
     */
    @Column(name = "path_name",nullable = false)
    private String pathName;

    /**
     * 服务名称
     */
    @Column(name = "service_name",nullable = false)
    private String serviceName;

    /**
     * 操作模式
     */
    @Column(name = "op_mode",nullable = false)
    private String opMode;

    public Path(String pathPattern, String httpMethod, String pathName, String serviceName, String opMode) {
        this.pathPattern = pathPattern;
        this.httpMethod = httpMethod;
        this.pathName = pathName;
        this.serviceName = serviceName;
        this.opMode = opMode;
    }
}
