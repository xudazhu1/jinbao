package com.hnjbkc.jinbao.organizationalstructure.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RoleDao extends JpaRepository<RoleBean,Integer> {

    /**
     * 为角色去除与用户的多对多关系
     * @param roleId id
     */
    @Modifying
    @Query(value = "delete from user_role_middle where user_role_middle_role_id=?1" , nativeQuery = true)
    void  deleteUserConnectionByRoleId(Integer roleId);

    /**
     * 为角色去除与权限的多对多关系
     * @param roleId id
     */
    @Modifying
    @Query(value = "delete from permission_role_middle where permission_role_middle_role_id=?1" , nativeQuery = true)
    void  deletePermissionConnectionByRoleId(Integer roleId);

}
