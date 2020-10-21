package com.hnjbkc.jinbao.disburse.disbursedetail;

import com.hnjbkc.jinbao.disburse.disbursetype.DisburseTypeBean;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 支出明细
 * @author xudaz
 * @date 2019/8/27
 */
@Data
@Entity
@Table(name = "disburse_detail")
public class DisburseDetailBean  implements Serializable {

    /**
     * 支出明细 Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Integer disburseDetailId;

    /**
     * 支出明细 名称
     */
    private  String disburseDetailName;

    /**
     * 明细备注
     */
    private String disburseDetailRemarks;
    /**
     * 来源(属于哪个模块用的)
     */
    private String disburseDetailSource;

    /**
     * 所属类型
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disburse_detail_type_id" , referencedColumnName = "disburseTypeId")
    private DisburseTypeBean disburseTypeBean;

}
