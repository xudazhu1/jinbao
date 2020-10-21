package com.hnjbkc.jinbao.disburse.property;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.disburse.DisburseBean;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 资产表的pojo类
 *
 * @author siliqiang
 * @date 2019.9.18
 */
@Data
@Entity
@Table(name = "property")
public class PropertyBean implements Serializable {
    /**
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer propertyId;
    /**
     * 资产名称
     */
    private String propertyName;
    /**
     * 物品型号
     */
    private String propertyType;
    /**
     * 购买日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date propertyBuyTime;
    /**
     * 折旧方式
     */
    private String propertyDepreciationMethod;
    /**
     * 原值
     */
    private Double propertyOriginalValue;
    /**
     * 期限
     */
    private Integer propertyDeadline;
    /**
     * 残值率
     */
    private Double propertyResidual;
    /**
     * 累计折旧
     */
    private Double propertyAccumulatedDepreciation;
    /**
     * 本月折旧
     */
    private Double propertyInstantDepreciation;
    /**
     * 剩余折旧
     */
    private Double propertyResidueDepreciation;
    /**
     * 净值
     */
    private Double propertyNetValue;
    /**
     * 录入时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date propertyInputTime;

    /**
     * 支出表的外键
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_disburse_id",referencedColumnName = "disburseId")
    @JsonIgnoreProperties("propertyBean1")
    private DisburseBean disburseBean;

    @OneToMany(mappedBy = "propertyBean" ,fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"propertyBean"})
    private List<DisburseBean> disburseBeans = new ArrayList<>();


}
