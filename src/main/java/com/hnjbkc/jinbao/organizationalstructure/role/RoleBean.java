package com.hnjbkc.jinbao.organizationalstructure.role;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.permission.PermissionBean;
import com.hnjbkc.jinbao.permission.permissiondata.PermissionDataBean;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xudaz
 * @date 2019/7/9
 * (虚拟)角色pojo类
 */
@Entity
@Table(name = "role")
@Setter
@Getter
//@HasOneToManyList(hasClasses = {
//        @OneToManyListInfo(propertyName = "userBeans" , propertyClass = UserBean.class )
//})
public class RoleBean implements Serializable {

    static final int  serialVersionUID = 2;
    /**
     * 自增主键Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roleId;

    /**
     * 角色名
     */
    private String roleName;

    /**
     * 角色描述
     */
    private String roleDescribe;

    /**
     * 角色类型 ( 权限组 | 角色 )
     */
    private String roleType;

    /**
     * 所对应的权限 多对多
     */
    @ManyToMany(fetch = FetchType.LAZY  )
    @JoinTable(name = "permission_role_middle" ,
            joinColumns = {@JoinColumn(name = "permission_role_middle_role_id" , referencedColumnName = "roleId")} ,
            inverseJoinColumns = {@JoinColumn(name = "permission_role_middle_permission_id" ,referencedColumnName = "permissionId")})
    private List<PermissionBean> permissionBeans;

    /**
     * 所有的用户 多对多
     */
    @ManyToMany(fetch = FetchType.LAZY )
    @JoinTable(name = "user_role_middle" ,
            joinColumns = {@JoinColumn(name = "user_role_middle_role_id" , referencedColumnName = "roleId")} ,
            inverseJoinColumns = {@JoinColumn(name = "user_role_middle_user_id" ,referencedColumnName = "userId")})
    @JsonIgnoreProperties({"roleBeans" , "jobBeans"})
    private List<UserBean> userBeans;

    /**
     * 数据权限
     */
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY , mappedBy = "roleBean" , cascade = CascadeType.ALL)
    private List<PermissionDataBean> permissionDataBeans;

    /**
     * 上级角色
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_parent_id", referencedColumnName = "roleId")
    @JsonIgnoreProperties({"nextRoleBeans" ,  "permissionBeans","professionBeanList"})
    private RoleBean parentRoleBean;

    /**
     * 下级角色
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentRoleBean")
    @JsonIgnoreProperties({"parentRoleBean", "permissionBeans","professionBeanList"})
    private List<RoleBean> nextRoleBeans;

    /**
     * 重写权限的get方法 使其能获取子集的权限
     * @return permissions
     */
    @SuppressWarnings("InfiniteRecursion")
    public List<PermissionBean> getPermissionBeans() {
        List<PermissionBean> permissionBeans = new ArrayList<>();
        if ( this.permissionBeans != null ) {
            permissionBeans = this.permissionBeans;
        }
        List<RoleBean> oldRoleBeanListTemp = nextRoleBeans;
        while ( oldRoleBeanListTemp != null && oldRoleBeanListTemp.size() > 0 ) {
            List<RoleBean> nextRoleBeanListTemp = new ArrayList<>();
            for (RoleBean roleBean : oldRoleBeanListTemp) {
                if ( roleBean.getNextRoleBeans() != null ) {
                    nextRoleBeanListTemp.addAll( roleBean.getNextRoleBeans() );
                }
                if ( roleBean.getPermissionBeans() != null ) {
                    permissionBeans.addAll(roleBean.getPermissionBeans() );
                }
            }
            oldRoleBeanListTemp = nextRoleBeanListTemp;
        }
        return permissionBeans;
    }
}
