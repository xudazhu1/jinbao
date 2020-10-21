package com.hnjbkc.jinbao.flow.flownode;


import com.hnjbkc.jinbao.flow.flownotice.FlowNoticeBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author 12
 */
@RestController
@RequestMapping("flow_node")
public class FlowNodeController {

    private FlowNodeServiceImpl flowSubService;

    @Autowired
    public void setFlowSubService(FlowNodeServiceImpl flowSubService) {
        this.flowSubService = flowSubService;
    }

    @PostMapping
    public boolean addFlowSub(FlowNodeBean flowNodeBean, Integer upFlowSubId){
        return flowSubService.add(flowNodeBean, upFlowSubId);
    }

    @DeleteMapping
    public boolean deleteFlowSub(Integer id){
        return flowSubService.delete(id);
    }

    @PutMapping
    public boolean updateFlowSub(FlowNodeBean flowNodeBean){
        return flowSubService.update(flowNodeBean) != null ;
    }


    @GetMapping("match")
    public List<FlowNodeBean> matchFlowSub(HttpServletRequest request ) {
        return flowSubService.match( request );
    }
    @GetMapping("match_todo")
    public List<FlowNoticeBean> matchFlowNotice(Integer tagId , HttpServletRequest request ) {
        return flowSubService.matchTODO(tagId ,  request );
    }


    /**
     * 获取该页面下所有属性key 和属性中文备注
     * (例 : showPageTag = 页面合同
     * return  项目编号 = projectBean.projectNum
     *             项目名称 = projectBean.projectName)
     * @param showPageTag 页面路径
     * @return map
     */
    @GetMapping("keys")
    public Map<String , String> getKeys4ShowPage(String showPageTag ) {
        return  flowSubService.getKeys4ShowPage(showPageTag);
    }


    /**
     * 根据当前流程 和页面自己所绑定的外键 获取当前已存在的数据
     * @param flowNodeId 当前流程Id
     * @param parentKey 页面外键name值(key)
     * @param parentValue 页面外键值 Id
     * @return 返回封装好的以当前页面为主体的json数据
     */
    @GetMapping("match_data")
    public String getData4FlowNode(Integer flowNodeId  ,  String parentKey , String parentValue ){
        return flowSubService.getData4FlowNode(flowNodeId , parentKey , parentValue);
    }

    @GetMapping("data_temp")
    public Object getDataByIdAndReferer(Integer id  , HttpServletRequest request ) {
        return flowSubService.getDataByIdAndReferer(id , request);
    }


}
