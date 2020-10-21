package com.hnjbkc.jinbao.disburse.disbursetype;

import com.hnjbkc.jinbao.disburse.disbursecategory.DisburseCategoryBean;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 支出类型 pojo
 * @author xudaz
 * @date 2019/8/27
 *
 */
@Entity
@Table(name = "disburse_type")
@Data
public class DisburseTypeBean implements Serializable {

    /**
     *  支出类型Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Integer disburseTypeId;

    /**
     * 支出类型名称
     */
    private  String disburseTypeName;


    /**
     * 所属类别
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disburse_type_category_id" , referencedColumnName = "disburseCategoryId")
    private DisburseCategoryBean disburseCategoryBean;


}
