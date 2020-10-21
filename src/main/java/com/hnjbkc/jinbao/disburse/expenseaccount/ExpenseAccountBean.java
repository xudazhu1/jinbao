package com.hnjbkc.jinbao.disburse.expenseaccount;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnore;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfo;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfoType;
import com.hnjbkc.jinbao.disburse.DisburseBean;
import com.hnjbkc.jinbao.disburse.approvalstatus.ApprovalStatusBean;
import com.hnjbkc.jinbao.hqldao.annotations.HasOneToManyList;
import com.hnjbkc.jinbao.hqldao.annotations.OneToManyListInfo;
import com.hnjbkc.jinbao.organizationalstructure.job.JobBean;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.permission.dataascriptionannotation.BelongJob;
import com.hnjbkc.jinbao.permission.dataascriptionannotation.BelongPerson;
import com.hnjbkc.jinbao.utils.Cancellation;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 报销总表的pojo类
 *
 * @author siliqiang
 * @date 2019.8.28
 */
@HasOneToManyList(hasClasses = {
        @OneToManyListInfo( propertyClass = DisburseBean.class , propertyName = "disburseBeans" )
})
@MyGraphIgnore(ignoreFields = {
        @MyGraphIgnoreInfo(fieldPath = "expenseAccountAuditUserBean", fetchType = MyGraphIgnoreInfoType.WHITE_LIST),
        @MyGraphIgnoreInfo(fieldPath = "expenseAccountSupervisorAuditUserBean", fetchType = MyGraphIgnoreInfoType.WHITE_LIST),
        @MyGraphIgnoreInfo(fieldPath = "expenseAccountGeneralManagerOfficeAuditUserBean", fetchType = MyGraphIgnoreInfoType.WHITE_LIST),
})
@Entity
@Table(name = "expense_account")
@Data
public class ExpenseAccountBean implements Serializable {
    /**
     * 报销总表的id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer expenseAccountId;

    /**
     * 所属报销类型
     */
    private String expenseAccountType;

    /**
     * 报销总表的编号
     */
    private String expenseAccountNum;

    /**
     * 报销月份
     */
    @DateTimeFormat(pattern = "yyyy-MM")
    private Date expenseAccountMonth;

    /**
     * 报销具体时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expenseAccountTime;

    /**
     * 报销人
     */
    private String expenseAccountUserName;

    /**
     * 审批备注(财务审核备注)
     */
    private String expenseAccountRemark;

    /**
     *  财务审核时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expenseAccountAuditTime;

    /**
     *  财务审核人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_account_audit_user" , referencedColumnName = "userId")
    private UserBean expenseAccountAuditUserBean;

    /**
     *  主管审核人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_account_supervisor_audit_user" , referencedColumnName = "userId")
    private UserBean  expenseAccountSupervisorAuditUserBean;

    /**
     *  总经办审核人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_account_general_manager_office_audit_user" , referencedColumnName = "userId")
    private UserBean  expenseAccountGeneralManagerOfficeAuditUserBean;
    /**
     *  终审操作人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_account_final_judgment_user" , referencedColumnName = "userId")
    private UserBean  expenseAccountFinalJudgmentUserBean;


    /**
     *  主管审核时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expenseAccountSupervisorAuditTime;
    /**
     *  总经办审核时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expenseAccountGeneralManagerOfficeAuditTime;
    /**
     *  终审时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expenseAccountFinalJudgmentTime;

    /**
     * 主管审核备注
     */
    private String expenseAccountSupervisorAuditRemark;

    /**
     * 总经办审核备注
     */
    private String expenseAccountGeneralManagerOfficeAuditRemark;
    /**
     * 终审备注
     */
    private String expenseAccountFinalJudgmentRemark;

    /**
     * 多笔报销单
     */
    @JsonIgnoreProperties({"expenseAccountBean"})
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "expenseAccountBean", cascade = CascadeType.ALL)
    private List<DisburseBean> disburseBeans = new ArrayList<>();

    /**
     * 外键关联审批状态
     * duo对一
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_account_approval_status_id", referencedColumnName = "approvalStatusId")
    @Cancellation
    private ApprovalStatusBean approvalStatusBean;

    /**
     * 数据权限创建人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_user_id",referencedColumnName = "userId")
    @BelongPerson
    private UserBean userBean;

    /**
     * 角色权限(创建角色)
     */
    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name = "expense_job_id",referencedColumnName = "jobId")
    @BelongJob
    private JobBean jobBean;

}
