package com.hnjbkc.jinbao.workload.post;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.hqldao.annotations.HasOneToManyList;
import com.hnjbkc.jinbao.hqldao.annotations.OneToManyListInfo;
import com.hnjbkc.jinbao.organizationalstructure.department.DepartmentBean;
import com.hnjbkc.jinbao.workload.userwork.UserWorkBean;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 岗位
 * @author 12
 * @Date 2019-09-30
 */
@Getter
@Setter
@Entity
@Table(name = "post")
@HasOneToManyList(hasClasses = {
        @OneToManyListInfo( propertyClass = UserWorkBean.class , propertyName = "staffUserList" ),
        @OneToManyListInfo( propertyClass = UserWorkBean.class , propertyName = "supervisorUserList" ),
        @OneToManyListInfo( propertyClass = UserWorkBean.class , propertyName = "captainUserList" ),
})
public class PostBean implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer	postId;

    /**
     *  部门
     */
    @ManyToOne
    @JoinColumn(name = "post_department_id",referencedColumnName = "departmentId")
    @JsonIgnoreProperties(value = {"parentDepartmentBean","nextDepartmentBeans",",jobBeans", "permissionBeans","professionBeanList"})
    private DepartmentBean departmentBean;

    /**
     *  岗位类别(员工,主管,队长)
     */
    private String	postLevel;

    /**
     *  岗位名称
     */
    private String	postName;

    /**
     * 员工岗位
     */
    @OneToMany(fetch = FetchType.LAZY ,mappedBy = "staffPostBean")
    @JsonIgnoreProperties(value = {"roleBeans","jobBean","staffPostBean","supervisorPostBean","captainPostBean"})
    private List<UserWorkBean> staffUserList;

    /**
     * 员工岗位
     */
    @OneToMany(fetch = FetchType.LAZY ,mappedBy = "supervisorPostBean")
    @JsonIgnoreProperties(value = {"roleBeans","jobBean","staffPostBean","supervisorPostBean","captainPostBean"})
    private List<UserWorkBean> supervisorUserList;

    /**
     * 员工岗位
     */
    @OneToMany(fetch = FetchType.LAZY ,mappedBy = "captainPostBean")
    @JsonIgnoreProperties(value = {"roleBeans","jobBean","staffPostBean","supervisorPostBean","captainPostBean"})
    private List<UserWorkBean> captainUserList;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date postCreateTime;
}
