package com.hnjbkc.jinbao.workload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnore;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfo;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfoType;
import com.hnjbkc.jinbao.implement.ImplementBean;
import com.hnjbkc.jinbao.organizationalstructure.job.JobBean;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.permission.dataascriptionannotation.BelongJob;
import com.hnjbkc.jinbao.permission.dataascriptionannotation.BelongPerson;
import com.hnjbkc.jinbao.workload.profession.ProfessionBean;
import com.hnjbkc.jinbao.workload.workloadstatus.WorkLoadStatusBean;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author 12
 * @Date 2019-08-27
 */
@Getter
@Setter
@Entity
@Table(name = "work_load")
@MyGraphIgnore( ignoreFields = {
        @MyGraphIgnoreInfo( fieldPath = "implementBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST , fieldList = {"projectBean","departmentBean"}),
        @MyGraphIgnoreInfo( fieldPath = "implementBean.projectBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST)
})
public class WorkLoadBean implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer	workLoadId;

    /**
     * 部门
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_load_implement_id", referencedColumnName = "implementId")
    @JsonIgnoreProperties(value = {"workLoadBeans"  ,"projectStatusRecordBeanList","implementRecordBeanList" ,"productionCostsBeans"
            ,"disburseBeans"
            ,"createJobBean","createUserBean","createUserBean"})
    private ImplementBean implementBean;

    /**
     * 人员成本
     */
    private Double workLoadLaborCost;
    /**
     * 工种
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_load_work_profession_id", referencedColumnName = "professionId")
    @JsonIgnoreProperties(value = {"departmentBean" , "professionUnitBean" ,"staffBeanList","supervisorBeanList","captainBeanList",
            "createUserBean","createJobBean"})
    private ProfessionBean professionBean;

    /**
     * 展示的工种名称
     */
    private String workLoadProfessionName;

    /**
     * 员工
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_load_staff_id", referencedColumnName = "userId")
    @JsonIgnoreProperties(value = {"roleBeans" , "permissionBeans" ,"jobBean","staffPostBean","supervisorPostBean","captainPostBean"})
    @BelongPerson
    private UserBean staffUserBean;

    /**
     * 主管
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_load_supervisor_id", referencedColumnName = "userId")
    @JsonIgnoreProperties(value = {"roleBeans" , "permissionBeans" ,"jobBean","captainPostBean","staffBean"})
    private UserBean supervisorUserBean;

    /**
     * 队长
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_load_captain_id", referencedColumnName = "userId")
    @JsonIgnoreProperties(value = {"roleBeans" , "permissionBeans" ,"jobBean","supervisorBean","staffBean"})
    private UserBean captainUserBean;

    /**
     * 员工单价
     */
    private Double workLoadPriceStaff;

    /**
     * 主管单价
     */
    private Double workLoadPriceManage;

    /**
     * 队长单价
     */
    private Double workLoadPriceCaptain;

    /**
     * 员工提成金额
     */
    private Double workLoadAmountStaff;

    /**
     * 主管提成金额
     */
    private Double workLoadAmountManage;

    /**
     * 队长提成金额
     */
    private Double workLoadAmountCaptain;

    /**
     * 工作量
     */
    private Double	workLoadWorkLoad;


    /**
     * 员工对应天数
     */
    private Double workLoadDuration;


    /**
     * 日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date workLoadDate;

    /**
     * 假删除
     */
    private Integer	workLoadExist;

    /**
     * 工作备注
     */
    private String workLoadRemark;


    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date workLoadCreateDate;

    /**
     * 修改时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date workLoadUpdateDate;

    /**
     * 审核状态
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_load_status_id",referencedColumnName = "workLoadStatusId")
    private WorkLoadStatusBean workLoadStatusBean;

    /**
     * 初审审核备注
     */
    private String workLoadAuditRemark;

    /**
     * 初审审核人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_load_audit_user_id" , referencedColumnName = "userId")
    private UserBean workLoadAuditUserBean;

    /**
     * 终审审核备注
     */
    private String workLoadEndAuditRemark;

    /**
     * 终审审核人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_load_end_audit_user_id" , referencedColumnName = "userId")

    private UserBean workLoadEndAuditUserBean;

    /**
     * 支付备注
     */
    private String workLoadPayRemark;

    /**
     * 人员成本 用于铺数据 数据库没有
     */
    @Transient
    private Double deductionWage;

    /**
     * 创建人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_load_create_user_id", referencedColumnName = "userId")
    @JsonIgnoreProperties(value = {"roleBeans" , "permissionBeans" ,"jobBean"})
    private UserBean createUserBean;


    /**
     * 数据职位 职位Bean
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_load_job_id", referencedColumnName = "jobId")
    @JsonIgnoreProperties(value = {"userBeans" , "permissionBeans" ,"departmentBean"})
    @BelongJob
    private JobBean createJobBean;
}
