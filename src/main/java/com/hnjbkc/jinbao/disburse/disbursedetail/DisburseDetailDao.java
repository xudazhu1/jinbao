package com.hnjbkc.jinbao.disburse.disbursedetail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author siliqiang
 * @date 2019.8.27
 */

public interface DisburseDetailDao extends JpaRepository<DisburseDetailBean, Integer> {

    /**
     * 查询中类属于部门报销的所有
     *
     * @return 返回的是字符串
     */
    @Query(value = "SELECT detail.disburse_detail_id , detail.disburse_detail_name from disburse_detail detail \n" +
            "LEFT OUTER JOIN disburse_type type ON type.disburse_type_id=detail.disburse_detail_type_id\n" +
            "LEFT OUTER JOIN disburse_category cate on cate.disburse_category_id=type.disburse_type_category_id\n" +
            "WHERE cate.disburse_category_name=?1 AND detail.disburse_detail_source='报销单'",nativeQuery = true)
    List<Object[]> getDepartmentdetail(String string);
}
