package com.hnjbkc.jinbao.organizationalstructure.job;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.organizationalstructure.department.DepartmentBean;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.permission.PermissionBean;
import com.hnjbkc.jinbao.permission.permissiondata.PermissionDataBean;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author siliqiang
 * @date 2019.7.9
 * 职位表的pojo类
 */

@Entity
@Table(name = "job")
@Getter
@Setter
@NamedEntityGraph(name = "jobBean.graph", attributeNodes = {
        @NamedAttributeNode(value = "departmentBean",subgraph = "departmentBean.graph")
},subgraphs = {
        @NamedSubgraph(name = "departmentBean.graph", attributeNodes = {
                @NamedAttributeNode("companyBean"),
                @NamedAttributeNode("parentDepartmentBean")
        }),
}
)
public class JobBean implements Serializable {
    /**
     * 主键自增id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer jobId;

    /**
     * 职位的名称
     */
    private String jobName;

    /**
     * 部门的外键
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_department_id", referencedColumnName = "departmentId")
    @JsonIgnoreProperties({"jobBeans" , "nextDepartmentBeans" , "permissionBeans" , "professionBeanList"})
    private DepartmentBean departmentBean;

    /**
     * 所对应的权限 多对多
     */
    @ManyToMany(fetch = FetchType.LAZY  )
    @JoinTable(name = "permission_job_middle" ,
            joinColumns = {@JoinColumn(name = "permission_job_middle_job_id" , referencedColumnName = "jobId")} ,
            inverseJoinColumns = {@JoinColumn(name = "permission_job_middle_permission_id" ,referencedColumnName = "permissionId")})
    private List<PermissionBean> permissionBeans;


    /**
     * 所有的用户 多对一
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "jobBean")
    @JsonIgnoreProperties({"jobBean" , "permissionBeans" })
    private List<UserBean> userBeans;

    /**
     * 数据权限
     */
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY , mappedBy = "jobBean" , cascade = CascadeType.ALL)
    private List<PermissionDataBean> permissionDataBeans;


}
