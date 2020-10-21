package com.hnjbkc.jinbao.organizationalstructure.department;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.hqldao.annotations.HasOneToManyList;
import com.hnjbkc.jinbao.hqldao.annotations.OneToManyListInfo;
import com.hnjbkc.jinbao.organizationalstructure.company.CompanyBean;
import com.hnjbkc.jinbao.organizationalstructure.job.JobBean;
import com.hnjbkc.jinbao.permission.PermissionBean;
import com.hnjbkc.jinbao.workload.profession.ProfessionBean;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author siliqiang
 * @date 2019.7.9
 * 部门的pojo类
 */
@Entity
@Table(name = "department")
@Getter
@Setter
@HasOneToManyList(hasClasses = {
        @OneToManyListInfo(propertyName = "nextDepartmentBeans" , propertyClass = DepartmentBean.class) ,
        @OneToManyListInfo(propertyName = "jobBeans" , propertyClass = JobBean.class) ,
})
public class DepartmentBean implements Serializable {

    private static final long serialVersionUID = 5730266126369867156L;

    /**
     * 自增主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer departmentId;

    /**
     * 部门的名称
     */
    private String departmentName;

    /**
     * 公司的外键
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_company_id", referencedColumnName = "companyId")
    @JsonIgnoreProperties({"departmentBeans" ,  "permissionBeans"})
    private CompanyBean companyBean;

    /**
     * 上级部门
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_parent_id", referencedColumnName = "departmentId")
    @JsonIgnoreProperties({"nextDepartmentBeans" ,  "permissionBeans","professionBeanList"})
    private DepartmentBean parentDepartmentBean;

    /**
     * 下级部门
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentDepartmentBean")
    @JsonIgnoreProperties({"parentDepartmentBean", "permissionBeans","professionBeanList"})
    private List<DepartmentBean> nextDepartmentBeans;

    /**
     * 多个职位
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "departmentBean")
    @JsonIgnoreProperties({"userBeans" , "permissionBeans" , "departmentBean"})
    private List<JobBean> jobBeans;

    /**
     * 所对应的权限 多对多
     */
    @ManyToMany(fetch = FetchType.LAZY )
    @JoinTable(name = "permission_dept_middle" ,
            joinColumns = {@JoinColumn(name = "permission_dept_middle_department_id" , referencedColumnName = "departmentId")} ,
            inverseJoinColumns = {@JoinColumn(name = "permission_dept_middle_permission_id" ,referencedColumnName = "permissionId")})
    private List<PermissionBean> permissionBeans;


}
