package com.hnjbkc.jinbao.flow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnore;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfo;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfoType;
import com.hnjbkc.jinbao.flow.flownode.FlowNodeBean;
import com.hnjbkc.jinbao.organizationalstructure.company.CompanyBean;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 流程pojo类
 *
 * @author xudaz
 * @date 2019/7/9
 */
@Entity
@Table(name = "flow")
@Setter
@Getter
@MyGraphIgnore( ignoreFields = {
        @MyGraphIgnoreInfo( fieldPath = "userBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST) ,
})
public class FlowBean implements Serializable {

    /**
     * 自增主键Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer flowId;

    /**
     * 流程名称
     */
    private String flowName;

    /**
     * 流程创建用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flow_create_user_id", referencedColumnName = "userId")
    @JsonIgnoreProperties(value = {"roleBeans" , "jobBean"} )
    private UserBean userBean;


    @OneToMany(mappedBy = "flowBean", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<FlowNodeBean> flowNodeBeans = new ArrayList<>();


    /**
     * 所有公司 多对多
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "flow_company_middle" ,
            joinColumns = {@JoinColumn(name = "flow_company_middle_flow_id" , referencedColumnName = "flowId")} ,
            inverseJoinColumns = {@JoinColumn(name = "flow_company_middle_company_id" ,referencedColumnName = "companyId")})
    @JsonIgnoreProperties(value = {"permissionBeans" , "departmentBeans"})
    private List<CompanyBean> companyBeans;

}
