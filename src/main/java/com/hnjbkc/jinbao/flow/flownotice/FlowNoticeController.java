package com.hnjbkc.jinbao.flow.flownotice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author 12
 */
@RestController
@RequestMapping("flow_notice")
public class FlowNoticeController {

    private FlowNoticeServiceImpl flowNoticeService;

    @Autowired
    public void setFlowNoticeService(FlowNoticeServiceImpl flowNoticeService) {
        this.flowNoticeService = flowNoticeService;
    }

    @PostMapping
    public boolean addFlowNotice(FlowNoticeBean flowNoticeBean){
        return flowNoticeService.add(flowNoticeBean) != null ;
    }

    @GetMapping
    public Page<FlowNoticeBean> get(@RequestParam Map<String, Object> propMap , HttpServletRequest request) {
        return flowNoticeService.get(propMap , request);
    }
    @GetMapping("num")
    public Integer getAllNum4Me( HttpSession session) {
        return flowNoticeService.getAllNum4Me( session);
    }
}
