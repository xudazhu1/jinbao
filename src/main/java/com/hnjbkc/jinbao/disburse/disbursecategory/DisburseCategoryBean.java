package com.hnjbkc.jinbao.disburse.disbursecategory;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 支出类别 pojo类
 * @author xudaz
 * @date 2019/8/27
 */
@Entity
@Table(name = "disburse_category")
@Data
public class DisburseCategoryBean implements Serializable {

    /**
     * 支出类别Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer disburseCategoryId;

    /**
     * 支出类别名称
     */
    private String disburseCategoryName;
}
