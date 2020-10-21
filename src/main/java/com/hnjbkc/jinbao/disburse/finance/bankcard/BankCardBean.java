package com.hnjbkc.jinbao.disburse.finance.bankcard;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 银行卡表
 *
 * @author xudaz
 * @date 2019/9/10
 */
@Data
@Table(name = "bank_card")
@Entity
public class BankCardBean implements Serializable {

    /**
     * 数据Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bankCardId;

    /**
     * 展示卡名称
     */
    private String bankCardName;

    /**
     * 卡账号
     */
    private String bankCardNum;

    /**
     * 卡余额
     */
    private Double bankCardMoney;

    /**
     * 卡备注
     */
    private String bankCardRemark;

    /**
     * 类型
     */
    private String bankCardType;
    /**
     * 初始值
     */
    private Double bankCardInitialValue;

    /**
     * 开户行
     */
    private String bankCardBankOfDeposit;

    @Override
    public String toString() {
        return "BankCardBean{" +
                "bankCardId=" + bankCardId +
                ", bankCardName='" + bankCardName + '\'' +
                ", bankCardNum='" + bankCardNum + '\'' +
                ", bankCardMoney=" + bankCardMoney +
                ", bankCardRemark=" + bankCardRemark +
                ", bankCardType='" + bankCardType + '\'' +
                ", bankCardInitialValue=" + bankCardInitialValue +
                '}';
    }
}
