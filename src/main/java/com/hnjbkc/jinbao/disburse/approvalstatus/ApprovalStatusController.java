package com.hnjbkc.jinbao.disburse.approvalstatus;

import com.hnjbkc.jinbao.utils.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author siliqiang
 * @date 2019.9.16
 */
@RequestMapping("approval_status")
@RestController
public class ApprovalStatusController {
    private ApprovalStatusServiceImpl approvalStatusService;

    @Autowired
    public void setApprovalStatusService(ApprovalStatusServiceImpl approvalStatusService) {
        this.approvalStatusService = approvalStatusService;
    }
    /**
     * 查询并且分页的方法(不模糊查询)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    @GetMapping
    public Page getApprovalStatus(@RequestParam Map<String, Object> propMap) {
        return approvalStatusService.get(propMap, PageableUtils.producePageable4Map(propMap, "approvalStatusId"));
    }
}
