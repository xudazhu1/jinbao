package com.hnjbkc.jinbao.management;

import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnore;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfo;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfoType;
import com.hnjbkc.jinbao.management.moneybackestimation.ManagementMoneyBackEstimationBean;
import com.hnjbkc.jinbao.organizationalstructure.job.JobBean;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.project.ProjectBean;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author xudaz
 * @date 2019/8/8
 * 经营管理表 pojo类
 */
@Entity
@Table(name = "management")
@Getter
@Setter
@MyGraphIgnore( ignoreFields = {
        @MyGraphIgnoreInfo( fieldPath = "managementPartnersBean.refereesUserBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST) ,
        @MyGraphIgnoreInfo( fieldPath = "managementPartnersBean.userBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST) ,
        @MyGraphIgnoreInfo( fieldPath = "projectBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST ,  fieldList = {"earningsCompanyBean"}) ,
        @MyGraphIgnoreInfo( fieldPath = "projectBean.earningsCompanyBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST ) ,
        @MyGraphIgnoreInfo( fieldPath = "createUserBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST) ,
        @MyGraphIgnoreInfo( fieldPath = "refereeUserBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST) ,
        @MyGraphIgnoreInfo( fieldPath = "createJobBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST , fieldList = {"departmentBean"}) ,
        @MyGraphIgnoreInfo( fieldPath = "createJobBean.departmentBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST) ,
})
public class ManagementBean implements Serializable {
    /**
     * 自增主键Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer managementId;

    /**
     * 所对应的项目
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "management_project_id" , referencedColumnName = "projectId")
    private ProjectBean projectBean ;

    /**
     * 主负责人 (经营负责人)
     */
    private String managementMainHead;

    /**
     * 业务人员
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "management_partners_id" , referencedColumnName = "managementPartnersId")
    private ManagementPartnersBean managementPartnersBean;

    /**
     * 身份标识(外部合伙人 | 内部合伙人 | 合作伙伴)
     */
    private String managementPartnersIdentity;

    /**
     * 业务介绍人
     */
    private String managementSponsor;

    /**
     * 推荐人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "management_referee_user_id" , referencedColumnName = "userId")
    private UserBean refereeUserBean;

    /**
     * 协调费/协作费 (有 | 无)
     */
    private String managementCoordinateFee;
    /**
     * 提成模式( 牛逼(特殊) , 牛逼(普通) , 小挣 , 劳务 | 合作 , 同行 )
     */
    private String managementCommissionMode;
    /**
     * 税率
     */
    private Double managementRate = 0.00;
//    /**
//     * Cooperation fee 合作费(弃用此字段)
//     */
//    private Double managementCooperationFee = 0.00;
    /**
     * Corporate profits 公司利润
     */
    private Double managementCorporateProfits = 0.00;
    /**
     * Contract fee 承包费
     */
    private Double managementContractFee = 0.00 ;
    /**
     * management fee 管理费率
     */
    private Double managementFee = 0.00 ;
    /**
     * 财评金额
     */
    private Double managementGoodsEvaluationAmount = 0.00;
    /**
     * 结算金额(弃) --> 预算金额(新)
     */
    private Double managementSettlementAmount = 0.00;
    /**
     * 审计金额
     */
    private Double managementAuditAmount = 0.00;
    /**
     * 备注
     */
    private String managementRemark;
    /**
     * 创建人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "management_create_user_id" , referencedColumnName = "userId")
    private UserBean createUserBean;
    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date managementCreateTime;
    /**
     * 数据所属职位
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "management_job_id" , referencedColumnName = "jobId")
    private JobBean createJobBean;

    /**
     * 预估回款
     */
    @OneToMany(mappedBy = "managementBean" , fetch = FetchType.LAZY)
    private List<ManagementMoneyBackEstimationBean> managementMoneyBackEstimationBeans;

}
