package com.hnjbkc.jinbao.invoiceandmoneyback.income.incomeimplmoney;

import com.hnjbkc.jinbao.utils.layuisoultable.SoulPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xudaz
 */
@RestController
@RequestMapping("income_impl_money")
public class IncomeImplMoneyController {


    private IncomeImplMoneyServiceImpl incomeImplMoneyService;

    @Autowired
    public void setIncomeImplMoneyService(IncomeImplMoneyServiceImpl incomeImplMoneyService) {
        this.incomeImplMoneyService = incomeImplMoneyService;
    }

    @GetMapping("4layui_table")
    public Object get4layuiTable(SoulPage soulPage , String filterSos ) throws Exception {
        return incomeImplMoneyService.get4layuiTable( soulPage , filterSos  );
    }

    @GetMapping("info_by_incomeId")
    public Object getInfoByIncomeId(Integer incomeId ) {
        return incomeImplMoneyService.getInfoByIncomeId( incomeId );
    }

    /**
     * 自动分配单个实施部的回款金额
     * @return true | false
     */
    @GetMapping("flush")
    public Object flushImplMoney() {
        return incomeImplMoneyService.flushImplMoney();
    }

}
