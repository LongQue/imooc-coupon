package com.imooc.coupon.service;

import com.imooc.coupon.constant.RoleEnum;
import com.imooc.coupon.dao.PathRepository;
import com.imooc.coupon.dao.RolePathMappingRepository;
import com.imooc.coupon.dao.RoleRepository;
import com.imooc.coupon.dao.UserRoleMappingRepository;
import com.imooc.coupon.entity.Path;
import com.imooc.coupon.entity.Role;
import com.imooc.coupon.entity.RolePathMapping;
import com.imooc.coupon.entity.UserRoleMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * <h1>权限校验功能服务接口实现</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/30
 */
@Slf4j
@Service
public class PermissionService {

    private final PathRepository pathRepository;
    private final RoleRepository roleRepository;
    private final UserRoleMappingRepository userRoleMappingRepository;
    private final RolePathMappingRepository rolePathMappingRepository;

    @Autowired
    public PermissionService(PathRepository pathRepository, RoleRepository roleRepository, UserRoleMappingRepository userRoleMappingRepository, RolePathMappingRepository rolePathMappingRepository) {
        this.pathRepository = pathRepository;
        this.roleRepository = roleRepository;
        this.userRoleMappingRepository = userRoleMappingRepository;
        this.rolePathMappingRepository = rolePathMappingRepository;
    }

    /**
     * <h2>用户访问接口权限校验</h2>
     * @param userId 用户id
     * @param uri    访问url
     * @param httpMethod 请求类型
     * @return true/false
     */
    public Boolean checkPermission(Long userId, String uri, String httpMethod) {
        UserRoleMapping userRoleMapping = userRoleMappingRepository.findByUserId(userId);
        //如果用户角色映射表找不到记录，直接返回false
        if (null == userRoleMapping) {
            log.error("userId not exist is UserRoleMapping: {}", userId);
            return false;
        }
        //如果找不到对应的Role记录，直接返回false
        Optional<Role> role = roleRepository.findById(userRoleMapping.getRoleId());
        if (!role.isPresent()) {
            log.error("roleId not exist is UserRoleMapping: {}",userRoleMapping.getRoleId());
            return false;
        }

        if (role.get().getRoleName().equals(RoleEnum.SUPER_ADMIN.name())) {
            return true;
        }

        //如果路径没有注册(忽略了),直接返回true，代表不需要校验权限
        Path path = pathRepository.findByPathPatternAndHttpMethod(uri, httpMethod);
        if (null == path) {
            return true;
        }

        RolePathMapping rolePathMapping = rolePathMappingRepository.findByRoleIdAndPathId(role.get().getId(), path.getId());
        return rolePathMapping != null;
    }
}
