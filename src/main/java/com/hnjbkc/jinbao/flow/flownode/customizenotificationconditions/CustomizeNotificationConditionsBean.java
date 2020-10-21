package com.hnjbkc.jinbao.flow.flownode.customizenotificationconditions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnore;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfo;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfoType;
import com.hnjbkc.jinbao.flow.flownode.FlowNodeBean;
import com.hnjbkc.jinbao.flow.flownode.flownodemiddle.FlowNodeMiddleBean;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author xudaz
 * @date 节点的自定义条件的pojo类
 */
@Entity
@Table(name = "customize_notification_conditions")
@Setter
@Getter
@MyGraphIgnore( ignoreFields = {
        @MyGraphIgnoreInfo(fieldPath = "flowNodeBean") ,
        @MyGraphIgnoreInfo(fieldPath = "flowNodeMiddleBean") ,
        @MyGraphIgnoreInfo(fieldPath = "userBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST) ,
        @MyGraphIgnoreInfo(fieldPath = "flowNodeBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST) ,
        @MyGraphIgnoreInfo(fieldPath = "flowNodeMiddleBean" , fetchType = MyGraphIgnoreInfoType.WHITE_LIST) ,
})
public class CustomizeNotificationConditionsBean implements Serializable {

    /**
     * 自增主键Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer customizeNotificationConditionsId;

    /**
     * 自定义通知条件中文名
     */
    private String customizeNotificationConditionsName;
    /**
     * 自定义通知条件key(JAVA Bean路径 也就是name值)
     */
    private String customizeNotificationConditionsKey;
    /**
     * 自定义通知条件的值
     */
    private String customizeNotificationConditionsValue;

    /**
     *自定义通知条件的人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customize_notification_conditions_user_id" , referencedColumnName = "userId")
    @JsonIgnoreProperties({"roleBeans" , "jobBean"})
    private UserBean userBean;

    /**
     *自定义通知条件所属节点(不为空的话是通知)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customize_notification_conditions_flow_node_id" , referencedColumnName = "flowNodeId")
    private FlowNodeBean flowNodeBean;

    /**
     *自定义通知条件所属走向(不为空的话是待办)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customize_notification_conditions_flow_node_middle_id" , referencedColumnName = "flowNodeMiddleId")
    private FlowNodeMiddleBean flowNodeMiddleBean;

}
