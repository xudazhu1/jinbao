package com.hnjbkc.jinbao.management.moneybackestimation;

import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnore;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfo;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfoType;
import com.hnjbkc.jinbao.management.ManagementBean;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 预计回款 条件 pojo类
 * @author xudaz
 * @date 2019/8/26
 */
@Data
@Entity
@Table(name = "management_money_back_estimation")
@MyGraphIgnore( ignoreFields = {
        @MyGraphIgnoreInfo( fieldPath = "managementBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST  ) ,
})
public class ManagementMoneyBackEstimationBean implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer managementMoneyBackEstimationId;

    /**
     * 预估时间(不为空则时间回款)
     */
    private Date managementMoneyBackEstimationTime;

    /**
     * 预估金额
     */
    private Double managementMoneyBackEstimationMoney;

    /**
     * 完成状态(向后合并 | 待回款 | 已完成 )
     */
    private String managementMoneyBackEstimationStatus;


    /**
     * 所属条件(不为空则条件回款)
     */
    @OneToMany(fetch = FetchType.LAZY)
    private List<ManagementMoneyBackEstimationConditionsBean> managementMoneyBackEstimationConditionsBeans;

    /**
     * 所属经营管理
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "management_money_back_estimation_management_id" , referencedColumnName = "managementId")
    private ManagementBean managementBean;

}
