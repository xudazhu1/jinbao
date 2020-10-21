package com.hnjbkc.jinbao.flow.flownode.flownodemiddle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.flow.flownode.FlowNodeBean;
import com.hnjbkc.jinbao.flow.flownode.customizenotificationconditions.CustomizeNotificationConditionsBean;
import com.hnjbkc.jinbao.hqldao.annotations.HasOneToManyList;
import com.hnjbkc.jinbao.hqldao.annotations.OneToManyListInfo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * 流程走向表
 * @author xudaz
 */
@Entity
@Table(name = "flow_node_middle")
@Getter
@Setter
@HasOneToManyList( hasClasses = {
        @OneToManyListInfo( propertyName =  "customizeNotificationConditionsBeans" , propertyClass = CustomizeNotificationConditionsBean.class ) ,
} )
public class FlowNodeMiddleBean implements Serializable {

    /**
     * 流程走向表Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Integer flowNodeMiddleId;

    /**
     * 流程(本)
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "flow_node_middle_flow_node_self_id" , referencedColumnName = "flowNodeId")
    @JsonIgnoreProperties({"flowNodeMiddleNextBeans"})
    private FlowNodeBean flowNodeSelfBean;

    /**
     * 流程(本)标识符路径
     */
    private String flowNodeMiddleFlowNodeSelfTagPath;

    /**
     * 流程(下)
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "flow_node_middle_flow_node_next_id" , referencedColumnName = "flowNodeId")
    @JsonIgnoreProperties({"flowNodeMiddleBeforeBeans"})
    private FlowNodeBean flowNodeNextBean;

    /**
     * 流程(下)标识符路径
     */
    private String flowNodeMiddleFlowNodeNextTagPath;

    /**
     * 自定义待办条件
     */
    @OneToMany(mappedBy = "flowNodeMiddleBean" , fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"flowNodeMiddleBean" , "flowNodeBean"})
    private List<CustomizeNotificationConditionsBean> customizeNotificationConditionsBeans;





}
