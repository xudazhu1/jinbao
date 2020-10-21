package com.hnjbkc.jinbao.flow.flownotice;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnjbkc.jinbao.flow.flownode.FlowNodeBean;
import com.hnjbkc.jinbao.flow.flownode.flownodemiddle.FlowNodeMiddleBean;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * 流程通知pojo类
 *
 * @author xudaz
 * @date 2019/7/9
 */
@Entity
@Table(name = "flow_notice")
@Setter
@Getter
public class FlowNoticeBean implements Serializable {

    /**
     * 自增主键Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer flowNoticeId;


    /**
     * 通知发起人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flow_notice_initiator_id", referencedColumnName = "userId")
    private UserBean userBean;
    /**
     * 通知发起时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date flowNoticeTime;

    /**
     * 所属节点(不为空为通知)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flow_notice_flow_node_id", referencedColumnName = "FlowNodeId")
    private FlowNodeBean flowNodeBean;

    /**
     *所属走向(不为空的话是待办)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flow_notice_flow_node_middle_id" , referencedColumnName = "flowNodeMiddleId")
    private FlowNodeMiddleBean flowNodeMiddleBean;

    /**
     * 对象(被通知(待办)人)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flow_notice_object", referencedColumnName = "userId")
    @JsonIgnoreProperties({"jobBean" , "roleBeans"})
    private UserBean objectUserBean;

    /**
     * 通知内容
     */
    private String flowNoticeContent;

    /**
     * 通知状态 ( 待办 | 已办 || 已读 , 未读 )
     */
    private String flowNoticeStatus;

    /**
     * 通知标识符
     */
    private Integer flowNoticeTag;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FlowNoticeBean that = (FlowNoticeBean) o;
        return Objects.equals(flowNoticeId, that.flowNoticeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flowNoticeId);
    }
}
