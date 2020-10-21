package com.hnjbkc.jinbao.disburse.finance.bank_card_allot;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnore;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfo;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfoType;
import com.hnjbkc.jinbao.disburse.DisburseBean;
import com.hnjbkc.jinbao.disburse.finance.bankcard.BankCardBean;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * @author siliqiang
 * @date 2019/11/19
 * 银行卡分配表的pojo类
 */
@Data
@Entity
@Table(name = "bank_card_allot")
@MyGraphIgnore( ignoreFields = {
        @MyGraphIgnoreInfo( fieldPath = "disburseBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST
                ) ,
})
public class BankCardAllotBean {
    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bankCardAllotId;
    /**
     * 银行卡支付的金额
     */
    private Double bankCardAllotBankCardMoney;
    /**
     * 银行卡支付时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date bankCardAllotTime;

    /**
     * 银行卡外键
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_card_allot_bank_card_id", referencedColumnName = "bankCardId")
    private BankCardBean bankCardBean;

    /**
     * 支出外键
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_card_allot_disburse_id", referencedColumnName = "disburseId")
    @JsonIgnoreProperties(value = {"bankCardAllotBean"})
    private DisburseBean disburseBean;
}
