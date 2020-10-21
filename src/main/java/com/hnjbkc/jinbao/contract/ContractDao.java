package com.hnjbkc.jinbao.contract;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author siliqiang
 * @date 2019.8.12
 */
public interface ContractDao extends JpaRepository<ContractBean, Integer> {

    @Query(value = "SELECT p.project_num,dept.department_name,c1.contract_department_money_distribution_money,c.contract_money,impl.implement_progress FROM contract c\n" +
            "LEFT OUTER JOIN contract_department_money c1 ON c.contract_id=c1.contract_department_money_contract_id\n" +
            "LEFT OUTER JOIN implement impl ON c1.contract_department_money_implement_department_id=impl.implement_id\n" +
            "LEFT OUTER JOIN department dept ON  dept.department_id=impl.implement_department_id\n" +
            " LEFT OUTER JOIN project p ON p.project_id=c.contract_project_id", nativeQuery = true)
    List<Object[]> getContractMoney();


    /**
     * 跟新扫描件为有
     * @return
     */
    @Transactional
    @Modifying
    @Query(value = "UPDATE contract,contract_accessory SET contract_scanned_exists ='无' WHERE contract_id !=contract_accessory.contract_accessory_contract_id", nativeQuery = true)
    Integer updateMaterialNo();
    /**
     * 跟新扫描件为有
     * @return
     */
    @Transactional
    @Modifying
    @Query(value = "UPDATE contract,contract_accessory SET contract_scanned_exists ='有' WHERE contract_id =contract_accessory.contract_accessory_contract_id", nativeQuery = true)
    Integer updateMaterial();

}
