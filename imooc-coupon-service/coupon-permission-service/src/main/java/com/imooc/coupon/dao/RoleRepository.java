package com.imooc.coupon.dao;

import com.imooc.coupon.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <h1>Role Dao</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/30
 */
public interface RoleRepository extends JpaRepository<Role,Integer> {
}
