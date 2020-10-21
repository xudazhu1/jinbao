package com.hnjbkc.jinbao.disburse.finance.personalbankcard;

import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author siliqiang
 * @date 2019/9/29
 */
@Table(name = "personal_bank_card")
@Entity
@Data
public class PersonalBankCardBean implements Serializable {
    /**
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer personalBankCardId;

    /**
     * 卡名称
     */
    private String personalBankCardName;
    /**
     * 卡号
     */
    private String personalBankCardNum;
    /**
     * 初始值
     */
    private Double personalBankCardInitialValue;
    /**
     * 用户的外键
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_bank_card_user_id",referencedColumnName = "userId")
    private UserBean userBean;
}
