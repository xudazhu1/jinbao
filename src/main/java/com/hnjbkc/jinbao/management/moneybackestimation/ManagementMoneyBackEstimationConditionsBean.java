package com.hnjbkc.jinbao.management.moneybackestimation;

import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnore;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfo;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfoType;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author xudaz
 * @date 2019/8/26
 */
@Data
@Entity
@Table(name = "management_money_back_estimation_conditions")
@MyGraphIgnore( ignoreFields = {
        @MyGraphIgnoreInfo( fieldPath = "managementMoneyBackEstimationBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST  ) ,
})
class ManagementMoneyBackEstimationConditionsBean implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer managementMoneyBackEstimationConditionsId;

    /**
     * 条件名称
     */
    private String managementMoneyBackEstimationConditionsName;

    /**
     * 条件备注
     */
    private String managementMoneyBackEstimationConditionsRemark;

    /**
     * 所属预估
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "management_money_back_estimation_conditions_estimation_id" , referencedColumnName = "ManagementMoneyBackEstimationId")
    private ManagementMoneyBackEstimationBean managementMoneyBackEstimationBean;
}
