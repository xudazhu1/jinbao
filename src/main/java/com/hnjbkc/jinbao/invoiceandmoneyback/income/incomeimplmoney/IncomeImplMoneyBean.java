package com.hnjbkc.jinbao.invoiceandmoneyback.income.incomeimplmoney;

import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnore;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfo;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfoType;
import com.hnjbkc.jinbao.implement.ImplementBean;
import com.hnjbkc.jinbao.invoiceandmoneyback.income.IncomeBean;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 回款金额分配
 * @author xudaz
 * @date 2019/10/4
 */
@Data
@Entity
@Table(name = "income_impl_money")
@MyGraphIgnore(ignoreFields = {
        @MyGraphIgnoreInfo( fieldPath = "implementBean"  , fetchType = MyGraphIgnoreInfoType.WHITE_LIST ,
        fieldList = "departmentBean"  ) ,
        @MyGraphIgnoreInfo( fieldPath = "implementBean.departmentBean"  , fetchType = MyGraphIgnoreInfoType.WHITE_LIST ) ,
        @MyGraphIgnoreInfo( fieldPath = "incomeBean"  , fetchType = MyGraphIgnoreInfoType.WHITE_LIST ) ,

})
public class IncomeImplMoneyBean implements Serializable {

    /**
     * id
     */
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Integer incomeImplMoneyId;


    /**
     * 所对应的实施部
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "income_impl_money_impl_id")
    private ImplementBean implementBean;

    /**
     * 具体金额
     */
    private Double incomeImplMoney;

    /**
     * 所对应的收入表
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "income_impl_money_income_id")
    private IncomeBean incomeBean;



}
