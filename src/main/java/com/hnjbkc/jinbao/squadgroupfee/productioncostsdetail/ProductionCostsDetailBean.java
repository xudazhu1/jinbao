package com.hnjbkc.jinbao.squadgroupfee.productioncostsdetail;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.squadgroupfee.squadgroupfeestatus.SquadGroupFeeStatusBean;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 设备使用的bean
 *
 * @author siliqiang
 * @date 2019/12/30
 */
@Data
@Entity
@Table(name = "production_costs_detail")
public class ProductionCostsDetailBean implements Serializable {
    /**
     * 自增id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productionCostsDetailId;

    /**
     * 编号
     */
    private String productionCostsDetailNum;
    /**
     * 名称
     */
    private String productionCostsDetailName;
    /**
     * 类型
     */
    private String productionCostsDetailType;
    /**
     * 录入时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date productionCostsDetailTime;
    /**
     * 当天使用费用
     */
    private Double productionCostsEquipmentPrice;
    /**
     * 备注
     */
    private String productionCostsDetailRemark;

    /**
     * 用户外键
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_costs_detail_user_id", referencedColumnName = "userId")
    private UserBean userBean;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_costs_detail_status_id", referencedColumnName = "squadGroupFeeStatusId")
    private SquadGroupFeeStatusBean squadGroupFeeStatusBean;

}
