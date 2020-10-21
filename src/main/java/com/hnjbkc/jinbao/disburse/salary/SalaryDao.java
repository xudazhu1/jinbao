package com.hnjbkc.jinbao.disburse.salary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 12
 * @Date 2019-10-03
 */
@Repository
public interface SalaryDao extends JpaRepository<SalaryBean,Integer> {

}