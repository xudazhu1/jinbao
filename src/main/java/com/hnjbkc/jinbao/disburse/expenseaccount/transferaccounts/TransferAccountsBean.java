package com.hnjbkc.jinbao.disburse.expenseaccount.transferaccounts;

import com.hnjbkc.jinbao.disburse.finance.bankcard.BankCardBean;
import com.hnjbkc.jinbao.disburse.paymentstatus.PaymentStatusBean;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 转账的pojo类
 *
 * @author siliqiang
 * @date 2019/9/29
 */
@SuppressWarnings("WeakerAccess")
@Entity
@Table(name = "transfer_accounts")
@Data
 public class TransferAccountsBean  implements Serializable {
    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transferAccountsId;

   /**
    * 转账编号
    */
   private String transferAccountsNum;
    /**
     * 日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date transferAccountsDate;
    /**
     * 日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date transferAccountsPayDate;
    /**
     * 金额
     */
    private Double transferAccountsMoney;
    /**
     * 备注
     */
    private String transferAccountsRemark;
    /**
     * 经办人
     */
    private String transferAccountsResponsiblePerson;
    /**
     * 转出卡外键
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_come_bank_card_id", referencedColumnName = "bankCardId")
    private BankCardBean comeBankCardBean;

    /**
     * 转入卡外键
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_enter_bank_card_id", referencedColumnName = "bankCardId")
    private BankCardBean enterBankCardBean;

    /**
     * 外键关联审批状态
     * 一对一
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfer_accounts_payment_status_id", referencedColumnName = "paymentStatusId")
    private PaymentStatusBean paymentStatusBean;

    /**
     * 审核备注
     */
    private String transferAccountsAffirmRemark;

   /**
    * 转账类型
    */
   private String transferAccountsType;

}
