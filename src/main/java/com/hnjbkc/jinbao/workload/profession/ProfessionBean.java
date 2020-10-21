package com.hnjbkc.jinbao.workload.profession;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnore;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfo;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfoType;
import com.hnjbkc.jinbao.hqldao.annotations.HasOneToManyList;
import com.hnjbkc.jinbao.hqldao.annotations.OneToManyListInfo;
import com.hnjbkc.jinbao.organizationalstructure.department.DepartmentBean;
import com.hnjbkc.jinbao.organizationalstructure.job.JobBean;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.workload.captain.CaptainBean;
import com.hnjbkc.jinbao.workload.profession_unit.ProfessionUnitBean;
import com.hnjbkc.jinbao.workload.staff.StaffBean;
import com.hnjbkc.jinbao.workload.supervisor.SupervisorBean;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 12
 * @Date 2019-08-27
 *
 * 工种
 */
@Getter
@Setter
@Entity
@Table(name = "profession")
@HasOneToManyList(hasClasses = {
        @OneToManyListInfo(propertyName = "staffBeanList" , propertyClass = StaffBean.class),
        @OneToManyListInfo(propertyName = "supervisorBeanList" , propertyClass = SupervisorBean.class),
        @OneToManyListInfo(propertyName = "captainBeanList" , propertyClass = CaptainBean.class),
})
@MyGraphIgnore( ignoreFields = {
        @MyGraphIgnoreInfo( fieldPath = "captainBeanList.postBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST , fieldList = {"projectBean"}),
        @MyGraphIgnoreInfo( fieldPath = "implementBean.projectBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST)
})
public class ProfessionBean implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer	professionId;

    /**
     * 部门
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profession_department_id", referencedColumnName = "departmentId")
    @JsonIgnoreProperties(value = {"jobBeans","permissionBeans", "professionBeanList" ,
            "permissionBeans" , "companyBean","nextDepartmentBeans","parentDepartmentBean"})
    private DepartmentBean departmentBean;

    /**
     * 工种名称
     */
    private String	professionName;

    /**
     * 计算单位
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profession_unit_id", referencedColumnName = "professionUnitId")
    private ProfessionUnitBean professionUnitBean;


    /**
     * 员工岗位
     */
    @OneToMany(mappedBy = "professionBean",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"professionBean","hibernateLazyInitializer", "handler"})
    private List<StaffBean> staffBeanList = new ArrayList<>();

    /**
     * 主管岗位
     */
    @OneToMany(mappedBy = "professionBean",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"professionBean","hibernateLazyInitializer", "handler"})
    private List<SupervisorBean> supervisorBeanList = new ArrayList<>();

    /**
     * 队长岗位
     */
    @OneToMany(mappedBy = "professionBean",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"professionBean","hibernateLazyInitializer", "handler"})
    private List<CaptainBean> captainBeanList = new ArrayList<>();

    /**
     * 是否存在
     */
    private Integer	professionExist;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date    professionCreateTime;



}
