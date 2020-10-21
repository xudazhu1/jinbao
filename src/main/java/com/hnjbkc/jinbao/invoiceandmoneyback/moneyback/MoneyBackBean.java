package com.hnjbkc.jinbao.invoiceandmoneyback.moneyback;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnore;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfo;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfoType;
import com.hnjbkc.jinbao.disburse.finance.bankcard.BankCardBean;
import com.hnjbkc.jinbao.implement.secondparty.SecondPartyBean;
import com.hnjbkc.jinbao.invoiceandmoneyback.income.IncomeBean;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 回款pojo
 * @author xudaz
 * @date 2019/9/4
 */
@Data
@Entity
@Table(name = "money_back")
@SuppressWarnings("unused")
@MyGraphIgnore( ignoreFields = {
        @MyGraphIgnoreInfo( fieldPath="incomeBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST )
})
public class MoneyBackBean implements Serializable {

    /**
     * 回款Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer moneyBackId;

    /**
     * 所对应的收入
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "money_back_income_id" , referencedColumnName = "incomeId")
    @JsonIgnoreProperties( {"moneyBackBeans"} )
    private IncomeBean incomeBean;

    /**
     * 实际回款编号
     */
    private String moneyBackNum;

    /**
     * 实际回款方式
     */
    private String moneyBackType;

    /**
     * 实际回款时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date moneyBackDate;

    /**
     * 实际回款金额
     */
    private Double moneyBackMoney;

    /**
     * 实际回款备注
     */
    private String moneyBackRemark;

    /**
     * 收款单位(实际回款)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "money_back_party_second" , referencedColumnName = "secondPartyId")
    private SecondPartyBean secondPartyBean;

    /**
     * 收款银行卡
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "money_back_bank_card_id" , referencedColumnName = "bankCardId")
    private BankCardBean bankCardBean;

    /**
     * 审核状态
     */
    private String moneyBackAuditStatus;

    /**
     * 审核备注
     */
    private String moneyBackAuditRemark;



}
