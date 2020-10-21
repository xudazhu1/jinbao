package com.hnjbkc.jinbao.contract.departmentmoney;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface ContractDepartmentMoneyDao extends JpaRepository<ContractDepartmentMoneyBean,Integer> {
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM contract_department_money WHERE contract_department_money.contract_department_money_contract_id=?1",nativeQuery = true)
    Integer deleteDepartmentMoney(Integer id);
}
