package com.hnjbkc.jinbao.businesscommission;

import com.hnjbkc.jinbao.utils.layuisoultable.SoulPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 业务提成
 * @author xudaz
 */
@RestController
@RequestMapping("business_commission")
public class BusinessCommissionController {

    private BusinessCommissionService businessCommissionService;

    @Autowired
    public void setBusinessCommissionService(BusinessCommissionService businessCommissionService) {
        this.businessCommissionService = businessCommissionService;
    }

    @PostMapping("all")
    public Object getBusinessCommission(SoulPage soulPage, String filterSos  ) {
        return businessCommissionService.getBusinessCommission( soulPage ,filterSos );
    }


}
