package com.hnjbkc.jinbao.contract.departmentmoney;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnore;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfo;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfoType;
import com.hnjbkc.jinbao.contract.ContractBean;
import com.hnjbkc.jinbao.implement.ImplementBean;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author siliqiang
 * @date 2019.8.15
 */
@Entity
@Getter
@Setter
@Table(name = "contract_department_money")
@MyGraphIgnore( ignoreFields = {
        @MyGraphIgnoreInfo( fieldPath = "implementBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST),
        @MyGraphIgnoreInfo( fieldPath = "implementBean.departmentBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST)
})

public class ContractDepartmentMoneyBean implements Serializable {
    /**
     * 合同部门金额id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer contractDepartmentMoneyId;
    /**
     * 合同部门分配金额
     */
    public Double contractDepartmentMoneyDistributionMoney;

    /**
     * 实施部的主键
     * 一对一
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_department_money_implement_department_id", referencedColumnName = "implementId")
    @JsonIgnoreProperties({"projectBean"})
    private ImplementBean implementBean;


    /**
     *合同的外键
     */
    @ManyToOne(fetch = FetchType.LAZY ,cascade = CascadeType.ALL)
    @JoinColumn(name = "contract_department_money_contract_id", referencedColumnName = "contractId" )
    private ContractBean contractBean;
}
