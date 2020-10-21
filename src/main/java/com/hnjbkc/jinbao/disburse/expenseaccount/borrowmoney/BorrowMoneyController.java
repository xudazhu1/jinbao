package com.hnjbkc.jinbao.disburse.expenseaccount.borrowmoney;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author siliqiang
 * @date 2019/10/2
 */
@RestController
@RequestMapping("borrow_money")
public class BorrowMoneyController {
    private BorrowMoneyServiceImpl borrowMoneyService;

    @PutMapping
    public BorrowMoneyBean put(BorrowMoneyBean borrowMoneyBean){
        return borrowMoneyService.add(borrowMoneyBean);
    }


    @PostMapping
    public BorrowMoneyBean add(BorrowMoneyBean borrowMoneyBean){
     return put(borrowMoneyBean);
    }

    @Autowired
    public void setBorrowMoneyService(BorrowMoneyServiceImpl borrowMoneyService) {
        this.borrowMoneyService = borrowMoneyService;
    }

    @GetMapping
    public Map<String, Map<String, Object[]>> getMoney(String name) {
        return borrowMoneyService.getBorrowMoney(name);
    }
}
