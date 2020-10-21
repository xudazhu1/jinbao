package com.hnjbkc.jinbao.disburse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnore;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfo;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfoType;
import com.hnjbkc.jinbao.disburse.approvalstatus.ApprovalStatusBean;
import com.hnjbkc.jinbao.disburse.disbursedetail.DisburseDetailBean;
import com.hnjbkc.jinbao.disburse.expenseaccount.ExpenseAccountBean;
import com.hnjbkc.jinbao.disburse.finance.bank_card_allot.BankCardAllotBean;
import com.hnjbkc.jinbao.disburse.finance.bankcard.BankCardBean;
import com.hnjbkc.jinbao.disburse.paymentstatus.PaymentStatusBean;
import com.hnjbkc.jinbao.disburse.property.PropertyBean;
import com.hnjbkc.jinbao.hqldao.annotations.HasOneToManyList;
import com.hnjbkc.jinbao.hqldao.annotations.OneToManyListInfo;
import com.hnjbkc.jinbao.implement.ImplementBean;
import com.hnjbkc.jinbao.implement.secondparty.SecondPartyBean;
import com.hnjbkc.jinbao.organizationalstructure.company.CompanyBean;
import com.hnjbkc.jinbao.organizationalstructure.department.DepartmentBean;
import com.hnjbkc.jinbao.project.earningscompany.EarningsCompanyBean;
import com.hnjbkc.jinbao.squadgroupfee.SquadGroupFeeBean;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author siliqiang
 * @date 2019/8/27
 */
@Data
@Table(name = "disburse")
@Entity
@HasOneToManyList(hasClasses = {
        @OneToManyListInfo(propertyClass = BankCardAllotBean.class, propertyName = "bankCardAllotBeans")
})
@MyGraphIgnore(ignoreFields = {
        @MyGraphIgnoreInfo(fieldPath = "implementBean",
                fetchType = MyGraphIgnoreInfoType.WHITE_LIST, fieldList = {"projectBean", "departmentBean", "projectStatusBean"}),
        @MyGraphIgnoreInfo(fieldPath = "implementBean.projectBean", fetchType = MyGraphIgnoreInfoType.WHITE_LIST,
                fieldList = {"managementBean"}),
        @MyGraphIgnoreInfo(fieldPath = "implementBean.projectBean.managementBean", fetchType = MyGraphIgnoreInfoType.WHITE_LIST,
                fieldList = {"managementPartnersBean"}),
        @MyGraphIgnoreInfo(fieldPath = "implementBean.projectBean.managementBean.managementPartnersBean", fetchType = MyGraphIgnoreInfoType.WHITE_LIST,
                fieldList = {"userBean"}),
        @MyGraphIgnoreInfo(fieldPath = "implementBean.projectBean.managementBean.managementPartnersBean.userBean", fetchType = MyGraphIgnoreInfoType.WHITE_LIST),
        @MyGraphIgnoreInfo(fieldPath = "implementBean.departmentBean", fetchType = MyGraphIgnoreInfoType.WHITE_LIST),
        @MyGraphIgnoreInfo(fieldPath = "departmentBean", fetchType = MyGraphIgnoreInfoType.WHITE_LIST),
        @MyGraphIgnoreInfo(fieldPath = "implementBean.projectStatusBean", fetchType = MyGraphIgnoreInfoType.WHITE_LIST),
})

public class DisburseBean implements Serializable {

    /**
     * 支出Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer disburseId;

    /**
     * 报销内容(报销)
     */
    private String disburseExpenseAccountContent;

    /**
     * 支出编号
     */
    private String disburseNum;

    /**
     * 字段暂时没有使用
     */
    private Double disburseExpenseAccountMoney;

    /**
     * 支出发票金额 ( 报销)
     */
    private Double disburseInvoiceMoney;

    /**
     * 费用归属部门 ( 报销)
     */
    private String disburseAttributionDepartment;

    /**
     * 支出时间 ( 付款申请单 )( 报销) (财务日常)
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date disburseTime;

    /**
     * 支出归属 ( 付款申请单 )( 报销)(资产)(财务日常)
     */
    private String disburseAffiliation;

    /**
     * 对应支出明细 ( 付款申请单) (报销) (财务日常)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disburse_disburse_detail_id", referencedColumnName = "disburseDetailId")
    private DisburseDetailBean disburseDetailBean;

    /**
     * 对应报销总表(报销)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disburse_expense_account_id", referencedColumnName = "expenseAccountId")
    private ExpenseAccountBean expenseAccountBean;

    /**
     * 对应的收益单位 ( 付款申请单) (报销)(财务日常)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disburse_earnings_company_id", referencedColumnName = "earningsCompanyId")
    private EarningsCompanyBean earningsCompanyBean;

    /**
     * 对应的部门(组织架构的) ( 付款申请单) (报销)(财务日常)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disburse_department_id", referencedColumnName = "departmentId")
    @JsonIgnoreProperties({"professionBeanList"})
    private DepartmentBean departmentBean;

    /**
     * 对应实施表 ( 付款申请单)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disburse_implement_id", referencedColumnName = "implementId")
    @JsonIgnoreProperties({"createJobBean", "createUserBean", "projectTypeBean", "secondPartyBean"})
    private ImplementBean implementBean;

    /**
     * 对应的公司(报销)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disburse_company_id", referencedColumnName = "companyId")
    private CompanyBean companyBean;

    /**
     * 外键付款单位 ( 付款申请单)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disburse_second_party_id", referencedColumnName = "secondPartyId")
    private SecondPartyBean secondPartyBean;

    /**
     * 外键关联审批状态(报销)
     * 一对一
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disburse_approval_status_id", referencedColumnName = "approvalStatusId")
    private ApprovalStatusBean approvalStatusBean;

    /**
     * 外键关联审批状态 ( 付款申请单)(财务日常)
     * 一对一
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disburse_payment_status_id", referencedColumnName = "paymentStatusId")
    private PaymentStatusBean paymentStatusBean;
    /**
     * 支出备注 ( 付款申请单)(报销)(财务日常)
     */
    private String disburseRemarks;

    /**
     * 审批备注( 待确认备注) ( 付款申请单)
     */
    private String disburseApprovalRemark;
    /**
     * 业务提成(付款申请单)
     */
    private Double disbursePaymentBusinessMoney;
    /**
     * 税费(付款申请单)
     */
    private Double disbursePaymentTax;

    /**
     * 审批备注( 待审核备注) ( 付款申请单)
     */
    private String disburseApprovalRemark2;

    /**
     * 审批备注( 待支付备注) ( 付款申请单)
     */
    private String disburseApprovalRemark3;

    /**
     * 付款方式 ( 付款申请单)
     */
    private String disburseMode;

    /**
     * 经办人 ( 付款申请单)
     */
    private String disburseResponsiblePerson;

    /**
     * 班组人/分红人 ( 付款申请单)
     */
    private String disburseLeaderProfitPerson;

    @ManyToOne
    @JoinColumn(name = "disburse_squad_group_fee_id", referencedColumnName = "squadGroupFeeId")
    private SquadGroupFeeBean squadGroupFeeBean;

    /**
     * 收款单位 ( 付款申请单)
     */
    private String disbursePayee;
    /**
     * 收款开户行 ( 付款申请单)
     */
    private String disburseCollectionBank;
    /**
     * 收款银行账号 ( 付款申请单)
     */
    private String financeShroffAccountNumber;

    /**
     * 支出金额(付款申请单)(财务日常)(报销单)
     */
    private Double disbursePaymentAmount;

    /**
     * 是否已开票 ( 付款申请单)
     */
    private String disburseHaveTicket;
    /**
     * 资产表的外键
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disburse_property_id", referencedColumnName = "propertyId")
    private PropertyBean propertyBean;
    /**
     * 资产表的外键
     */
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "disburseBean")
    @JsonIgnoreProperties({"disburseBean"})
    private PropertyBean propertyBean1;

    /**
     * 银行卡表外键
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "disburse_bank_card_id", referencedColumnName = "bankCardId")
    private BankCardBean bankCardBean;

    /**
     * 银行卡分配金额外键
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "disburseBean", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"disburseBean"})
    private List<BankCardAllotBean> bankCardAllotBeans = new ArrayList<>();

}
