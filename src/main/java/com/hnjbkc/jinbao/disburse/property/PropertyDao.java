package com.hnjbkc.jinbao.disburse.property;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author siliqiang
 * @date 2019.9.19
 */
public interface PropertyDao extends JpaRepository<PropertyBean,Integer> {

//    @Query(value = "select disburseBean.disburseNum , propertyBean. ")
//    Page<Object[]> findAllByHQL(Pageable pageable);

    /**
     * 为资产折旧费去除一对多关系
     * @param roleId id
     */
    @Modifying
    @Query(value = "delete from disburse where disburse_property_id=?1" , nativeQuery = true)
    void  deletePropertyId(Integer roleId);
}
