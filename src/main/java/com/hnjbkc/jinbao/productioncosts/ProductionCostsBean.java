package com.hnjbkc.jinbao.productioncosts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnore;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfo;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfoType;
import com.hnjbkc.jinbao.implement.ImplementBean;
import com.hnjbkc.jinbao.organizationalstructure.job.JobBean;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.squadgroupfee.SquadGroupFeeBean;
import com.hnjbkc.jinbao.squadgroupfee.productioncostsdetail.ProductionCostsDetailBean;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author 12
 * @Date 2019-09-29
 * 生产费
 */
@Getter
@Setter
@Entity
@Table(name = "production_costs")
@MyGraphIgnore( ignoreFields = {
        @MyGraphIgnoreInfo( fieldPath = "implementBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST, fieldList = {"departmentBean"}),
        @MyGraphIgnoreInfo(fieldPath = "implementBean.departmentBean", fetchType = MyGraphIgnoreInfoType.WHITE_LIST),
        @MyGraphIgnoreInfo(fieldPath = "departmentBean", fetchType = MyGraphIgnoreInfoType.WHITE_LIST),
})
public class ProductionCostsBean implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer	productionCostsId;

    /**
     * 生产费金额
     */
    private Double productionCostsMoney;

    /**
     * 班组老板 是班组费的时候 才有
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_costs_project_boss_id",referencedColumnName = "squadGroupFeeId")
    private SquadGroupFeeBean squadGroupFeeBean;

    /**
     * 天数
     */
    private String productionCostsDay;

    /**
     * 对应项目实施
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_costs_implement_id",referencedColumnName = "implementId")
    private ImplementBean implementBean;

    /**
     * 生产费详情名称  根据类别 获取详情 是 班组费 或者 设备使用的
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "production_costs_detail_id",referencedColumnName = "productionCostsDetailId")
    private ProductionCostsDetailBean productionCostsDetailBean;



    /**
     * 创建人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_costs_create_user_id", referencedColumnName = "userId")
    @JsonIgnoreProperties(value = {"roleBeans" , "permissionBeans" ,"jobBean"})
    private UserBean createUserBean;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date productionCostsCreateTime;

    /**
     * 数据职位 职位Bean
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_costs_job_id", referencedColumnName = "jobId")
    @JsonIgnoreProperties(value = {"userBeans" , "permissionBeans" ,"departmentBean"})
    private JobBean createJobBean;

    /**
     * 备注
     */
    private String productionCostsRemark;
}
