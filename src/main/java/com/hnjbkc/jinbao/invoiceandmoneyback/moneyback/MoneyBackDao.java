package com.hnjbkc.jinbao.invoiceandmoneyback.moneyback;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author siliqiang
 * @date 2019.9.26
 */
public interface MoneyBackDao extends JpaRepository<MoneyBackBean, Integer> {

    /**
     * 银行卡的收入总和(即收入)
     * 条件是已通过状态即income的状态为1
     * @return 数组的集合
     */
    @Query(value = "SELECT SUM(moneyBack.money_back_money),bank.bank_card_id ,bank.bank_card_name FROM money_back AS moneyBack\n" +
            "            LEFT OUTER JOIN bank_card AS bank ON bank.bank_card_id=moneyBack.money_back_bank_card_id \n" +
            "            LEFT OUTER JOIN income income ON income.income_id=money_back_income_id WHERE income.income_audit_status='1'\n" +
            "            GROUP BY bank.bank_card_id ", nativeQuery = true)
    List<Object[]> getMoneyBackBean();

    /**
     * 统计报销总表的数据
     *
     * @return
     */
    @Query(value = "SELECT date_format(moneyBack.money_back_date , '%Y-%m') as dateA,ea.earnings_company_name,SUM(moneyBack.money_back_money)\n" +
            "FROM money_back moneyBack \n" +
            "LEFT OUTER JOIN income inc ON inc.income_id=moneyBack.money_back_income_id\n" +
            "LEFT OUTER JOIN earnings_company ea ON ea.earnings_company_id=inc.income_earnings_company_id\n" +
            "WHERE inc.income_audit_status=\"1\"\n" +
            "GROUP BY dateA DESC, ea.earnings_company_name", nativeQuery = true)
    List<Object[]> getStatistics();

    /**
     * 统计每个月的收支明细
     * @param date
     * @return
     */
    @Query(value = "SELECT date_format( m.money_back_date , '%Y-%m') as dateA,\n" +
            "e.earnings_company_name ,left(i.income_type,4),SUM(m.money_back_money) FROM money_back m\n" +
            "LEFT OUTER JOIN income i on i.income_id=m.money_back_income_id\n" +
            "LEFT OUTER JOIN earnings_company e ON e.earnings_company_id=i.income_earnings_company_id\n" +
            "WHERE i.income_audit_status=1 AND date_format( m.money_back_date , '%Y-%m')=?1 \n" +
            "GROUP BY  dateA DESC, e.earnings_company_name ", nativeQuery = true)
    List<Object[]> getMonty(String date);
}
