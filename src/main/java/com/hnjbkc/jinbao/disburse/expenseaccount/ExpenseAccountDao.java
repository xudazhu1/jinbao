package com.hnjbkc.jinbao.disburse.expenseaccount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author siliqiang
 * @date 2019.8.29
 */
public interface ExpenseAccountDao extends JpaRepository<ExpenseAccountBean, Integer> {



    @Query(value = "SELECT e.expense_account_num, d.disburse_num,\n" +
            "ear.earnings_company_name,\n" +
            "detail.disburse_detail_name,\n" +
            "SUM(d.disburse_payment_amount),\n" +
            "SUM(d.disburse_invoice_money),\n" +
            "project_num,\n" +
            "project_name\n" +
            "FROM expense_account e\n" +
            "LEFT OUTER JOIN disburse d ON e.expense_account_id=d.disburse_expense_account_id\n" +
            "LEFT OUTER JOIN disburse_detail detail ON detail.disburse_detail_id=d.disburse_disburse_detail_id\n" +
            "LEFT OUTER JOIN earnings_company ear ON ear.earnings_company_id=d.disburse_earnings_company_id\n" +
            "LEFT OUTER JOIN implement ON implement_id=d.disburse_implement_id\n" +
            "LEFT OUTER JOIN project ON project_id=implement_project_id\n" +
            "WHERE e.expense_account_id=?1\n" +
            "GROUP BY project_name, ear.earnings_company_name,\n" +
            "         detail.disburse_detail_name", nativeQuery = true)
    List<Object[]> getPrint(Integer id);

    /**
     * 获取报销打印类型为项目的报销金额统计数据
     *
     * @param id 前端传过来对应的id
     * @return 返回的是一个
     */
    @Query(value = "SELECT e.expense_account_num, d.disburse_num,\n" +
            "            ear.earnings_company_name,\n" +
            "            detail.disburse_detail_name,\n" +
            "            SUM(d.disburse_payment_amount),\n" +
            "            SUM(d.disburse_invoice_money),\n" +
            "            project_num,\n" +
            "            project_name\n" +
            "            FROM expense_account e\n" +
            "            LEFT OUTER JOIN disburse d ON e.expense_account_id=d.disburse_expense_account_id\n" +
            "            LEFT OUTER JOIN disburse_detail detail ON detail.disburse_detail_id=d.disburse_disburse_detail_id\n" +
            "            LEFT OUTER JOIN earnings_company ear ON ear.earnings_company_id=d.disburse_earnings_company_id\n" +
            "            LEFT OUTER JOIN implement ON implement_id=d.disburse_implement_id\n" +
            "            LEFT OUTER JOIN project ON project_id=implement_project_id\n" +
            "            WHERE e.expense_account_id=?1\n" +
            "            GROUP BY project_name, ear.earnings_company_name", nativeQuery = true)
    List<Object[]> getProjectPrint(Integer id);

}
