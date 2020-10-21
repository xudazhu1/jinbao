package com.hnjbkc.jinbao.flow;

import com.hnjbkc.jinbao.utils.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author 12
 */
@RestController
@RequestMapping("flow")
public class FlowController {

    private FlowServiceImpl flowService;

    @Autowired
    public void setFlowService(FlowServiceImpl flowService) {
        this.flowService = flowService;
    }

    @PostMapping
    public FlowBean addFlow(FlowBean flowBean, HttpSession session){
        return flowService.add(flowBean, session);
    }
    @DeleteMapping
    public boolean deleteFlow(Integer id){
        return flowService.delete(id);
    }

    @PutMapping
    public boolean updateFlow(FlowBean flowBean){
        return flowService.update(flowBean)!= null;
    }

    /**
     * 查询并且分页的方法
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    @GetMapping()
    public Page get(@RequestParam Map<String, Object> propMap) {
        return flowService.get(propMap, PageableUtils.producePageable4Map(propMap, "flowId"));
    }


}
