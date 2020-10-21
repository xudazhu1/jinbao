package com.hnjbkc.jinbao.organizationalstructure.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author xudaz
 * @date 2019/7/11
 */
@Repository
public interface UserDao extends JpaRepository<UserBean, Integer> {
    /**
     * 根据用户名查找用户信息
     *
     * @param s 用户名
     * @return 用户的对象
     */
    UserBean findUserBeanByUserName(String s);

    /**
     * 为用户去除与角色的多对多关系
     *
     * @param userId id
     */
    @Modifying
    @Query(value = "delete from user_role_middle where user_role_middle_user_id=?1", nativeQuery = true)
    void deleteRoleConnectionByUserId(Integer userId);

//    @EntityGraph
//    List<UserBean> findUserBeanByStaffPostBeanIsNotNullOrSupervisorPostBeanIsNotNullOrCaptainPostBeanIsNotNullOrderByUserIdDesc();

    /**
     * 修改密码的方法
     *
     * @param newPassword 新密码
     * @param name        用户名
     * @param oldPassword 旧密码
     * @return
     */
    @Transactional()
    @Query(value = "UPDATE user SET user_password=?1 WHERE user_name=?2 AND user_password=?3", nativeQuery = true)
    @Modifying(clearAutomatically = true)
    int updatePassword(String newPassword, String name, String oldPassword);

    /**
     * 获取所有的user id name
     * @return id name数组
     */
    @Query(value = "select u.user_id,u.user_name from `user` u",nativeQuery = true)
    List<List> getUserAll();
}
