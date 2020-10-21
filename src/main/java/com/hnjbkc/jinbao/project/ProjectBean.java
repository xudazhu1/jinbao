package com.hnjbkc.jinbao.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnore;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfo;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfoType;
import com.hnjbkc.jinbao.contract.ContractBean;
import com.hnjbkc.jinbao.hqldao.annotations.HasOneToManyList;
import com.hnjbkc.jinbao.hqldao.annotations.OneToManyListInfo;
import com.hnjbkc.jinbao.implement.ImplementBean;
import com.hnjbkc.jinbao.management.ManagementBean;
import com.hnjbkc.jinbao.organizationalstructure.job.JobBean;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.project.earningscompany.EarningsCompanyBean;
import com.hnjbkc.jinbao.project.projectlocation.ProjectLocationBean;
import com.hnjbkc.jinbao.quote.QuoteBean;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 12
 * @date 2019-08-06
 */
@Getter
@Setter
@Entity
@Table(name = "project")
@HasOneToManyList(hasClasses = {
        @OneToManyListInfo( propertyClass = ImplementBean.class , propertyName = "implementBeans" )
})
@MyGraphIgnore( ignoreFields = {
        @MyGraphIgnoreInfo( fieldPath = "managementBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST ,
        fieldList =  {"managementPartnersBean"}) ,
        @MyGraphIgnoreInfo( fieldPath = "contractBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST ) ,
        @MyGraphIgnoreInfo( fieldPath = "managementBean.managementPartnersBean.refereesUserBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST) ,
        @MyGraphIgnoreInfo( fieldPath = "managementBean.managementPartnersBean.userBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST) ,
        @MyGraphIgnoreInfo( fieldPath = "createUserBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST) ,
        @MyGraphIgnoreInfo( fieldPath = "createJobBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST , fieldList = {"departmentBean"}) ,
        @MyGraphIgnoreInfo( fieldPath = "createJobBean.departmentBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST) ,
        @MyGraphIgnoreInfo( fieldPath = "implementBeans.departmentBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST) ,
})
public class ProjectBean implements Serializable {

    public  ProjectBean () {
        super();
    }

    /**
     * 项目Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer	projectId;

    /**
     * 项目编号
     */
    private String	projectNum;

    /**
     * 项目名称
     */
    private String	projectName;

    /**
     * 立项时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date projectApprovalTime;

    /**
     * 项目所属类型
     */
    private String projectBelongsType;

    /**
     * 经营类型
     */
    private String	projectManagementType;

    /**
     * 项目地点
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_location_id", referencedColumnName = "projectLocationId")
    private ProjectLocationBean projectLocationBean;

    /**
     * 甲方名称
     */
    private String	projectFirstPartyName;

    /**
     * 工作内容
     */
    private String	projectWorkContent;

    /**
     * 收益单位
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_earnings_company_id", referencedColumnName = "earningsCompanyId")
    private EarningsCompanyBean earningsCompanyBean;

    /**
     * 报价Bean
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_quote_id", referencedColumnName = "quoteId")
    private QuoteBean quoteBean;

    /**
     * 跟合同一对一
     */
    @OneToOne(mappedBy = "projectBean" ,fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"projectBean"})
    private ContractBean contractBean;

    /**
     * 跟经营管理一对一
     */
    @OneToOne(mappedBy = "projectBean", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"projectBean"})
    private ManagementBean managementBean;

    /**
     * 项目跟实施一对多
     */
    @OneToMany(mappedBy = "projectBean", fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"projectBean","createJobBean",",createUserBean"})
    private List<ImplementBean> implementBeans = new ArrayList<>();


    /**
     * 创建人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_create_user_id", referencedColumnName = "userId")
    @JsonIgnoreProperties(value = {"roleBeans" , "permissionBeans" ,"jobBean"})
    private UserBean createUserBean;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date projectCreateTime;

    /**
     * 数据职位 职位Bean
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_job_id", referencedColumnName = "jobId")
    @JsonIgnoreProperties(value = {"userBeans" , "permissionBeans" ,"departmentBean"})
    private JobBean createJobBean;



}
