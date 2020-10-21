package com.hnjbkc.jinbao.contract.material;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

/**
 * @author siliqiang
 * @date 2019.4.19
 */
public interface MaterialDao extends JpaRepository<MaterialBean,Integer> {
    @Query(value = "select contract_accessory_old_name,contract_accessory_new_name from contract c\n" +
            "left join contract_accessory ca\n" +
            "on c.contract_id = ca.contract_accessory_contract_id\n" +
            "where c.contract_id = ?1",nativeQuery = true)
    List<Map<String,String>> findMaterialList(Integer contId);
}
