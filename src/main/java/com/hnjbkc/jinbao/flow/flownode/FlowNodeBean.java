package com.hnjbkc.jinbao.flow.flownode;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.flow.FlowBean;
import com.hnjbkc.jinbao.flow.flownode.customizenotificationconditions.CustomizeNotificationConditionsBean;
import com.hnjbkc.jinbao.flow.flownode.flownodemiddle.FlowNodeMiddleBean;
import com.hnjbkc.jinbao.hqldao.annotations.HasOneToManyList;
import com.hnjbkc.jinbao.hqldao.annotations.OneToManyListInfo;
import com.hnjbkc.jinbao.organizationalstructure.job.JobBean;
import com.hnjbkc.jinbao.organizationalstructure.role.RoleBean;
import com.hnjbkc.jinbao.permission.PermissionBean;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * 流程节点pojo类
 *
 * @author xudaz
 * @date 2019/7/9
 */
@Entity
@Table(name = "flow_node")
@Setter
@Getter
@HasOneToManyList( hasClasses = {
        @OneToManyListInfo( propertyName =  "flowNodeMiddleNextBeans" , propertyClass = FlowNodeMiddleBean.class ) ,
        @OneToManyListInfo( propertyName =  "flowNodeMiddleBeforeBeans" , propertyClass = FlowNodeMiddleBean.class ) ,
        @OneToManyListInfo( propertyName =  "customizeNotificationConditionsBeans" , propertyClass = CustomizeNotificationConditionsBean.class ) ,
} )
public class FlowNodeBean implements Serializable {

    /**
     * 流程节点自增主键Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer flowNodeId;
    /**
     * 流程节点名称
     */
    private String flowNodeName;
    /**
     * 流程节点描述
     */
    private String flowNodeDescribe;

    /**
     * 流程节点所对应的展示页面
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flow_node_show_page", referencedColumnName = "permissionId")
    private PermissionBean showPageBean;

    /**
     * 流程节点所对应的编辑页面
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flow_node_edit_page", referencedColumnName = "permissionId")
    private PermissionBean editPageBean;

    /**
     * 流程节点所对应的主流程
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flow_node_flow_id", referencedColumnName = "flowId")
    @JsonIgnoreProperties({"flowNodeBeans"})
    private FlowBean flowBean;

    /**
     * 流程走向下
     */
    @OneToMany( mappedBy = "flowNodeSelfBean" , fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"flowNodeSelfBean"})
    private List<FlowNodeMiddleBean> flowNodeMiddleNextBeans;

    /**
     * 流程走向上
     */
    @OneToMany( mappedBy = "flowNodeNextBean" , fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"flowNodeNextBean"})
    private List<FlowNodeMiddleBean> flowNodeMiddleBeforeBeans;

    /**
     * 有此流程操作权限的角色集合 多对多
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "flow_node_permission",
            joinColumns = {@JoinColumn(name = "flow_sub_permission_flow_sub_id", referencedColumnName = "flowNodeId")},
            inverseJoinColumns = {@JoinColumn(name = "flow_sub_permission_role_id", referencedColumnName = "roleId")})
    private List<RoleBean> roleBeans;

    /**
     * 有此流程操作权限的职位集合 多对多
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "flow_node_permission",
            joinColumns = {@JoinColumn(name = "flow_sub_permission_flow_sub_id", referencedColumnName = "flowNodeId")},
            inverseJoinColumns = {@JoinColumn(name = "flow_sub_permission_job_id", referencedColumnName = "jobId")})
    private List<JobBean> jobBeans;

    /**
     * 自定义通知条件
     */
    @OneToMany(mappedBy = "flowNodeBean"  , fetch = FetchType.LAZY )
    @JsonIgnoreProperties({"flowNodeMiddleBean" , "flowNodeBean"})
    private List<CustomizeNotificationConditionsBean> customizeNotificationConditionsBeans;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FlowNodeBean that = (FlowNodeBean) o;
        return Objects.equals(flowNodeId, that.flowNodeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flowNodeId);
    }
}
