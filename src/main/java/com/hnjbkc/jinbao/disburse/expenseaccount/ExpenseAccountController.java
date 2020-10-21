package com.hnjbkc.jinbao.disburse.expenseaccount;

import com.hnjbkc.jinbao.utils.GetNetworkTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author siliqiang
 * @date 2019..8.29
 */
@RestController
@RequestMapping("expense_account")
public class ExpenseAccountController {
    private ExpenseAccountServiceImpl expenseAccountService;

    @Autowired
    public void setExpenseAccountService(ExpenseAccountServiceImpl expenseAccountService) {
        this.expenseAccountService = expenseAccountService;
    }

    @PutMapping
    public ExpenseAccountBean put(ExpenseAccountBean expenseAccountBean){
    return expenseAccountService.add(expenseAccountBean);
    }
    @PostMapping
    public ExpenseAccountBean addExpenseAccount(ExpenseAccountBean expenseAccountBean) {
        return put(expenseAccountBean);
    }

    @GetMapping("print")
    public Map<String, List<Object[]>> getPrint(Integer expenseAccountId) {
        return expenseAccountService.getPrint(expenseAccountId);
    }


    @GetMapping("get_time")
    public String getNetWorkTime(){
        //获取当前网络时间,百度的时间
        String webUrl="http://www.baidu.com";
        return GetNetworkTime.getNetworkTime(webUrl);

    }

    @GetMapping("statistics4month_and_user")
    public Object getStatistics4MonthAndUser(String userName , String date ){
        return expenseAccountService.getStatistics4MonthAndUser( userName , date );
    }


    @PutMapping("obsolete")
    public ExpenseAccountBean putObsolete(ExpenseAccountBean expenseAccountBean){
      return   expenseAccountService.obsolete(expenseAccountBean);
    }

}
