//package com.hnjbkc.jinbao.flow.flownotice.flownoticemiddle;
//
//import com.hnjbkc.jinbao.flow.flownotice.FlowNoticeBean;
//import com.hnjbkc.jinbao.flow.flownode.FlowNodeBean;
//import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
//import lombok.Getter;
//import lombok.Setter;
//
//import javax.persistence.*;
//import java.util.Objects;
//
///**
// * 记录被通知人和一些信息的pojo类
// *
// * @author xudaz
// * @date 2019/7/9
// */
//@Entity
//@Table(name = "flow_notice_middle")
//@Setter
//@Getter
//@NamedEntityGraph(name = "flowNoticeMiddle.graph", attributeNodes = {
//        @NamedAttributeNode(value = "flowNoticeBean", subgraph = "flowNotice.graph"),
//        @NamedAttributeNode("objectUserBean"),
//        @NamedAttributeNode(value = "nextFlowNodeBean", subgraph = "FlowNodeBean")
//}, subgraphs = {
//        @NamedSubgraph(name = "flowNotice.graph", attributeNodes = {
//                @NamedAttributeNode("userBean"),
//                @NamedAttributeNode(value = "nextFlowNodeBean", subgraph = "flowSub.graph")
//        }),
//        @NamedSubgraph(name = "FlowNodeBean", attributeNodes = {
//                @NamedAttributeNode(value = "showPageBean"),
//                @NamedAttributeNode(value = "editPageBean"),
//                @NamedAttributeNode(value = "flowBean")
//        })
//})
//public class FlowNoticeMiddleBean {
//
//    /**
//     * 自增主键Id
//     */
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer flowNoticeMiddleId;
//
//    /**
//     * 所对应的通知
//     */
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "flow_notice_middle_flow_notice_id", referencedColumnName = "flowNoticeId")
//    private FlowNoticeBean flowNoticeBean;
//
//
//
//    /**
//     * 下个流程 待办 (被通知人需要跳转到的流程 页面)
//     */
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "flow_notice_middle_next_flow_sub_id", referencedColumnName = "flowSubId")
//    private FlowNodeBean nextFlowNodeBean;
//
//
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (o == null || getClass() != o.getClass()) {
//            return false;
//        }
//        FlowNoticeMiddleBean that = (FlowNoticeMiddleBean) o;
//        return Objects.equals(flowNoticeMiddleId, that.flowNoticeMiddleId);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(flowNoticeMiddleId);
//    }
//}
