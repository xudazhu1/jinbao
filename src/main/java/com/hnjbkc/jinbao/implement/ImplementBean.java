package com.hnjbkc.jinbao.implement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnore;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfo;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfoType;
import com.hnjbkc.jinbao.disburse.DisburseBean;
import com.hnjbkc.jinbao.hqldao.annotations.HasOneToManyList;
import com.hnjbkc.jinbao.hqldao.annotations.OneToManyListInfo;
import com.hnjbkc.jinbao.implement.implementrecord.ImplementRecordBean;
import com.hnjbkc.jinbao.implement.projectstatus.ProjectStatusBean;
import com.hnjbkc.jinbao.implement.projectstatusrecord.ProjectStatusRecordBean;
import com.hnjbkc.jinbao.implement.projecttype.ProjectTypeBean;
import com.hnjbkc.jinbao.implement.secondparty.SecondPartyBean;
import com.hnjbkc.jinbao.organizationalstructure.department.DepartmentBean;
import com.hnjbkc.jinbao.organizationalstructure.job.JobBean;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.productioncosts.ProductionCostsBean;
import com.hnjbkc.jinbao.productioncosts.productioncostsfile.ProductionCostsFileBean;
import com.hnjbkc.jinbao.project.ProjectBean;
import com.hnjbkc.jinbao.workload.WorkLoadBean;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 12
 * @Date 2019-08-08
 */
@Getter
@Setter
@Entity
@Table(name = "implement")
@HasOneToManyList(hasClasses = {
        @OneToManyListInfo( propertyClass = ProductionCostsBean.class , propertyName = "productionCostsBeans" ),
        @OneToManyListInfo( propertyClass = WorkLoadBean.class , propertyName = "workLoadBeans" ),
        @OneToManyListInfo( propertyClass = ProjectStatusRecordBean.class , propertyName = "projectStatusRecordBeanList" ),
        @OneToManyListInfo( propertyClass = ImplementRecordBean.class , propertyName = "implementRecordBeanList" ),
        @OneToManyListInfo( propertyClass = ProductionCostsBean.class , propertyName = "productionCostsBeans" ),
        @OneToManyListInfo( propertyClass = DisburseBean.class , propertyName = "disburseBeans" ),
        @OneToManyListInfo( propertyClass = ProductionCostsFileBean.class , propertyName = "productionCostsFileBeans" )
})
@MyGraphIgnore( ignoreFields = {
        @MyGraphIgnoreInfo( fieldPath = "projectBean.managementBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST ) ,
        @MyGraphIgnoreInfo( fieldPath = "projectBean.quoteBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST ) ,
        @MyGraphIgnoreInfo( fieldPath = "projectBean.contractBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST ) ,
        @MyGraphIgnoreInfo( fieldPath = "departmentBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST ) ,
        @MyGraphIgnoreInfo( fieldPath = "projectBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST ) ,
        @MyGraphIgnoreInfo( fieldPath = "projectStatusBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST ) ,
})
public class ImplementBean implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer implementId;



    /**
     * 项目  实施跟项目是 (n:1)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "implement_project_id", referencedColumnName = "projectId")
    @JsonIgnoreProperties({"createJobBean" , "createUserBean", "createUserBean","implementBeans","quoteBean","projectLocationBean"})
    private ProjectBean projectBean;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "implement_department_id",referencedColumnName = "departmentId")
    @JsonIgnoreProperties({"professionBeanList"})
    private DepartmentBean departmentBean;
    /**
     * 实施部负责人
     */
    private String implementImplementHead;

    /**
     * 项目状态
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "implement_project_status_id", referencedColumnName = "projectStatusId")
    private ProjectStatusBean projectStatusBean;

    /**
     * 项目状态历史记录
     */
    @OneToMany(mappedBy = "implementBean", fetch=FetchType.LAZY ,cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"implementBean"})
    private List<ProjectStatusRecordBean> projectStatusRecordBeanList;

    /**
     * 实施记录字段
     */
    private String implementRecordContent;

    /**
     * 实施进度
     */
    private Double implementProgress;

    /**
     * 实施历史记录
     */
    @OneToMany(mappedBy = "implementBean", fetch=FetchType.LAZY ,cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"implementBean"})
    private List<ImplementRecordBean> implementRecordBeanList;

    /**
     * 项目类型
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "implement_project_type_id", referencedColumnName = "projectTypeId")
    private ProjectTypeBean projectTypeBean;

    /**
     * 乙方
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "implement_second_party_id", referencedColumnName = "secondPartyId")
    private SecondPartyBean secondPartyBean;

    /**
     * 创建人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "implement_create_user_id", referencedColumnName = "userId")
    @JsonIgnoreProperties(value = {"roleBeans" , "staffBean" , "supervisorBean" , "captainPostBean" , "permissionBeans" ,"jobBean"})
    private UserBean createUserBean;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date implementCreateTime;

    /**
     * 数据职位 职位Bean
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "implement_job_id", referencedColumnName = "jobId")
    @JsonIgnoreProperties(value = {"userBeans" , "permissionBeans" ,"departmentBean"})
    private JobBean createJobBean;

    /**
     * 多个工作量
     */
    @OneToMany(mappedBy = "implementBean",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JsonIgnore
    @Fetch(FetchMode.SUBSELECT)
    private List<WorkLoadBean> workLoadBeans;


    /**
     * 生产费
     */
    @OneToMany(mappedBy = "implementBean", fetch=FetchType.LAZY ,cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"implementBean","createUserBean","createJobBean","bossUserBean"})
    private List<ProductionCostsBean> productionCostsBeans;

    /**
     * 生产费的扫描件
     */
    @OneToMany(mappedBy = "implementBean",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"implementBean"})
    private  List<ProductionCostsFileBean> productionCostsFileBeans;

    /**
     * 项目跟实施一对多
     */
    @OneToMany(mappedBy = "implementBean", fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"departmentBean","implementBean",",companyBean",",companyBean","propertyBean","propertyBean1","bankCardBean"})
    private List<DisburseBean> disburseBeans = new ArrayList<>();
}
