//package com.hnjbkc.jinbao.flow.flownotice.flownoticemiddle;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * @author 12
// */
//@RestController
//@RequestMapping("flow_notice_middle")
//public class FlowNoticeMiddleController {
//
//    private FlowNoticeMiddleServiceImpl flowNoticeMiddleService;
//
//    @Autowired
//    public void setFlowNoticeMiddleService(FlowNoticeMiddleServiceImpl flowNoticeMiddleService) {
//        this.flowNoticeMiddleService = flowNoticeMiddleService;
//    }
//
//    @PostMapping
//    public boolean add(FlowNoticeMiddleBean flowNoticeMiddleBean){
//        return flowNoticeMiddleService.add(flowNoticeMiddleBean) != null ;
//    }
//}
