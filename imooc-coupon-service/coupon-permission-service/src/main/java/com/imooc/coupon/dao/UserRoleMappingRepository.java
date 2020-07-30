package com.imooc.coupon.dao;

import com.imooc.coupon.entity.UserRoleMapping;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <h1>UserRoleMapping Dao</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/30
 */
public interface UserRoleMappingRepository extends JpaRepository<UserRoleMapping, Integer> {

    /**
     * <h2>通过userId查找对应的角色</h2>
     */
    UserRoleMapping findByUserId(Long userId);
}
