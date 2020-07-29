package com.imooc.coupon.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * <h1>User 与 Role 的映射关系实体类</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "coupon_user_role_mapping")
public class UserRoleMapping {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * User 的主键
     */
    @Basic
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Role 表的主键
     */
    @Basic
    @Column(name = "role_id", nullable = false)
    private Integer roleId;
}
