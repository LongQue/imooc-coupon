package com.imooc.coupon.dao;

import com.imooc.coupon.entity.Path;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * <h1>Path Dao</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/30
 */
public interface PathRepository extends JpaRepository<Path,Integer> {

    /**
     * <h2>根据服务名称查找 Path  记录</h2>
     */
    List<Path> findAllByServiceName(String serviceName);

    /**
     * <h2>根据路径模式 + 请求类型 查找数据记录</h2>
     * @param pattern
     * @param httpMethod
     * @return
     */
    Path findByPathPatternAndHttpMethod(String pattern, String httpMethod);
}
