package com.hnjbkc.jinbao.disburse.finance;

import com.hnjbkc.jinbao.disburse.DisburseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author siliqiang
 * @date 2019.9.2
 */
@RestController
@RequestMapping("finance")
public class FinanceController {
    private FinanceServiceImpl financeService;

    @Autowired
    public void setFinanceService(FinanceServiceImpl financeService) {
        this.financeService = financeService;
    }

    @PostMapping
    public DisburseBean addFinance(DisburseBean disburseBean) {
        return update(disburseBean);
    }

    @PutMapping
    public DisburseBean update(DisburseBean disburseBean) {
        return financeService.add(disburseBean);
    }
}
