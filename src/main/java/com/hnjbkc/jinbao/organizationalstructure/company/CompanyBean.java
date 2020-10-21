package com.hnjbkc.jinbao.organizationalstructure.company;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.hqldao.annotations.HasOneToManyList;
import com.hnjbkc.jinbao.hqldao.annotations.OneToManyListInfo;
import com.hnjbkc.jinbao.organizationalstructure.department.DepartmentBean;
import com.hnjbkc.jinbao.permission.PermissionBean;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author siliqiang
 *
 * @date 2019.7.9
 * 公司的pojo类
 */

@Entity
@Table(name = "company")
@Setter
@Getter
@HasOneToManyList(hasClasses = {
        @OneToManyListInfo(propertyName = "departmentBeans" , propertyClass = DepartmentBean.class)
})
public class CompanyBean implements Serializable {
    /**
     * 自增主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer companyId;

    /**
     * 公司的名称
     */
    private String companyName;

    /**
     * 多个部门
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "companyBean")
    @JsonIgnoreProperties({"parentDepartmentBean" , "permissionBeans" , "professionBeanList" , "companyBean"})
    private List<DepartmentBean> departmentBeans = new ArrayList<>();

    /**
     * 所对应的权限 多对多
     */
    @ManyToMany(fetch = FetchType.LAZY )
    @JoinTable(name = "permission_comp_middle" ,
            joinColumns = {@JoinColumn(name = "permission_comp_middle_company_id" , referencedColumnName = "companyId")} ,
            inverseJoinColumns = {@JoinColumn(name = "permission_comp_middle_permission_id" ,referencedColumnName = "permissionId")})
    private List<PermissionBean> permissionBeans = new ArrayList<>();

}
