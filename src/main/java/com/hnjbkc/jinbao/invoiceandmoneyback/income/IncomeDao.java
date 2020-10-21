package com.hnjbkc.jinbao.invoiceandmoneyback.income;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author siliqiang
 * @date 2019.10.04
 */
public interface IncomeDao extends JpaRepository<IncomeBean, Integer> {

    /**
     * 预估金额模块使用的
     *
     * @return
     */
    @Query(value = "SELECT p.project_num,inc.income_money FROM income inc \n" +
            "LEFT OUTER JOIN project p ON inc.income_project_id=p.project_id WHERE p.project_num is NOT NULL", nativeQuery = true)
    List<Object[]> getIncomeMoney();



}
