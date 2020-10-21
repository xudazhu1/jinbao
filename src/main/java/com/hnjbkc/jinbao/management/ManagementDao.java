package com.hnjbkc.jinbao.management;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.table.TableRowSorter;
import java.util.List;

/**
 * @author xudaz
 */
@Repository
public interface ManagementDao extends JpaRepository<ManagementBean, Integer> {

    /**
     * 根据项目编号分组拿到,审计 财评 预算金额
     *
     * @return
     */
    @Query(value = "SELECT p.project_num, m.management_audit_amount, m.management_goods_evaluation_amount, m.management_settlement_amount FROM management m\n" +
            "LEFT OUTER JOIN project p on p.project_id=m.management_project_id" , nativeQuery = true)
    List<Object[]> getManagementMoney();
}
