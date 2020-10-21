package com.hnjbkc.jinbao.invoiceandmoneyback.income;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnore;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfo;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfoType;
import com.hnjbkc.jinbao.hqldao.annotations.HasOneToManyList;
import com.hnjbkc.jinbao.hqldao.annotations.OneToManyListInfo;
import com.hnjbkc.jinbao.implement.secondparty.SecondPartyBean;
import com.hnjbkc.jinbao.invoiceandmoneyback.income.incomeimplmoney.IncomeImplMoneyBean;
import com.hnjbkc.jinbao.invoiceandmoneyback.invoice.InvoiceBean;
import com.hnjbkc.jinbao.invoiceandmoneyback.moneyback.MoneyBackBean;
import com.hnjbkc.jinbao.project.ProjectBean;
import com.hnjbkc.jinbao.project.earningscompany.EarningsCompanyBean;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 收入pojo
 * @author xudaz
 * @date 2019/9/4
 */
@SuppressWarnings("unused")
@Entity
@Data
@Table(name = "income")
@MyGraphIgnore(ignoreFields = {
        @MyGraphIgnoreInfo( fieldPath = "projectBean"  , fetchType = MyGraphIgnoreInfoType.WHITE_LIST) ,
        @MyGraphIgnoreInfo( fieldPath = "invoiceBean.incomeBean" , fetchType = MyGraphIgnoreInfoType.ONLY_SELF  ) ,
        @MyGraphIgnoreInfo( fieldPath = "invoiceBean.createUserBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST  ) ,
        @MyGraphIgnoreInfo( fieldPath = "earningsCompanyBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST  )

})
@HasOneToManyList( hasClasses = {
        @OneToManyListInfo( propertyName = "moneyBackBeans" , propertyClass = MoneyBackBean.class ),
        @OneToManyListInfo( propertyName = "incomeImplMoneyBeans" , propertyClass = IncomeImplMoneyBean.class )
})
public class IncomeBean implements Serializable {

    /**
     * 收入 id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer incomeId;

    /**
     * 所对应的项目
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "income_project_id" , referencedColumnName = "projectId")
    @JsonIgnoreProperties(value = {"quoteBean" , "createUserBean" , "createJobBean" })
    private ProjectBean projectBean;

    /**
     * 所对应的开票
     */
    @OneToOne(fetch = FetchType.LAZY , cascade = CascadeType.ALL )
    @JoinColumn(name = "income_invoice_id" , referencedColumnName = "invoiceId")
    @JsonIgnoreProperties( {"incomeBean"} )
    private InvoiceBean invoiceBean;

    /**
     * 收入的实际回款
     */
    @OneToMany(fetch = FetchType.LAZY , mappedBy = "incomeBean" , cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"incomeBean"})
    private List<MoneyBackBean> moneyBackBeans;

    /**
     * 收入编号
     */
    private String incomeNum;

    /**
     * 收入备注
     */
    private String incomeRemark;

    /**
     * 收入类型
     */
    private String incomeType;

    /**
     * 收入类型 (项目? 其他?)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "income_income_type_id" , referencedColumnName = "incomeTypeId")
    private IncomeTypeBean incomeTypeBean;

    /**
     * 收益单位
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "income_earnings_company_id" , referencedColumnName = "earningsCompanyId")
    private EarningsCompanyBean earningsCompanyBean;

    /**
     * 回款方式
     */
    private String incomeWay;

    /**
     * 回款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date incomeDate;

    /**
     * 回款金额
     */
    private Double incomeMoney;

    /**
     * 收款单位(回款金额)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "income_party_second" , referencedColumnName = "secondPartyId")
    private SecondPartyBean secondPartyBean;

    /**
     * 总实际回款
     */
    private Double incomeCountMoneyBackMoney;

    /**
     * 审核状态
     */
    private String incomeAuditStatus;

    /**
     * 审核备注
     */
    private String incomeAuditRemark;

    /**
     *回款金额分配
     */
    @JsonIgnoreProperties({"incomeBean"})
    @OneToMany(mappedBy = "incomeBean" ,fetch = FetchType.LAZY ,cascade = CascadeType.ALL)
    private List<IncomeImplMoneyBean> incomeImplMoneyBeans;


}
