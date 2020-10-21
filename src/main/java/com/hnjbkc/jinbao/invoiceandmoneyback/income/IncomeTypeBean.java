package com.hnjbkc.jinbao.invoiceandmoneyback.income;

import lombok.Data;

import javax.persistence.*;

/**
 * 收入类型
 * @author xudaz
 */
@Data
@Entity
@Table(name = "income_type")
public class IncomeTypeBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer incomeTypeId;

    /**
     * 收入类型
     */
    private String incomeType;

    /**
     * 收入类型明细
     */
    private String incomeDetail;

}
