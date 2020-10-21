package com.hnjbkc.jinbao.permission;

import com.hnjbkc.jinbao.organizationalstructure.company.CompanyBean;
import com.hnjbkc.jinbao.organizationalstructure.department.DepartmentBean;
import com.hnjbkc.jinbao.organizationalstructure.job.JobBean;
import com.hnjbkc.jinbao.organizationalstructure.role.RoleBean;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @author xudaz
 * @date 2019/7/9
 * 权限pojo类
 */
@Entity
@Table(name = "permission")
@Getter
@Setter
public class PermissionBean implements Serializable {
    /**
     * 自增主键Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer permissionId;

    /**
     * 权限类型(增删改查|页面)
     */
    private String permissionType;

    /**
     * 是否是数据权限
     */
    private String permissionDataPermission;

    /**
     * 权限标识符(URL)
     */
    private String permissionTag;

    /**
     * 权限分类
     */
    private String permissionClass;

    /**
     * (页面)权限名称
     */
    private String permissionName;

    /**
     * 权限备注(说明)
     */
    private String permissionRemark;


    /**
     * 与职位 多对多
     */
    @ManyToMany(fetch = FetchType.LAZY , mappedBy = "permissionBeans")
    private List<JobBean> jobBeans;
    /**
     * 与部门 多对多
     */
    @ManyToMany(fetch = FetchType.LAZY , mappedBy = "permissionBeans")
    private List<DepartmentBean> departmentBeans;
    /**
     * 与角色 多对多
     */
    @ManyToMany(fetch = FetchType.LAZY , mappedBy = "permissionBeans")
    private List<RoleBean> roleBeans;
    /**
     * 与公司 多对多
     */
    @ManyToMany(fetch = FetchType.LAZY , mappedBy = "permissionBeans")
    private List<CompanyBean> companyBeans;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PermissionBean that = (PermissionBean) o;
        return Objects.equals(permissionId, that.permissionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permissionId);
    }

    @Override
    public String toString() {
        return "PermissionBean{" +
                "permissionId=" + permissionId +
                ", permissionType='" + permissionType + '\'' +
                ", permissionTag='" + permissionTag + '\'' +
                ", permissionClass='" + permissionClass + '\'' +
                ", permissionName='" + permissionName + '\'' +
                ", permissionRemark='" + permissionRemark + '\'' +
                '}';
    }
}
