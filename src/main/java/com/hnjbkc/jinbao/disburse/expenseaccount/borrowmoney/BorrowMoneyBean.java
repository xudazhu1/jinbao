package com.hnjbkc.jinbao.disburse.expenseaccount.borrowmoney;

import com.hnjbkc.jinbao.disburse.finance.bankcard.BankCardBean;
import com.hnjbkc.jinbao.disburse.finance.personalbankcard.PersonalBankCardBean;
import com.hnjbkc.jinbao.disburse.paymentstatus.PaymentStatusBean;
import com.hnjbkc.jinbao.organizationalstructure.job.JobBean;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.permission.dataascriptionannotation.BelongPerson;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 借款的pojo类
 *
 * @author siliqiang
 * @date 2019-9-29
 */
@Entity
@Table(name = "borrow_money")
@Data
public class BorrowMoneyBean implements Serializable {

    /**
     * 借款的id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer borrowMoneyId;

    /**
     * 借款编号
     */
    private String borrowMoneyNum;
    /**
     * 借款时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date borrowMoneyDate;
    /**
     * 支付时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date borrowMoneyPayDate;
    /**
     * 借款金额
     */
    private Double borrowMoneyMoney;
    /**
     * 借款备注
     */
    private String borrowMoneyRemark;

    /**
     * 银行卡的外键
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrow_money_bank_card_id", referencedColumnName = "bankCardId")
    private BankCardBean bankCardBean;

    /**
     * 个人银行卡外键
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrow_money_personal_bank_card_id", referencedColumnName = "personalBankCardId")
    private PersonalBankCardBean personalBankCardBean;

    /**
     * 外键关联审批状态
     * 一对一
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrow_money_payment_status_id", referencedColumnName = "paymentStatusId")
    private PaymentStatusBean paymentStatusBean;

    /**
     * 确认备注
     */
    private String borrowMoneyAuditRemark;
    /**
     * 审核备注
     */
    private String borrowMoneyAffirmRemark;
    /**
     * 支付备注
     */
    private String borrowMoneyPayRemark;

    /**
     * 数据权限创建人
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "borrow_money_user_id", referencedColumnName = "userId")
    @BelongPerson
    private UserBean userBean;

    /**
     * 角色权限(创建角色)
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "borrow_money_job_id", referencedColumnName = "jobId")
    private JobBean jobBean;

    @Override
    public String toString() {
        return "BorrowMoneyBean{" +
                "borrowMoneyId=" + borrowMoneyId +
                ", borrowMoneyDate=" + borrowMoneyDate +
                ", borrowMoneyMoney=" + borrowMoneyMoney +
                ", borrowMoneyRemark='" + borrowMoneyRemark + '\'' +
                ", bankCardBean=" + bankCardBean +
                ", personalBankCardBean=" + personalBankCardBean +
                '}';
    }
}
