package com.hnjbkc.jinbao.contract;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.contract.departmentmoney.ContractDepartmentMoneyBean;
import com.hnjbkc.jinbao.contract.material.MaterialBean;
import com.hnjbkc.jinbao.hqldao.annotations.HasOneToManyList;
import com.hnjbkc.jinbao.hqldao.annotations.OneToManyListInfo;
import com.hnjbkc.jinbao.organizationalstructure.job.JobBean;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.project.ProjectBean;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 合同的pojo类
 *
 * @author siliqiang
 * @date 2019.8.9
 */
@Getter
@Setter
@Table(name = "contract")
@Entity
@HasOneToManyList(hasClasses = {
        @OneToManyListInfo(propertyName = "materialBeans", propertyClass = MaterialBean.class) ,
        @OneToManyListInfo(propertyName = "contractDepartmentMoneyBeans", propertyClass = ContractDepartmentMoneyBean.class) ,
})


public class ContractBean implements Serializable {
    /**
     * 合同的id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer contractId;

    /**
     * 签订时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date contractSigningDate;
    /**
     *
     * 合同状态
     */
    private String contractState;
    /**
     * 预估金额
     */
    private Double contractEstimateMoney;
    /**
     * 合同总金额
     */
    private Double contractMoney;
    /**
     * 原件数
     */
    private Integer contractRawFileCount;
    /**
     * 副件数
     */
    private Integer contractAccessoryCount;
    /**
     * 付款条件
     */
    private String contractPaymentClause;
    /**
     * 资料要求
     */
    private String contractDataRequirements;
    /**
     * 是否有扫描件
     */
    private String contractScannedExists;
    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date contractCreateTime;

    /**
     * 多扫描件
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "contractBean", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"contractBean"})
    private List<MaterialBean> materialBeans = new ArrayList<>();

    /**
     * 部门分配金额
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "contractBean", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"contractBean"})
    private List<ContractDepartmentMoneyBean> contractDepartmentMoneyBeans  = new ArrayList<>();

    /**
     * 外键关联项目主键
     * 一对一
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_project_id", referencedColumnName = "projectId")
    @JsonIgnoreProperties(value = {"quoteBean", "contractBean","managementBean" , "createUserBean", "createJobBean"})
    private ProjectBean projectBean;

    /**
     * 创建人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_user_id", referencedColumnName = "userId")
    private UserBean createUserBean;

    /**
     * 数据归属职位
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_job_id", referencedColumnName = "jobId")
    @JsonIgnoreProperties(value = {"permissionBeans", "departmentBean"})
    private JobBean createJobBean;

}
