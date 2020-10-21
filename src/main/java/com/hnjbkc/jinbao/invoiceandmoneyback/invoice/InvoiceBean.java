package com.hnjbkc.jinbao.invoiceandmoneyback.invoice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnore;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfo;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfoType;
import com.hnjbkc.jinbao.implement.secondparty.SecondPartyBean;
import com.hnjbkc.jinbao.invoiceandmoneyback.income.IncomeBean;
import com.hnjbkc.jinbao.organizationalstructure.job.JobBean;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.permission.dataascriptionannotation.BelongJob;
import com.hnjbkc.jinbao.permission.dataascriptionannotation.BelongPerson;
import com.hnjbkc.jinbao.permission.dataascriptionannotation.CreateTime;
import com.hnjbkc.jinbao.project.earningscompany.EarningsCompanyBean;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 项目开票pojo类
 * @author xudaz
 * @date 2019/9/4
 */
@Data
@Table(name = "invoice")
@Entity
@SuppressWarnings("unused")
@MyGraphIgnore( ignoreFields = {
        @MyGraphIgnoreInfo( fieldPath = "incomeBean.projectBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST  ) ,
        @MyGraphIgnoreInfo( fieldPath = "createUserBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST  ) ,
})
public class InvoiceBean implements Serializable {

    /**
     * 开票 id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer invoiceId;

    /**
     * 申请时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreateTime
    private Date invoiceApplyDate;
    /**
     * 开票时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date invoiceDate;

    /**
     * 所对应的收入
     */
    @OneToOne(mappedBy = "invoiceBean" ,fetch = FetchType.LAZY )
    @JsonIgnoreProperties(value = {"invoiceBean"})
    private IncomeBean incomeBean;

    /**
     * 开票编号
     */
    private String invoiceNumber;

    /**
     * 收款单位(开票)
     */
    @ManyToOne(fetch = FetchType.LAZY )
    @JoinColumn(name = "invoice_party_second" , referencedColumnName = "secondPartyId")
    private SecondPartyBean secondPartyBean;
    /**
     * 收益单位
     */
    @ManyToOne(fetch = FetchType.LAZY )
    @JoinColumn(name = "invoice_earnings_company_id" , referencedColumnName = "earningsCompanyId")
    private EarningsCompanyBean earningsCompanyBean;

    /**
     * 开票类型
     */
    private String invoiceType;

    /**
     * 甲方单位
     */
    private String invoicePartyFirst;

    /**
     * 纳税识别号
     */
    private String invoiceTaxes;

    /**
     * 地址
     */
    private String invoiceSite;

    /**
     * 电话
     */
    private String invoicePhone;

    /**
     * 开户行
     */
    private String invoiceBank;

    /**
     * 账号
     */
    private String invoiceAccount;

    /**
     * 税率
     */
    private Double invoiceRate;

    /**
     * 税额
     */
    private Double invoiceRateMoney;

    /**
     * 开票内容
     */
    private String invoiceContent;

    /**
     * 开票金额
     */
    private Double invoiceMoney;

    /**
     * 备注
     */
    private String invoiceRemark;

    /**
     * 开票时合同
     */
    private String invoiceOnCont;

    /**
     * 项目地点
     */
    private String invoiceItemSite;

    /**
     * 通知开票人
     */
    private String invoiceNoticeMans;

    /**
     * 开票审核状态
     */
    private Integer invoiceAuditStatus;

    /**
     * 开票审核备注
     */
    private String invoiceAuditRemark;

    /**
     * 创建人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_create_user_id" , referencedColumnName = "userId")
    @BelongPerson
    private UserBean createUserBean;

    /**
     * 数据所属职位
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_job_id" , referencedColumnName = "jobId")
    @BelongJob
    private JobBean createJobBean;

}
